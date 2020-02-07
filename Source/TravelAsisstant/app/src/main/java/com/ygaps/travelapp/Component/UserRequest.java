package com.ygaps.travelapp.Component;

public class UserRequest
{
    private String fullName;
    private String email;
    private String phone;
    private Number gender;
    private String dob;

    public UserRequest(String fullName, String email, String phone, String dob, Number gender)
    {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.gender = gender;
    }

}
