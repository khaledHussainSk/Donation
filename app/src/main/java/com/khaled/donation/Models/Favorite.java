package com.khaled.donation.Models;

import java.io.Serializable;
import java.util.Date;

public class Favorite implements Serializable {
    String id,id_user,id_post;
    Date date;

    public Favorite() {
    }

    public Favorite(String id_user, String id_post, Date date) {
        this.id_user = id_user;
        this.id_post = id_post;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getId_post() {
        return id_post;
    }

    public void setId_post(String id_post) {
        this.id_post = id_post;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
