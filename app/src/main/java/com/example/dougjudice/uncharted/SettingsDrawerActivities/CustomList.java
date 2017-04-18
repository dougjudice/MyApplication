package com.example.dougjudice.uncharted.SettingsDrawerActivities;

/**
 * Created by dougjudice on 4/1/17.
 */

import android.app.Activity;
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

    public CustomList(Activity context,
                      String[] web, Integer[] imageId, String[] facebookIds) {
        super(context, R.layout.list_item, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
        this.facebookIds = facebookIds;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.rsc_txt);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.rsc_img);
        txtTitle.setText(web[position]);

        imageView.setImageResource(imageId[position]);
        return rowView;
    }

    // usage :  CustomList adapter = ...
    //          adapter.getStringByPos(position);
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