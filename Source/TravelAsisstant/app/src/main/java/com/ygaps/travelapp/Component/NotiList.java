package com.ygaps.travelapp.Component;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotiList {

    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("long")
    @Expose
    private double _long;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("notificationType")
    @Expose
    private Integer notificationType;
    @SerializedName("speed")
    @Expose
    private int speed;
    @SerializedName("createdByTourId")
    @Expose
    private String createdByTourId;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLong() {
        return _long;
    }

    public void setLong(double _long) {
        this._long = _long;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(Integer notificationType) {
        this.notificationType = notificationType;
    }

    public Object getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getCreatedByTourId() {
        return createdByTourId;
    }

    public void setCreatedByTourId(String createdByTourId) {
        this.createdByTourId = createdByTourId;
    }

}