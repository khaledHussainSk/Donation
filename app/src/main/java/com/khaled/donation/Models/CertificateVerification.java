package com.khaled.donation.Models;

import java.io.Serializable;
import java.util.Date;

public class CertificateVerification implements Serializable {

    String id,id_user,image;
    Date date;

    public CertificateVerification(String id_user, String image, Date date) {
        this.id_user = id_user;
        this.image = image;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
