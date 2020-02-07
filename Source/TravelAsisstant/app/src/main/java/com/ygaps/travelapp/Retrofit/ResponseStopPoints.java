package com.ygaps.travelapp.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ygaps.travelapp.Component.StopPoint;

import java.util.List;

public class ResponseStopPoints
{
    @Expose
    @SerializedName("stopPoints")
    public List<StopPoint> StopPoints;
}
