package com.example.android.radiusassignment.filter;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.example.android.radiusassignment.data.AppRepository;
import com.example.android.radiusassignment.data.local.Exclusion;
import com.example.android.radiusassignment.data.local.Facility;
import com.example.android.radiusassignment.data.local.Option;
import com.example.android.radiusassignment.data.remote.BaseResponse;
import com.example.android.radiusassignment.interfaces.Constants;
import com.example.android.radiusassignment.utils.NoInternetException;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link FilterActivityFragment}), retrieves the data and updates the
 * UI as required.
 */
public class FilterPresenter implements FilterContract.Presenter {
    @NonNull
    private final AppRepository mAppRepository;

    @NonNull
    private final FilterContract.View mFilterView;

    @NonNull
    private CompositeDisposable mCompositeDisposable;

    @Nullable
    private BaseResponse mBaseResponse;

    public FilterPresenter(@NonNull AppRepository appRepository,
                           @NonNull FilterContract.View filterView) {
        mAppRepository = checkNotNull(appRepository, "App Repository cannot be null");
        mFilterView = checkNotNull(filterView, "Filter View cannot be null");

        mCompositeDisposable = new CompositeDisposable();
        mFilterView.setPresenter(this);
    }

    /**
     * Method called to subscribe to presenter
     */
    @Override
    public void subscribe() {
        loadData(true);
    }

    /**
     * Method called to unsubscribe to presenter
     */
    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }

    /**
     * @param showLoadingUI Pass in true to display loading screen in the UI
     */
    @Override
    public void loadData(final boolean showLoadingUI) {
        if (showLoadingUI) {
            mFilterView.setLoadingIndicator(true);
        }

        // clear all previous disposables
        mCompositeDisposable.clear();
        Disposable disposable = mAppRepository
                .getData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseResponse -> {
                    mFilterView.setLoadingIndicator(false);
                    processFilters(baseResponse);
                }, throwable -> {
                    mFilterView.setLoadingIndicator(false);
                    mFilterView.showEmptyView();
                    handleThrowable(throwable);
                });
        // add the current disposable
        mCompositeDisposable.add(disposable);
    }


    /**
     * Getter for BaseResponse object
     */
    @Nullable
    @Override
    public BaseResponse getBaseResponse() {
        return mBaseResponse;
    }

    /**
     * Method called to perform click action on the BaseResponse Instance and then notify the view.
     *
     * @param isSelected To show whether the item was already selected or not
     * @param facilityId Facility id associated with the clicked item
     * @param optionId   Option id associated with the clicked item
     */
    @Override
    public void itemClicked(boolean isSelected, @NonNull String facilityId, @NonNull String optionId) {
        if (mBaseResponse != null) {
            // select or unselect the chips
            Flowable<Boolean> selectChipFlowable = Flowable.fromIterable(mBaseResponse.getFacilityList())
                    .filter(facility -> facility != null && facility.getFacilityId() != null &&
                            facility.getFacilityId().equals(facilityId) && facility.getOptions() != null &&
                            !facility.getOptions().isEmpty())
                    .map(Facility::getOptions)
                    .flatMap(options -> {
                        List<Option> previousSelectedOptionsList = new ArrayList<>();
                        return Flowable.fromIterable(options)
                                .filter(option -> option != null && option.getId() != null)
                                .map(option -> {
                                    if (option.isSelected()) {
                                        previousSelectedOptionsList.add(option);
                                    }
                                    if (option.getId().equals(optionId)) {
                                        option.setSelected(!option.isSelected());
                                    } else {
                                        option.setSelected(false);
                                    }

                                    return previousSelectedOptionsList;
                                })
                                .last(new ArrayList<>())
                                .toFlowable()
                                .flatMap(Flowable::fromIterable)
                                // enable the previously excluded chips
                                .flatMap(option -> updateChipsByExclusionList(facilityId, option.getId(),
                                        false));
                    });

            // disable the excluded chips
            Flowable<Boolean> disableChipFlowable = (!isSelected) ? updateChipsByExclusionList(facilityId,
                    optionId, true) : Flowable.just(Boolean.TRUE);

            // Concat the observables and call showFacilities onComplete
            Disposable itemClickFlowable = Flowable.fromIterable(Lists.newArrayList(selectChipFlowable, disableChipFlowable))
                    .concatMap(booleanFlowable -> booleanFlowable)
                    .doOnComplete(mFilterView::showFacilities)
                    .subscribe();

            // add the disposable.
            mCompositeDisposable.add(itemClickFlowable);
        }
    }

    /**
     * It is used to update (enable or disable) chips based on previously or currently selected chips and exclusionList in BaseResponse object.
     * Gets the exclusion list for the facility id and option id present in parameters
     *
     * @param facilityId  Facility id of an item
     * @param optionId    Option id of an item
     * @param isDisabling It is used to decide whether to enable the chips or disable them
     * @return Flowable of type Boolean
     */
    private Flowable<Boolean> updateChipsByExclusionList(@NonNull String facilityId, @NonNull String optionId,
                                                         boolean isDisabling) {
        Flowable<List<Exclusion>> excludedItemListFlowable = getExclusionList(facilityId, optionId);
        if (!isDisabling) {
            excludedItemListFlowable = excludedItemListFlowable.flatMap(exclusionList1 -> checkForExistingSelectedChips(exclusionList1, facilityId, optionId));
        }
        return excludedItemListFlowable.flatMap(exclusions -> toggleChipsFlowable(exclusions, isDisabling));
    }

    /**
     * Performs check for finding out whether there are other selected chips that hold same exclusion item as of the
     * current one.
     *
     * @param exclusionList List of Exclusion for the clicked item
     * @param facilityId    Facility id of an item
     * @param optionId      Option id of an item
     * @return Flowable of type List<Exclusion> where Exclusion is a model
     * @see Exclusion
     */
    private Flowable<List<Exclusion>> checkForExistingSelectedChips(@NonNull List<Exclusion> exclusionList,
                                                                    @NonNull String facilityId,
                                                                    @NonNull String optionId) {
        List<Exclusion> newExclusionList = new ArrayList<>(exclusionList);
        if (mBaseResponse != null) {
            return Flowable.fromIterable(exclusionList)
                    // reverse lookup for other exclusions
                    .flatMap(currentExclusion -> performReverseLookup(newExclusionList, currentExclusion, facilityId, optionId))
                    .defaultIfEmpty(newExclusionList);
        } else {
            return Flowable.just(newExclusionList);
        }
    }

    /**
     * Perform reverse lookup for an exclusion to find out in exclusion list. Removes the currently
     * selected facilityId and optionsId. Calls checkForSelectedOptions {@link #checkForSelectedOptions(List, Exclusion, Exclusion)}
     * to find out whether reverse looked up exclusions are already selected or not.
     *
     * @param newExclusionList Exclusion list that needs to be updated
     * @param currentExclusion Exclusion object to perform reverse lookup
     * @param avoidFacilityId  Facility Id used to remove exclusion from reverse looked up list
     * @param avoidOptionId    Option Id used to remove exclusion from reverse looked up list
     * @return Flowable of updated list of Exclusion
     */
    private Flowable<List<Exclusion>> performReverseLookup(@NonNull List<Exclusion> newExclusionList, @NonNull Exclusion currentExclusion,
                                                           @NonNull String avoidFacilityId, @NonNull String avoidOptionId) {
        if (mBaseResponse != null) {
            return getExclusionList(currentExclusion.getFacilityId(), currentExclusion.getOptionsId())
                    .flatMapIterable(exclusionList -> exclusionList)
                    .filter(exclusion -> exclusion != null && exclusion.getOptionsId() != null && exclusion.getFacilityId() != null &&
                            !exclusion.getFacilityId().equals(avoidFacilityId) && !exclusion.getOptionsId().equals(avoidOptionId))
                    .toList()
                    .toFlowable()
                    // if other exclusions found
                    .flatMap(facilitiesExclusionList -> Flowable.fromIterable(facilitiesExclusionList)
                            // check for each facility exclusion being selected or not
                            .flatMap(facilityExclusion -> checkForSelectedOptions(newExclusionList, facilityExclusion, currentExclusion))
                            .defaultIfEmpty(newExclusionList))
                    .defaultIfEmpty(newExclusionList);
        } else {
            return Flowable.just(newExclusionList);
        }
    }

    /**
     * Checks for selected options in BaseResponse facility list {@link BaseResponse#facilityList}
     * and remove items from list of exclusion when options are selected
     *
     * @param newExclusionList  List of exclusions that needs to be updated
     * @param facilityExclusion Exclusion object used to compare from list
     * @param currentExclusion  Exclusion object that might get eliminated if a option is found selected
     * @return Flowable of updated list of Exclusion
     */
    private Flowable<List<Exclusion>> checkForSelectedOptions(@NonNull List<Exclusion> newExclusionList, @NonNull Exclusion facilityExclusion,
                                                              @NonNull Exclusion currentExclusion) {
        if (mBaseResponse != null) {
            return Flowable.fromIterable(mBaseResponse.getFacilityList())
                    .filter(facility -> facility != null && facility.getFacilityId() != null &&
                            facility.getOptions() != null && !facility.getOptions().isEmpty() &&
                            facility.getFacilityId().equals(facilityExclusion.getFacilityId()))
                    .flatMap(facility -> Flowable.fromIterable(facility.getOptions())
                            .filter(option -> option != null && option.getId() != null &&
                                    option.getId().equals(facilityExclusion.getOptionsId()) &&
                                    option.isSelected())
                            .flatMap(option -> { // selected option found
                                newExclusionList.remove(currentExclusion);
                                return Flowable.just(newExclusionList);
                            }).defaultIfEmpty(newExclusionList))
                    .defaultIfEmpty(newExclusionList);
        } else {
            return Flowable.just(newExclusionList);
        }
    }

    /**
     * Computes an exclusion list Flowable {@link Flowable} from the list given in parameters based on the facility id,
     * option id. Uses collect operator of Flowable {@link Flowable#collect(Callable, BiConsumer)}
     *
     * @param facilityId Facility id of an item
     * @param optionId   Option id of an item
     * @return Flowable of type List<Exclusion>
     */
    private Flowable<List<Exclusion>> getExclusionList(@NonNull String facilityId, @NonNull String optionId) {
        if (mBaseResponse != null) {
            List<Exclusion> totalDisableOptionList = new ArrayList<>();
            return Flowable.fromIterable(mBaseResponse.getExclusionList())
                    .flatMap(exclusions -> {
                        List<Exclusion> disableOptionList = new ArrayList<>();
                        AtomicBoolean containsCurrentIds = new AtomicBoolean(false);
                        AtomicInteger exclusionSize = new AtomicInteger(exclusions.size());
                        return getFilteredExclusionFlowable(exclusions)
                                .collect(() -> disableOptionList, (list, exclusion) -> {
                                    if (exclusion.getFacilityId().equals(facilityId) &&
                                            exclusion.getOptionsId().equals(optionId)) {
                                        containsCurrentIds.set(true);
                                    } else {
                                        list.add(exclusion);
                                    }
                                })
                                .repeat()
                                .takeUntil(disableOptList -> {
                                    if (disableOptList.size() == exclusionSize.get() ||
                                            (containsCurrentIds.get() && disableOptList.size() + 1 == exclusionSize.get())) {
                                        if (!containsCurrentIds.get()) {
                                            disableOptList.clear();
                                        }
                                        return true;
                                    }
                                    return false;
                                })
                                .last(new ArrayList<>())
                                .toFlowable()
                                .filter(tempExclusions -> tempExclusions != null && !tempExclusions.isEmpty())
                                .map(tempExclusions -> {
                                    totalDisableOptionList.addAll(tempExclusions);
                                    return totalDisableOptionList;
                                });
                    })
                    .last(new ArrayList<>())
                    .toFlowable();
        } else {
            return Flowable.just(new ArrayList<Exclusion>());
        }
    }

    /**
     * Method to toogle the chips.
     *
     * @param exclusionList list of items that needs to be toggled
     * @param isDisabling   boolean value to decide whether to disable or enable them
     * @return Flowable of type Boolean
     */
    private Flowable<Boolean> toggleChipsFlowable(List<Exclusion> exclusionList, boolean isDisabling) {
        if (mBaseResponse != null && exclusionList != null) {
            return Flowable.fromIterable(mBaseResponse.getFacilityList())
                    .filter(facility -> facility != null && facility.getFacilityId() != null &&
                            facility.getOptions() != null && !facility.getOptions().isEmpty())
                    .flatMap(facility -> Flowable.fromIterable(facility.getOptions())
                            .filter(option -> option != null && option.getId() != null)
                            .flatMap(option -> getFilteredExclusionFlowable(exclusionList)
                                    .flatMap(exclusion -> {
                                        if (exclusion != null && exclusion.getOptionsId().equals(option.getId()) &&
                                                exclusion.getFacilityId().equals(facility.getFacilityId())) {
                                            if (isDisabling) {
                                                option.setSelected(false);
                                                option.setEnabled(false);
                                            } else {
                                                option.setEnabled(true);
                                            }
                                        }
                                        return Flowable.just(Boolean.TRUE);
                                    })))
                    .defaultIfEmpty(Boolean.FALSE);
        }
        return Flowable.just(Boolean.FALSE);
    }

    /**
     * Create a Flowable object of Exclusion type from a list of exclusions and filter its values.
     *
     * @param exclusions List that needs to be converted into Flowable<Exclusion>
     * @return Flowable of type Exclusion
     */
    private Flowable<Exclusion> getFilteredExclusionFlowable(@NonNull List<Exclusion> exclusions) {
        return Flowable.fromIterable(exclusions)
                .filter(exclusion -> exclusion != null && exclusion.getFacilityId() != null &&
                        exclusion.getOptionsId() != null && !TextUtils.isEmpty(exclusion.getFacilityId()) &&
                        !TextUtils.isEmpty(exclusion.getOptionsId()));
    }

    /**
     * This is to process base response {@link BaseResponse} as per its values
     *
     * @param baseResponse baseResponse Object
     */
    private void processFilters(BaseResponse baseResponse) {
        if (baseResponse == null || baseResponse.getFacilityList() == null ||
                baseResponse.getExclusionList() == null ||
                baseResponse.getFacilityList().isEmpty()) {
            // no facilities found, show empty view screen
            mFilterView.showEmptyView();
        } else {
            // show the list of facilities
            mBaseResponse = baseResponse;
            mFilterView.showFacilities();
        }
    }

    /**
     * It is handle throwable based on its instance
     *
     * @param throwable Throwable that is used to show error in UI.
     */
    private void handleThrowable(@NonNull Throwable throwable) {
        checkNotNull(throwable);
        // check its instance
        if (throwable instanceof NoInternetException) {
            mFilterView.showApiErrors(Constants.NO_INTERNET_ERROR);
        } else if (throwable instanceof SocketTimeoutException) {
            mFilterView.showApiErrors(Constants.SOCKET_TIMEOUT_ERROR);
        } else if (throwable instanceof IOException) {
            mFilterView.showApiErrors(Constants.IO_ERROR);
        } else {
            mFilterView.showApiErrors(Constants.OTHER_ERROR);
        }
    }
}
