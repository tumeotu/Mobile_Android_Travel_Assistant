package com.ygaps.travelapp.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestNotification
{
    @Expose
    @SerializedName("lat")
    public Double Lat = 0.0;

    @Expose
    @SerializedName("long")
    public Double Long = 0.0;

    @Expose
    @SerializedName("tourId")
    public String TourId = null;

    @Expose
    @SerializedName("userId")
    public String UserId = null;

    @Expose
    @SerializedName("notificationType")
    public Integer Type = 3;

    @Expose
    @SerializedName("speed")
    public Double Speed = null;

    @Expose
    @SerializedName("note")
    public String Note = null;
}
