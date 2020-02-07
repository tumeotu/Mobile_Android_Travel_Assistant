package com.ygaps.travelapp.Component;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedbackList
{
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("userId")
    @Expose
    public String userId;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("phone")
    @Expose
    public String phone;
    @SerializedName("avatar")
    @Expose
    public String avatar;
    @SerializedName("feedback")
    @Expose
    public String feedback = "";
    @SerializedName("review")
    @Expose
    public String review = "";
    @SerializedName("point")
    @Expose
    public Integer point = 0;
    @SerializedName("createdOn")
    @Expose
    public Long createdOn = 0L;

    public FeedbackList(Comment comment)
    {
        this.id = Integer.valueOf(comment.id);
        this.name = comment.name;
        this.avatar = comment.avatar;
        this.feedback = comment.comment;
    }

}
