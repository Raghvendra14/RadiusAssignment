package com.example.android.radiusassignment.data.local;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Facility extends RealmObject {
    @PrimaryKey
    @SerializedName("facility_id")
    @Expose
    private String facilityId;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("options")
    @Expose
    private RealmList<Option> options = new RealmList<>();

    public Facility() {

    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<Option> getOptions() {
        return options;
    }

    public void setOptions(RealmList<Option> options) {
        this.options = options;
    }
}
