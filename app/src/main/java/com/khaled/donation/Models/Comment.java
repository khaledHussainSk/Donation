package com.khaled.donation.Models;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {

    String id_comment,id_post_,id_publisher,comment;
    Date date;

    public Comment(String id_post_, String id_publisher, String comment, Date date) {
        this.id_post_ = id_post_;
        this.id_publisher = id_publisher;
        this.comment = comment;
        this.date = date;
    }

    public Comment() {
    }

    public String getId_post_() {
        return id_post_;
    }

    public void setId_post_(String id_post_) {
        this.id_post_ = id_post_;
    }

    public String getId_publisher() {
        return id_publisher;
    }

    public void setId_publisher(String id_publisher) {
        this.id_publisher = id_publisher;
    }

    public String getId_comment() {
        return id_comment;
    }

    public void setId_comment(String id_comment) {
        this.id_comment = id_comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
