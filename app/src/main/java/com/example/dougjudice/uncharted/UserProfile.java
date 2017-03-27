package com.example.dougjudice.uncharted;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {

    private static UserProfile instance;

    private UserProfile() {

    }

    public static UserProfile getProfile() {
        if (instance == null) {
            instance = new UserProfile();
        }

        return instance;
    }

    private String id;

    public void setUserId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return id;
    }
}
