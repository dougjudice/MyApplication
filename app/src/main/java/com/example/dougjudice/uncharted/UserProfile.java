package com.example.dougjudice.uncharted;

import com.example.dougjudice.uncharted.GameElements.GameItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dougjudice on 3/25/17.
 */

public class UserProfile {

    // FROM JSON
    int userID;
    int groupID;
    String groupName;
    ArrayList<GameItem> inventory = new ArrayList<>();


    // FROM FACEBOOK API
    String userName;
    ArrayList<UserProfile> friendList = new ArrayList<>();
    // Bitmap userImage = ...

    public UserProfile(){


    }

}
