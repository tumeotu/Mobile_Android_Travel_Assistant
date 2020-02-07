package com.ygaps.travelapp.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestAcceptInvitation
{
    @Expose
    @SerializedName("tourId")
    public Integer TourId = null;

    @Expose
    @SerializedName("isAccepted")
    public Boolean IsAccepted = null;
}
