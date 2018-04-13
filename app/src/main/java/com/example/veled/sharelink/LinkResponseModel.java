package com.example.veled.sharelink;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.reverse;

/**
 * Created by veled on 8.02.2018.
 */

public class LinkResponseModel {
    @SerializedName("auth")
    private boolean auth;

    @SerializedName("message")
    private String message;

    @SerializedName("token")
    private String token;

    @SerializedName("email")
    private String email;

    @SerializedName("groups")
    private ArrayList<String> groups;


    public boolean isAuth() {
        return auth;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() { return email;}

    public ArrayList<String> getGroups() { return groups; }

}
