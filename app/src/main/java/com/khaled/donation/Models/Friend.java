package com.khaled.donation.Models;

import java.util.Date;

public class Friend {

    private String id_following,id_follower,id;
    private Date date;

    public Friend() {
    }

    public Friend(String id_following, String id_follower, Date date) {
        this.id_following = id_following;
        this.id_follower = id_follower;
        this.date = date;
    }

    public String getId_following() {
        return id_following;
    }

    public void setId_following(String id_following) {
        this.id_following = id_following;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId_follower() {
        return id_follower;
    }

    public void setId_follower(String id_follower) {
        this.id_follower = id_follower;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
