package com.ygaps.travelapp.Component;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestLocation {
    @SerializedName("userId")
    @Expose
    public int userId;
    @SerializedName("tourId")
    @Expose
    public int tourId;
    @SerializedName("lat")
    @Expose
    public double lat;
    @SerializedName("long")
    @Expose
    public double _long;
}
