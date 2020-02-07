package com.ygaps.travelapp.Component;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotifyTour {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("hostId")
    @Expose
    public int hostId;
    @SerializedName("hostName")
    @Expose
    public String hostName;
    @SerializedName("hostPhone")
    @Expose
    public String hostPhone;
    @SerializedName("hostEmail")
    @Expose
    public String hostEmail;
    @SerializedName("hostAvatar")
    @Expose
    public String hostAvatar;
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("minCost")
    @Expose
    public int minCost;
    @SerializedName("maxCost")
    @Expose
    public int maxCost;
    @SerializedName("startDate")
    @Expose
    public long startDate;
    @SerializedName("endDate")
    @Expose
    public long endDate;
    @SerializedName("adults")
    @Expose
    public Integer adults;
    @SerializedName("childs")
    @Expose
    public Integer childs;
    @SerializedName("avatar")
    @Expose
    public String avatar;
    @SerializedName("createdOn")
    @Expose
    public long createdOn;
}
