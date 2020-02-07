package com.ygaps.travelapp.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RequestCoordList
{
    @Expose
    @SerializedName("coordList")
    public List<JsonCoordSet> CoordSets = null;

    @Expose
    @SerializedName("hasOneCoordinate")
    public Boolean HasOneCoordinate = false;

    public RequestCoordList(List<JsonCoordSet> coordSets)
    {
        CoordSets = coordSets;
    }

}
