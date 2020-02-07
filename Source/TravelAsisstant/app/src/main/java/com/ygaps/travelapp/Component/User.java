package com.ygaps.travelapp.Component;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User
{
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("username")
    @Expose
    public String username;

    @SerializedName("fullName")
    @Expose
    public String fullName;

    @SerializedName("full_name")
    @Expose
    public String full_name;

    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("phone")
    @Expose
    public String phone;

    @SerializedName("address")
    @Expose
    public String address;

    @SerializedName("dob")
    @Expose
    public String dob;
    @SerializedName("gender")
    @Expose
    public Integer gender;
    @SerializedName("email_verified")
    @Expose
    public Boolean emailVerified;
    @SerializedName("phone_verified")
    @Expose
    public Boolean phoneVerified;
    @SerializedName("token")
    @Expose
    public String token;

    public User(String fullName, String email, String phone, String dob, Integer gender)
    {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.gender = gender;
    }

    public User(User user)
    {
        this.id=user.id;
        this.fullName = user.fullName;
        this.email = user.email;
        this.phone = user.phone;
        this.dob = user.dob;
        this.gender = user.gender;
    }

    public String getFull_name()
    {
        return full_name;
    }

    public void setFull_name(String full_name)
    {
        this.full_name = full_name;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getDob()
    {
        return dob;
    }

    public void setDob(String dob)
    {
        this.dob = dob;
    }

    public Integer getGender()
    {
        return gender;
    }

    public void setGender(Integer gender)
    {
        this.gender = gender;
    }

    public Boolean getEmailVerified()
    {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified)
    {
        this.emailVerified = emailVerified;
    }

    public Boolean getPhoneVerified()
    {
        return phoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified)
    {
        this.phoneVerified = phoneVerified;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }
}
