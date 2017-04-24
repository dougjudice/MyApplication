package com.example.dougjudice.uncharted;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.dougjudice.uncharted.GameElements.PlayerGroup;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

import org.json.JSONException;
import org.json.JSONObject;

public class UserProfile implements Parcelable {

    boolean usingItem; // Determines whether or not the user is using an item, and if an item can be used or not (can only use one at a time)

    public static final Parcelable.Creator<UserProfile> CREATOR = new Parcelable.Creator<UserProfile>() {

        public UserProfile createFromParcel(Parcel in) {
            return new UserProfile(in);
        }

        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };

    private UserProfile(Parcel in) {
        userId = in.readInt();
        facebookId = in.readString();
        picture = in.readParcelable(Bitmap.class.getClassLoader());
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(userId);
        parcel.writeString(facebookId);
        parcel.writeParcelable(picture, 0);
        parcel.writeString(name);
    }

    // Can be called from anywhere

    private static class SingletonWrapper {
        static UserProfile INSTANCE = new UserProfile();
    }

    private UserProfile() {

    }

    public static UserProfile deserialize(String json) throws JSONException {
        return deserialize(new JSONObject(json));
    }

    public static UserProfile deserialize(JSONObject userJson) throws JSONException {
        int userId = userJson.getInt("UserId");
        String facebookId = userJson.getString("FacebookId");
        Integer groupId = userJson.isNull("GroupId") ? null : userJson.getInt("GroupId");

        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setFacebookId(facebookId);
        profile.setGroupId(groupId);

        return profile;
    }

    public static UserProfile getProfile() {
        return SingletonWrapper.INSTANCE;
    }

    public static void setProfile(UserProfile profile) {
        SingletonWrapper.INSTANCE = profile;
    }

    private int userId; // OUR ID NOT FACEBOOK'S
    private String facebookId;
    private Integer groupId;
    private Bitmap picture;
    private String name;

    private int[] userMaterials = {0,0,0}; // TODO: get from server

    public synchronized int getUserId() {
        return userId;
    }

    public synchronized void setUserId(int userId) {
        this.userId = userId;
    }

    public synchronized Integer getGroupId() {
        return groupId;
    }

    public synchronized void setGroupId(Integer id) {
        this.groupId = id;
    }

    public synchronized String getFacebookId() {
        return facebookId;
    }

    public synchronized void setFacebookId(String id) {
        this.facebookId = id;
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

    public synchronized int[] getUserMaterials(){ return this.userMaterials; }
}
