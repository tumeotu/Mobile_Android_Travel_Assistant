package com.ygaps.travelapp.Component;

public class NewPassword
{
    private Number userId;
    private String newPassword;
    private String verifyCode;

    public NewPassword(Number userId, String newPassword, String verifyCode)
    {
        this.userId = userId;
        this.newPassword = newPassword;
        this.verifyCode = verifyCode;
    }

    public Number getUserId()
    {
        return userId;
    }

    public void setUserId(Number userId)
    {
        this.userId = userId;
    }

    public String getNewPassword()
    {
        return newPassword;
    }

    public void setNewPassword(String newPassword)
    {
        this.newPassword = newPassword;
    }

    public String getVerifyCode()
    {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode)
    {
        this.verifyCode = verifyCode;
    }
}
