package com.khaled.donation.Models;

import java.util.Date;

public class Notifications {

    private String id;
    private String post_id;
    private String notifications_type;
    private String id_post_owner;
    private String id_notifications_owner;
    private String date_notifications;

    public Notifications() {
    }

    public Notifications(String post_id, String notifications_type, String id_post_owner, String id_notifications_owner, String date_notifications) {
        this.post_id = post_id;
        this.notifications_type = notifications_type;
        this.id_post_owner = id_post_owner;
        this.id_notifications_owner = id_notifications_owner;
        this.date_notifications = date_notifications;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getNotifications_type() {
        return notifications_type;
    }

    public void setNotifications_type(String notifications_type) {
        this.notifications_type = notifications_type;
    }

    public String getId_post_owner() {
        return id_post_owner;
    }

    public void setId_post_owner(String id_post_owner) {
        this.id_post_owner = id_post_owner;
    }

    public String getId_notifications_owner() {
        return id_notifications_owner;
    }

    public void setId_notifications_owner(String id_notifications_owner) {
        this.id_notifications_owner = id_notifications_owner;
    }

    public String getDate_notifications() {
        return date_notifications;
    }

    public void setDate_notifications(String date_notifications) {
        this.date_notifications = date_notifications;
    }
}
