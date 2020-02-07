package com.ygaps.travelapp.Component;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Resister
{
    @Expose
    @SerializedName("password")
    public String password;
    @Expose
    @SerializedName("fullName")
    public String fullName;
    @Expose
    @SerializedName("email")
    public String email;
    @Expose
    @SerializedName("phone")
    public String phone;

    @Expose
    @SerializedName("address")
    public String address;
    @Expose
    @SerializedName("dob")
    public String dob;
    @Expose
    @SerializedName("gender")
    public Number gender;

    public Resister()
    {

    }

    public Resister(String password, String fullName, String email, String phone, String address, String dob, Number gender)
    {
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dob = dob;
        this.gender = gender;
    }

}
