package com.example.android.radiusassignment.data.local;

import io.realm.RealmList;
import io.realm.RealmObject;

public class ExclusionList extends RealmObject {
    private RealmList<Exclusion> exclusionRealmList = new RealmList<>();

    public ExclusionList() {

    }

    public RealmList<Exclusion> getExclusionRealmList() {
        return exclusionRealmList;
    }

    public void setExclusionRealmList(RealmList<Exclusion> exclusionRealmList) {
        this.exclusionRealmList = exclusionRealmList;
    }
}
