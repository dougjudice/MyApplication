package com.example.dougjudice.uncharted.GameElements;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by dougjudice on 3/25/17.
 */

/*
 -- ITEM ID --
 COMMONITE = 00
 RAREIUM = 01
 LEGENDGEM = 02

 SCANNER = 10
 BARRIER = 11
 */

public class GameItem {

    int itemID;
    String itemName;

    public GameItem(int itemID, Bitmap itemImg){
        this.itemID = itemID;
        this.itemName = resolveName(itemID);
    }

    public int getItemID(){
        return this.itemID;
    }
    public String getItemName(){
        return this.itemName;
    }

    public String resolveName(int id){
        switch(id){
            case 0: return "Commonite";
            case 1: return "Rareium";
            case 2: return "Legendgem";
            case 10: return "Scanner";
            case 11: return "Barrier";
            default: return "ITEM_NOT_FOUND";
        }
    }
}
