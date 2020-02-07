package com.ygaps.travelapp.Component;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Comment implements Serializable
{
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("comment")
    @Expose
    public String comment;
    @SerializedName("avatar")
    @Expose
    public String avatar;

    public Comment(String name, String comment) {
        this.id="0";
        this.name = name;
        this.comment = comment;
    }
}
