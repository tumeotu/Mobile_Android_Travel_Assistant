package com.ygaps.travelapp.Component;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StopPoint implements Serializable
{
    @SerializedName("id")
    @Expose
    public Integer Id = null;
    @SerializedName("serviceId")
    @Expose
    public Integer serviceId = null;
    @SerializedName("address")
    @Expose
    public String Address = "";
    @SerializedName("provinceId")
    @Expose
    public Integer ProvinceId = 0;
    @SerializedName("name")
    @Expose
    public String Name = "";
    @SerializedName("lat")
    @Expose
    public Double Lat = 0.0;
    @SerializedName("long")
    @Expose
    public Double Long = 0.0;
    @SerializedName("arrivalAt")
    @Expose
    public Long ArrivalAt = 0L;
    @SerializedName("leaveAt")
    @Expose
    public Long LeaveAt = 0L;
    @SerializedName("serviceTypeId")
    @Expose
    public Integer ServiceTypeId = 0;
    @SerializedName("minCost")
    @Expose
    public Long MinCost = 0L;
    @SerializedName("maxCost")
    @Expose
    public Long MaxCost = 0L;
    @SerializedName("avatar")
    @Expose
    public String Avatar = null;
    // the index of stop Point in Tour object, do not rely on this property
    // this property is used as a response from server
    @SerializedName("index")
    @Expose
    public Integer Index = null;

    public Integer getId()
    {
        return Id;
    }

    public void setId(Integer id)
    {
        Id = id;
    }

    public Integer getServiceId()
    {
        return serviceId;
    }

    public void setServiceId(Integer serviceId)
    {
        this.serviceId = serviceId;
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String name)
    {
        Name = name;
    }

    public String getAddress()
    {
        return Address;
    }

    public void setAddress(String address)
    {
        Address = address;
    }

    public Integer getProvinceId()
    {
        return ProvinceId;
    }

    public void setProvinceId(Integer provinceId)
    {
        ProvinceId = provinceId;
    }

    public Double getLat()
    {
        return Lat;
    }

    public void setLat(Double lat)
    {
        Lat = lat;
    }

    public Double getLong()
    {
        return Long;
    }

    public void setLong(Double aLong)
    {
        Long = aLong;
    }

    public java.lang.Long getArrivalAt()
    {
        return ArrivalAt;
    }

    public void setArrivalAt(java.lang.Long arrivalAt)
    {
        ArrivalAt = arrivalAt;
    }

    public java.lang.Long getLeaveAt()
    {
        return LeaveAt;
    }

    public void setLeaveAt(java.lang.Long leaveAt)
    {
        LeaveAt = leaveAt;
    }

    public Integer getServiceTypeId()
    {
        return ServiceTypeId;
    }

    public void setServiceTypeId(Integer serviceTypeId)
    {
        ServiceTypeId = serviceTypeId;
    }

    public java.lang.Long getMinCost()
    {
        return MinCost;
    }

    public void setMinCost(java.lang.Long minCost)
    {
        MinCost = minCost;
    }

    public java.lang.Long getMaxCost()
    {
        return MaxCost;
    }

    public void setMaxCost(java.lang.Long maxCost)
    {
        MaxCost = maxCost;
    }

    public String getAvatar()
    {
        return Avatar;
    }

    public void setAvatar(String avatar)
    {
        Avatar = avatar;
    }

    public Integer getIndex()
    {
        return Index;
    }

    public void setIndex(Integer index)
    {
        Index = index;
    }

}
