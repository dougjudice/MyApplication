package com.example.dougjudice.uncharted;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {

    private static class SingletonWrapper {
        static UserProfile INSTANCE = new UserProfile();
    }

    private UserProfile() {

    }

    public static UserProfile getProfile() {
        return SingletonWrapper.INSTANCE;
    }

    private int id;
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
