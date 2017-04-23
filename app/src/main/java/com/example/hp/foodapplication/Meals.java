package com.example.hp.foodapplication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Hp on 4/21/2017.
 */

public class Meals implements Serializable{

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("recipe")
    private String recipe;

    @SerializedName("numberOfServings")
    private int numberOfServings;

    @SerializedName("prepTimeHour")
    private int prepTimeHour;

    @SerializedName("getPrepTimeMinute")
    private int getPrepTimeMinute;

    @SerializedName("createdAt")
    private double createdAt;

    @SerializedName("mealType")
    private MealTypes mealType;


    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    public String getRecipe() {return recipe;}

    public void setRecipe(String recipe) {this.recipe = recipe;}

    public int getNumberOfServings() {return numberOfServings;}

    public void setNumberOfServings(int numberOfServings) {this.numberOfServings = numberOfServings;}

    public int getPrepTimeHour() {return prepTimeHour;}

    public void setPrepTimeHour(int prepTimeHour) {this.prepTimeHour = prepTimeHour;}

    public int getPrepTimeMinute() {return getPrepTimeMinute;}

    public void setPrepTimeMinute(int getPrepTimeMinute) {this.getPrepTimeMinute = getPrepTimeMinute;}

    public double getCreatedAt() {return createdAt;}

    public void setCreatedAt(int createdAt) {this.createdAt = createdAt;}

    public MealTypes getMealType() {return mealType;}

    public void setMealType(MealTypes mealType) {this.mealType = mealType;}
}
