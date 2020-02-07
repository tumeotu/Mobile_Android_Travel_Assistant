package com.ygaps.travelapp.Component;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tour implements Serializable
{
    private final String DemoAvatar = "https://images.pexels.com/photos/67636/rose-blue-flower-rose-blooms-67636.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500";

    @SerializedName("id")
    @Expose
    public String Id = null;

    @SerializedName("hostId")
    @Expose
    public String HostId = null;

    @SerializedName("status")
    @Expose
    public Integer Status = 0;

    @SerializedName("name")
    @Expose
    public String Name = "";

    @SerializedName("sourceLat")
    @Expose
    public Double SourceLat = 0.0;

    @SerializedName("sourceLong")
    @Expose
    public Double SourceLong = 0.0;

    @SerializedName("desLat")
    @Expose
    public Double DestLat = 0.0;

    @SerializedName("desLong")
    @Expose
    public Double DestLong = 0.0;

    @SerializedName("minCost")
    @Expose
    public Long MinCost = 0L;

    @SerializedName("maxCost")
    @Expose
    public Long MaxCost = 0L;

    @SerializedName("startDate")
    @Expose
    public Long StartDate = 0L;

    @SerializedName("endDate")
    @Expose
    public Long EndDate = 0L;

    @SerializedName("adults")
    @Expose
    public Integer Adults = 0;

    @SerializedName("childs")
    @Expose
    public Integer Children = 0;

    @SerializedName("isPrivate")
    @Expose
    public Boolean IsPrivate = false;

    @SerializedName("avatar")
    @Expose
    public String Avatar64 = null;

    @SerializedName("stopPoints")
    @Expose
    public List<StopPoint> StopPoints = new ArrayList<>();

    @SerializedName("comments")
    @Expose
    public List<Comment> Commenents = new ArrayList<>();

    @SerializedName("members")
    @Expose
    public List<Member> Members = new ArrayList<>();

    public Bitmap Image;
}
