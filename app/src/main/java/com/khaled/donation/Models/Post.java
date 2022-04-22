package com.khaled.donation.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Post implements Serializable {

    private String postId,description,publisher;
    private ArrayList<String> images;
//    private ArrayList<MediaStore.Video> videos;
    private Date datenews;
    private int likes,comments;

    public Post(String description, String publisher, ArrayList<String> images, Date datenews, int likes, int comments) {
        this.description = description;
        this.publisher = publisher;
        this.images = images;
        this.datenews = datenews;
        this.likes = likes;
        this.comments = comments;
    }

    public Post() {
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public Date getDatenews() {
        return datenews;
    }

    public void setDatenews(Date datenews) {
        this.datenews = datenews;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }
}
