package com.example.dougjudice.uncharted.SettingsDrawerActivities;

/**
 * Created by dougjudice on 4/1/17.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.dougjudice.uncharted.R;

// This class defines the custom ArrayAdapter to get images in a listview

public class CustomList extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] web;
    private final Integer[] imageId;
    private final String[] facebookIds;
    private final Bitmap[] bitmapArray;
    private final boolean bitmapFlag;
    private final Context c;

    // All other uses
    public CustomList(Activity context,
                      String[] web, Integer[] imageId, String[] facebookIds) {
        super(context, R.layout.list_item, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
        this.facebookIds = facebookIds;
        this.bitmapArray = null;
        this.bitmapFlag = false;
        this.c = null;
    }

    // Use for 'GroupActivity', building list of friends in group
    public CustomList(Activity context,
                      String[] web, Bitmap[] bitmapArray, String[] facebookIds, Context c) {
        super(context, R.layout.list_item, web);
        this.context = context;
        this.web = web;
        this.bitmapArray = bitmapArray;
        this.facebookIds = facebookIds;
        this.imageId = null;
        this.bitmapFlag = true;
        this.c = c;
    }



    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.rsc_txt);
        txtTitle.setText(web[position]);

        if(!bitmapFlag){ // Load as from R.id.drawable
            ImageView imageView = (ImageView) rowView.findViewById(R.id.rsc_img);
            imageView.setImageResource(imageId[position]);
        } else{ // load from bitmap
            BitmapDrawable bd = new BitmapDrawable(c.getResources(), bitmapArray[position]);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.rsc_img);
            imageView.setBackground(bd);
        }

        return rowView;
    }

    // usage :  0: CustomList adapter = ...
    //          1: String s = adapter.getStringByPos(position);
    public String getStringByPos(int position){
        String s = web[position];
        return s;
    }
    public String getFacebookIdByPos(int position){
        return this.facebookIds[position];
    }
    public Integer getImageIdByPos(int position){
        return imageId[position];
    }
}