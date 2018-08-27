package com.example.android.radiusassignment.data.local;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Exclusion extends RealmObject {
    @SerializedName("facility_id")
    @Expose
    private String facilityId;
    @SerializedName("options_id")
    @Expose
    private String optionsId;

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getOptionsId() {
        return optionsId;
    }

    public void setOptionsId(String optionsId) {
        this.optionsId = optionsId;
    }

}
