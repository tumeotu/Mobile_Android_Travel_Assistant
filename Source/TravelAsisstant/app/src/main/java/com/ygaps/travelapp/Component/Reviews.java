package com.ygaps.travelapp.Component;

import android.graphics.Bitmap;

public class Reviews
{

    private String namUser;
    private Bitmap avatar;
    private String comment;


    public Reviews(String namUser, Bitmap avatar, String comment)
    {
        this.namUser = namUser;
        this.avatar = avatar;
        this.comment = comment;
    }

    public String getNamUser()
    {
        return namUser;
    }

    public void setNamUser(String namUser)
    {
        this.namUser = namUser;
    }

    public Bitmap getAvatar()
    {
        return avatar;
    }

    public void setAvatar(Bitmap avatar)
    {
        this.avatar = avatar;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }
}
