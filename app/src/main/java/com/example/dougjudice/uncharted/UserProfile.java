package com.example.dougjudice.uncharted;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {

    boolean usingItem; // Determines whether or not the user is using an item, and if an item can be used or not (can only use one at a time)

    // Can be called from anywhere

    private static class SingletonWrapper {
        static UserProfile INSTANCE = new UserProfile();
    }

    private UserProfile() {

    }

    public static UserProfile getProfile() {
        return SingletonWrapper.INSTANCE;
    }

    private int id; // OUR ID NOT FACEBOOK'S
    private Bitmap picture;
    private String name;

    public synchronized int getId() {
        return id;
    }

    public synchronized void setId(int id) {
        this.id = id;
    }

    public synchronized Bitmap getPicture() {
        return picture;
    }

    public synchronized void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public synchronized String getName() {
        return this.name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }
}
