package com.ygaps.travelapp.Component;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReviewStopPoint {
    @Expose
    @SerializedName("serviceId")
    public Integer ServiceId;
    @Expose
    @SerializedName("point")
    public Integer Point;
    @Expose
    @SerializedName("feedback")
    public String Feedback;
}
