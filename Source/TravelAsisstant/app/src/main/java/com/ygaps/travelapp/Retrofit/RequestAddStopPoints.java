package com.ygaps.travelapp.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ygaps.travelapp.Component.StopPoint;

import java.util.List;

public class RequestAddStopPoints
{
    @SerializedName("tourId")
    @Expose
    public String TourId;
    @SerializedName("stopPoints")
    @Expose
    public List<StopPoint> StopPoints;
    @SerializedName("deleteIds")
    @Expose
    public List<Integer> DeleteIds;

    public RequestAddStopPoints(String tourId, List<StopPoint> stopPoints)
    {
        this.TourId = tourId;
        this.StopPoints = stopPoints;
    }

    public RequestAddStopPoints(String tourId, List<StopPoint> stopPoints, List<Integer> deleteIds)
    {
        this.TourId = tourId;
        this.StopPoints = stopPoints;
        DeleteIds = deleteIds;
    }
}
