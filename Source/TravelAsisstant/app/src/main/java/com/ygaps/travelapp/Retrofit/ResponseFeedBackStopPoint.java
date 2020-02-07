package com.ygaps.travelapp.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ygaps.travelapp.Component.FeedbackList;

import java.util.List;

public class ResponseFeedBackStopPoint
{

    @SerializedName("feedbackList")
    @Expose
    public List<FeedbackList> feedbackList = null;


}
