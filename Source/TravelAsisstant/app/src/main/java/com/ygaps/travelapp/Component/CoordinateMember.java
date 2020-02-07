package com.ygaps.travelapp.Component;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoordinateMember {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("lat")
    @Expose
    public Double lat;
    @SerializedName("long")
    @Expose
    public Double _long;
    public String NameUser;
}
