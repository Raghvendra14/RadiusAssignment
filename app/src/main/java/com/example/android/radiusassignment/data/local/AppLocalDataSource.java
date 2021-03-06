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
        // get Realm instance
        Realm parentRealm = Realm.getDefaultInstance();
        // create observables of facilities and exclusionList
        Flowable<RealmResults<Facility>> facilitiesFlowable = parentRealm.where(Facility.class)
                .findAllAsync().asFlowable().filter(RealmResults::isLoaded);
        Flowable<RealmResults<ExclusionList>> exclusionListFlowable = parentRealm.where(ExclusionList.class)
                .findAllAsync().asFlowable().filter(RealmResults::isLoaded);
        return Flowable.zip(facilitiesFlowable, exclusionListFlowable, (facilities, exclusionLists) -> {
            List<Facility> facilityList = parentRealm.copyFromRealm(facilities);
            List<ExclusionList> exclusionList = parentRealm.copyFromRealm(exclusionLists);
            // close the Realm instance
            parentRealm.close();
            // create a new exclusion list
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
        checkNotNull(baseResponse);
        Realm parentRealm = Realm.getDefaultInstance();
        parentRealm.executeTransactionAsync(realm -> {
            if (baseResponse.getFacilityList() != null && !baseResponse.getFacilityList().isEmpty() &&
                    baseResponse.getExclusionList() != null && !baseResponse.getExclusionList().isEmpty()) {
                // clean the db to store new data
                realm.delete(Facility.class);
                realm.delete(Option.class);
                realm.delete(ExclusionList.class);
                realm.delete(Exclusion.class);
                // store facilities data with options in Realm
                realm.copyToRealm(baseResponse.getFacilityList());
                // store exclusion list data in Realm
                for (List<Exclusion> exclusionList : baseResponse.getExclusionList()) {
                    ExclusionList exclusionListList = new ExclusionList();
                    RealmList<Exclusion> newExclusionList = new RealmList<>();
                    newExclusionList.addAll(exclusionList);
                    exclusionListList.setExclusionRealmList(newExclusionList);
                    realm.copyToRealm(exclusionListList);
                }
            }
        });
        // close the Realm instance
        parentRealm.close();
    }
}
