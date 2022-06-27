package com.khaled.donation.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Post implements Serializable {

    private String postId,title,description,donation_link,publisher;
    private ArrayList<String> images;
    private Date datenews;
    private int likes,comments;
    private double price;
    private String category;
    private String postType;

    public Post(String title, String description, String publisher, ArrayList<String> images
            , Date datenews, int likes, int comments, double price, String category, String postType) {
        this.title = title;
        this.description = description;
        this.publisher = publisher;
        this.images = images;
        this.datenews = datenews;
        this.likes = likes;
        this.comments = comments;
        this.price = price;
        this.category = category;
        this.postType = postType;
    }

    public Post(String title, String description,String donation_link, String publisher
            , Date datenews, int likes, int comments
            , String category, String postType) {
        this.title = title;
        this.description = description;
        this.donation_link = donation_link;
        this.publisher = publisher;
        this.datenews = datenews;
        this.likes = likes;
        this.comments = comments;
        this.category = category;
        this.postType = postType;
    }

    public Post() {
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getDonation_link() {
        return donation_link;
    }

    public void setDonation_link(String donation_link) {
        this.donation_link = donation_link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
