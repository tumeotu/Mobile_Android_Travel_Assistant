package com.ygaps.travelapp.Retrofit;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JsonLatLng
{
    @Expose
    @SerializedName("lat")
    public Double Lat = null;
    @Expose
    @SerializedName("long")
    public Double Long = null;

    public JsonLatLng(Double lat, Double lon)
    {
        Lat = lat;
        Long = lon;
    }

    public JsonLatLng(LatLng latLng)
    {
        if (latLng == null) return;
        Lat = latLng.latitude;
        Long = latLng.longitude;
    }
}
