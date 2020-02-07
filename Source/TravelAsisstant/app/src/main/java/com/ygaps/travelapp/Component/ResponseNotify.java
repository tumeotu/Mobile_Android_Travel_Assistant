package com.ygaps.travelapp.Component;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResponseNotify {

    @SerializedName("total")
    @Expose
    public int total=0;
    @SerializedName("tours")
    @Expose
    public List<NotifyTour> tours = new ArrayList<>();
}