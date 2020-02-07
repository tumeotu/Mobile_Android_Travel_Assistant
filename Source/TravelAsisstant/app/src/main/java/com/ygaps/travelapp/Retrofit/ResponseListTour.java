package com.ygaps.travelapp.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ygaps.travelapp.Component.Tour;

import java.util.List;

public class ResponseListTour
{
    @SerializedName("total")
    @Expose
    public Integer Total;

    @SerializedName("tours")
    @Expose
    public List<Tour> Tours;
}
