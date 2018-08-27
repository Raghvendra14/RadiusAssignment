package com.example.android.radiusassignment.data.remote;

import com.example.android.radiusassignment.data.local.ExclusionList;
import com.example.android.radiusassignment.data.local.Facility;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BaseResponse {
    @SerializedName("facilities")
    @Expose
    private List<Facility> facilityList = new ArrayList<>();

    @SerializedName("exclusions")
    @Expose
    private List<ExclusionList> exclusionList = new ArrayList<>();

    public List<Facility> getFacilityList() {
        return facilityList;
    }

    public void setFacilityList(List<Facility> facilityList) {
        this.facilityList = facilityList;
    }

    public List<ExclusionList> getExclusionList() {
        return exclusionList;
    }

    public void setExclusionList(List<ExclusionList> exclusionList) {
        this.exclusionList = exclusionList;
    }
}
