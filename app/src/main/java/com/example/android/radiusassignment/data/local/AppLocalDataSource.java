package com.example.android.radiusassignment.data.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.radiusassignment.data.AppDataSource;
import com.example.android.radiusassignment.data.remote.BaseResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.google.common.base.Preconditions.checkNotNull;

public class AppLocalDataSource implements AppDataSource {
    @Nullable
    private static AppLocalDataSource __instance;

    /*  private Realm mRealm;*/

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

//        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public Flowable<BaseResponse> getData() {
        // TODO: get Data from Realm database
        Realm realm = Realm.getDefaultInstance();
        Flowable<RealmResults<Facility>> facilitiesFlowable = realm.where(Facility.class)
                .findAllAsync().asFlowable().filter(RealmResults::isLoaded);
        Flowable<RealmResults<ExclusionList>> exclusionListFlowable = realm.where(ExclusionList.class)
                .findAllAsync().asFlowable().filter(RealmResults::isLoaded);

        return Flowable.zip(facilitiesFlowable, exclusionListFlowable, (facilities, exclusionLists) -> {
            List<Facility> facilityList = new ArrayList<>(facilities);
            List<List<Exclusion>> exclusionListList = new ArrayList<>();
            Flowable.just(exclusionLists).flatMap(Flowable::fromIterable)
                    .map(ExclusionList::getExclusionRealmList)
                    .subscribe(exclusionListList::add);
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setExclusionList(exclusionListList);
            baseResponse.setFacilityList(facilityList);
            return baseResponse;
        });
    }

    @Override
    public void saveData(@Nullable Boolean isOnlyStoredLocally,
                         @NonNull BaseResponse baseResponse) {
        // TODO: save data locally
        checkNotNull(baseResponse);
        Realm.getDefaultInstance().executeTransactionAsync(realm -> {
            if (baseResponse.getFacilityList() != null && !baseResponse.getFacilityList().isEmpty()) {
                realm.delete(Facility.class);
                realm.delete(Option.class);
                for (int index = 0; index < baseResponse.getFacilityList().size(); index++) {
                    Facility facility = baseResponse.getFacilityList().get(index);
                    Facility newFacility = realm.createObject(Facility.class);
                    newFacility.setFacilityId(facility.getFacilityId());
                    newFacility.setName(facility.getName());
                    for (int indexTwo = 0; facility.getOptions() != null && indexTwo < facility.getOptions().size(); indexTwo++) {
                        Option option = facility.getOptions().get(indexTwo);
                        Option newOption = realm.createObject(Option.class);
                        newOption.setIcon(option.getIcon());
                        newOption.setName(option.getName());
                        // TODO: continue here for adding data in Realm
                    }
                }
                realm.insert(baseResponse.getFacilityList());
            }

            if (baseResponse.getExclusionList() != null && !baseResponse.getExclusionList().isEmpty()) {
                realm.delete(ExclusionList.class);
                realm.delete(Exclusion.class);
                for (List<Exclusion> exclusionList : baseResponse.getExclusionList()) {
                    ExclusionList newExclusionList = realm.createObject(ExclusionList.class);
//                    newExclusionList.setExclusionRealmList();
//                    realm.insert();
                }
            }
        });
    }
}
