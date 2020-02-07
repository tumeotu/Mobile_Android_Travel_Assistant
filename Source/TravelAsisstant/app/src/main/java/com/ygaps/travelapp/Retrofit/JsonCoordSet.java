package com.ygaps.travelapp.Retrofit;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class JsonCoordSet
{
    @Expose
    @SerializedName("coordinateSet")
    private List<JsonLatLng> mCoords = new ArrayList<>(2);

    private JsonCoordSet()
    {
        mCoords.add(null);
        mCoords.add(null);
    }

    public JsonCoordSet(LatLng first, LatLng second)
    {
        this();
        setCoords(first, second);
    }

    public JsonCoordSet(List<LatLng> coordSet) throws IllegalArgumentException
    {
        this();
        if (coordSet == null || coordSet.isEmpty())
            throw new IllegalArgumentException("List must contain at least 1 LatLng");
        if (coordSet.size() < 2)
            setCoords(coordSet.get(0), null);
        else
            setCoords(coordSet.get(0), coordSet.get(1));
    }

    public void setCoords(LatLng first, LatLng second)
    {
        mCoords.set(0, new JsonLatLng(first));
        mCoords.set(1, new JsonLatLng(second));
    }
}
