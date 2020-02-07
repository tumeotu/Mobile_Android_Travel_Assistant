package com.ygaps.travelapp.Component;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponNotifyOnRoad {
    @SerializedName("notiList")
    @Expose
    private List<NotiList> notiList = null;

    public List<NotiList> getNotiList() {
        return notiList;
    }

    public void setNotiList(List<NotiList> notiList) {
        this.notiList = notiList;
    }
}