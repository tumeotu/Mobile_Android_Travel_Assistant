package com.ygaps.travelapp.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestLogin
{
    @Expose
    @SerializedName("accessToken")
    public String AccessToken;

    public RequestLogin(String accessToken)
    {
        this.AccessToken = accessToken;
    }
}
