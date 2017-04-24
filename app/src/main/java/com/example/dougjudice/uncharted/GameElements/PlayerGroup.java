package com.example.dougjudice.uncharted.GameElements;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.dougjudice.uncharted.UserProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dougjudice on 4/23/17.
 */

public class PlayerGroup implements Parcelable {

    public String name;
    public int groupId;
    public List<UserProfile> members;

    public static PlayerGroup deserialize(String json) throws JSONException {
        JSONObject groupJson = new JSONObject(json);

        List<UserProfile> members = new ArrayList<>();
        JSONArray jsonMembers = groupJson.getJSONArray("Members");
        for (int i = 0; i < jsonMembers.length(); i++) {
            JSONObject jsonMember = jsonMembers.getJSONObject(i);
            UserProfile member = UserProfile.deserialize(jsonMember);
            members.add(member);
        }

        PlayerGroup group = new PlayerGroup();
        group.members = members;
        group.name = groupJson.getString("Name");
        group.groupId = groupJson.getInt("GroupId");

        return group;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(groupId);
        parcel.writeTypedList(members);
    }

    public static final Parcelable.Creator<PlayerGroup> CREATOR = new Parcelable.Creator<PlayerGroup>() {
        public PlayerGroup createFromParcel(Parcel in) {
            return new PlayerGroup(in);
        }

        public PlayerGroup[] newArray(int size) {
            return new PlayerGroup[size];
        }
    };

    private PlayerGroup() {}

    private PlayerGroup(Parcel in) {
        name = in.readString();
        groupId = in.readInt();
        members = new ArrayList<>();
        in.readTypedList(members, UserProfile.CREATOR);
    }

    public ArrayList<String> getNameList(){

        ArrayList<String> result = new ArrayList<>();

        for(int i = 0; i < members.size(); i++){
            UserProfile up = members.get(i);
            String name = up.getName();
            result.add(name);
        }
        return result;
    }
    public ArrayList<Bitmap> getPictureList(){

        ArrayList<Bitmap> result = new ArrayList<>();
        for(int i = 0; i < members.size(); i++){
            UserProfile up = members.get(i);
            Bitmap bm = up.getPicture();
            result.add(bm);
        }
        return result;
    }
}
