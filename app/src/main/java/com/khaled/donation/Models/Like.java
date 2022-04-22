package com.khaled.donation.Models;

import java.io.Serializable;
import java.util.Date;

public class Like implements Serializable {

    private String id_like,id_post,id_who_gave_like,id_who_took_Like;
    private Date date;

    public Like(String id_post, String id_who_gave_like, String id_who_took_Like,Date date) {
        this.id_post = id_post;
        this.id_who_gave_like = id_who_gave_like;
        this.id_who_took_Like = id_who_took_Like;
        this.date = date;
    }

    public Like() {
        
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId_like() {
        return id_like;
    }

    public void setId_like(String id_like) {
        this.id_like = id_like;
    }

    public String getId_post() {
        return id_post;
    }

    public void setId_post(String id_post) {
        this.id_post = id_post;
    }

    public String getId_who_gave_like() {
        return id_who_gave_like;
    }

    public void setId_who_gave_like(String id_who_gave_like) {
        this.id_who_gave_like = id_who_gave_like;
    }

    public String getId_who_took_Like() {
        return id_who_took_Like;
    }

    public void setId_who_took_Like(String id_who_took_Like) {
        this.id_who_took_Like = id_who_took_Like;
    }
}
