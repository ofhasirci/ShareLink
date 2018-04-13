package com.example.veled.sharelink;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by veled on 16.03.2018.
 */

public class GroupObjectModel {
    @SerializedName("name")
    private String name;

    @SerializedName("starter")
    private String starter;

    @SerializedName("dateOfStart")
    private String dateOfStart;

    @SerializedName("members")
    private ArrayList<String> members;

    @SerializedName("links")
    private ArrayList<LinkObjectModel> links;

    public String getName() {
        return name;
    }

    public String getStarter() {
        return starter;
    }

    public String getDateOfStart() {
        return dateOfStart;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public ArrayList<LinkObjectModel> getLinks() {
        return links;
    }
}
