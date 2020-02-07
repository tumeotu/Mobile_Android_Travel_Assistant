package com.ygaps.travelapp.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestInviteMember
{
    @Expose
    @SerializedName("tourId")
    public String TourId;
    @Expose
    @SerializedName("invitedUserId")
    public String InvitedUserId;
    @Expose
    @SerializedName("isInvited")
    public Boolean IsInvited;
}
