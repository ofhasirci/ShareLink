package com.example.veled.sharelink;

import com.google.gson.annotations.SerializedName;

/**
 * Created by veled on 8.02.2018.
 */

public class LinkObjectModel {
    @SerializedName("user")
    private String username;
    @SerializedName("link")
    private String link;
    @SerializedName("description")
    private String description;
    @SerializedName("date")
    private String date;

    public LinkObjectModel(String username, String link, String description, String date){
        this.username = username;
        this.link = link;
        this.description = description;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
