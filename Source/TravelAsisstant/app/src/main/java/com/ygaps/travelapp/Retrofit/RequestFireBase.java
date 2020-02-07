package com.ygaps.travelapp.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestFireBase
{
    @Expose
    @SerializedName("fcmToken")
    public String FcmToken;
    @Expose
    @SerializedName("deviceId")
    public String DeviceId;
    @Expose
    @SerializedName("platform")
    public Number Platform;
    @Expose
    @SerializedName("appVersion")
    public String AppVersion;
}
