package com.example.hp.foodapplication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Hp on 4/20/2017.
 */

public class MealTypes implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("priority")
    private int priority;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }
}
