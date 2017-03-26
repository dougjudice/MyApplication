package com.example.dougjudice.uncharted;

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


    // FROM FACEBOOK API
    String userName;
    ArrayList<UserProfile> friendList = new ArrayList<>();
    // JPEG userImage = ...

    public UserProfile(){


    }

}
