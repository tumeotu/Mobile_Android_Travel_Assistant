package com.ygaps.travelapp.Component;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReviewTour {
    @Expose
    @SerializedName("tourId")
    public Integer TourId;

    @Expose
    @SerializedName("point")
    public Integer Point;

    @Expose
    @SerializedName("review")
    public String Review;
}
