package com.example.dougjudice.uncharted.GameElements;

import java.io.Serializable;

/**
 * Created by dougjudice on 4/27/17.
 */

public class Item implements Serializable {
    String name;
    int count;

    public Item(String name,  int count){
        this.name = name;
        this.count = count;
    }

    public String getName(){
        return this.name;
    }
    public int getCount(){
        return this.count;
    }
    public void setCount(int i){
        this.count = i;
    }
}
