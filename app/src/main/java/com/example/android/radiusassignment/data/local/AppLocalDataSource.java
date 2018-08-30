package com.example.android.radiusassignment.data.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.radiusassignment.data.AppDataSource;
import com.example.android.radiusassignment.data.remote.BaseResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.google.common.base.Preconditions.checkNotNull;

public class AppLocalDataSource implements AppDataSource {
    @Nullable
    private static AppLocalDataSource __instance;

    public static AppLocalDataSource getInstance() {
        if (__instance == null)
            synchronized (AppLocalDataSource.class) {
                if (__instance == null) {
                    __instance = new AppLocalDataSource();
                }
            }
        return __instance;
    }

    // Prevent direct instantiation.
    private AppLocalDataSource() {
    }

    /**
     * Method to get data from local source using Realm instance.
     *
     * @return Flowable of type BaseResponse
     */
    @Override
    public Flowable<BaseResponse> getData() {
        // TODO: get Data from Realm database (require changes)
        Realm parentRealm = Realm.getDefaultInstance();
        Flowable<RealmResults<Facility>> facilitiesFlowable = parentRealm .where(Facility.class)
                .findAllAsync().asFlowable().filter(RealmResults::isLoaded);
        Flowable<RealmResults<ExclusionList>> exclusionListFlowable = parentRealm .where(ExclusionList.class)
                .findAllAsync().asFlowable().filter(RealmResults::isLoaded);
        return Flowable.zip(facilitiesFlowable, exclusionListFlowable, (facilities, exclusionLists) -> {
            List<Facility> facilityList = parentRealm.copyFromRealm(facilities);
            List<ExclusionList> exclusionList = parentRealm.copyFromRealm(exclusionLists);

            parentRealm .close();
            List<List<Exclusion>> exclusionListList = new ArrayList<>();
            Flowable.just(exclusionList)
                    .flatMap(Flowable::fromIterable)
                    .map(ExclusionList::getExclusionRealmList)
                    .subscribe(exclusionListList::add);
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setExclusionList(exclusionListList);
            baseResponse.setFacilityList(facilityList);
            return baseResponse;
        });
    }

    /**
     * Method to store data in local data source, i.e., Realm.IO.
     *
     * @param isOnlyStoredLocally Boolean value to decide where to store the data.
     * @param baseResponse        BaseResponse object that needs to be stored
     */
    @Override
    public void saveData(@Nullable Boolean isOnlyStoredLocally,
                         @NonNull BaseResponse baseResponse) {
        // TODO: save data locally (require changes)
        checkNotNull(baseResponse);
        Realm realmObject = Realm.getDefaultInstance();
        realmObject.executeTransactionAsync(realm -> {
            if (baseResponse.getFacilityList() != null && !baseResponse.getFacilityList().isEmpty()) {
                realm.delete(Facility.class);
                realm.delete(Option.class);
                for (Facility facility : baseResponse.getFacilityList()) {
                    if (facility != null && facility.getOptions() != null && !facility.getOptions().isEmpty()) {
                        Facility newFacility = realm.createObject(Facility.class, facility.getFacilityId());
//                        newFacility.setFacilityId(facility.getFacilityId());
                        newFacility.setName(facility.getName());
                        RealmList<Option> optionRealmList = new RealmList<>();
                        for (Option option : facility.getOptions()) {
                            if (option != null) {
                                Option newOption = realm.createObject(Option.class, option.getId());
                                newOption.setIcon(option.getIcon());
                                newOption.setName(option.getName());
//                                newOption.setId(option.getId());
                                optionRealmList.add(newOption);
                            }
                        }
                        newFacility.setOptions(optionRealmList);
                    }
                }
            }

            if (baseResponse.getExclusionList() != null && !baseResponse.getExclusionList().isEmpty()) {
                realm.delete(ExclusionList.class);
                realm.delete(Exclusion.class);
                for (List<Exclusion> exclusionList : baseResponse.getExclusionList()) {
                    if (exclusionList != null && !exclusionList.isEmpty()) {
                        ExclusionList newExclusionList = realm.createObject(ExclusionList.class);
                        RealmList<Exclusion> exclusionRealmList = new RealmList<>();
                        for (Exclusion exclusion : exclusionList) {
                            if (exclusion != null) {
                                Exclusion newExclusion = realm.createObject(Exclusion.class);
                                newExclusion.setFacilityId(exclusion.getFacilityId());
                                newExclusion.setOptionsId(exclusion.getOptionsId());
                                exclusionRealmList.add(newExclusion);
                            }
                        }
                        newExclusionList.setExclusionRealmList(exclusionRealmList);
                    }
                }
            }
        });

        realmObject.close();
    }
}
