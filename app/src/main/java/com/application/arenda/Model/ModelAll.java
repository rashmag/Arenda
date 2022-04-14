package com.application.arenda.Model;

import android.util.Log;

import com.application.arenda.Chat.ChatUsersList;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ModelAll {
    private String name;
    private String Adsurl;
    private String search;
    private String publisher;
    private String postId;
    private String address;
    private String direction;
    private double lat;
    private double lon;
    private String arrayImagesUrl;

    //Message
    private String sender;
    private String messageId;
    private int feeling = -1;
    private String receiver;
    private String message;
    private boolean isseen;
    //Auth
    private String id;
    private String userName;
    private String userPhotoUri;
    private String status;
    private String adsPhotoUri;

    public ModelAll() {
    }
    //Auth - начало
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhotoUri() {
        return userPhotoUri;
    }

    public void setUserPhotoUri(String userPhotoUri) {
        this.userPhotoUri = userPhotoUri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdsPhotoUri() {
        return adsPhotoUri;
    }

    public void setAdsPhotoUri(String adsPhotoUri) {
        this.adsPhotoUri = adsPhotoUri;
    }
    //Auth - конец

    //MessageActivity - начало
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getFeeling() {
        return feeling;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }
    //MessageActivity - конец

    //Lenta - начало
    public String getArrayImagesUrl() {
        return arrayImagesUrl;
    }

    public void setArrayImagesUrl(String arrayImagesUrl) {
        this.arrayImagesUrl = arrayImagesUrl;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdsurl() {
        return Adsurl;
    }

    public void setAdsurl(String adsurl) {
        Adsurl = adsurl;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
    //Lenta - конец
}
