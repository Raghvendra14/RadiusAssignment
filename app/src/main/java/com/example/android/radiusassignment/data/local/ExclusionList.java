package com.example.android.radiusassignment.data.local;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ExclusionList extends RealmObject {
    @PrimaryKey
    private long id;

    private RealmList<Exclusion> exclusionRealmList = new RealmList<>();

    public ExclusionList() {

    }

    public RealmList<Exclusion> getExclusionRealmList() {
        return exclusionRealmList;
    }

    public void setExclusionRealmList(RealmList<Exclusion> exclusionRealmList) {
        this.exclusionRealmList = exclusionRealmList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
