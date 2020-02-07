package com.ygaps.travelapp.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ygaps.travelapp.Component.FeedbackList;

import java.util.List;

public class ResponseFeedBackTour
{
    @SerializedName("reviewList")
    @Expose
    public List<FeedbackList> feedbackList = null;

}
