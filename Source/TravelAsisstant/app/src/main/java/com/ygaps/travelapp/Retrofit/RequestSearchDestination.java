package com.ygaps.travelapp.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestSearchDestination
{
    @Expose
    @SerializedName("searchKey")
    public String Keyword;

    @Expose
    @SerializedName("provinceId")
    public Integer ProvinceId;

    @Expose
    @SerializedName("provinceName")
    public String ProvinceName;

    @Expose
    @SerializedName("pageIndex")
    public Integer PageIndex;

    @Expose
    @SerializedName("pageSize")
    public Integer PageSize;
}
