package com.example.hp.foodapplication;

import java.io.Serializable;

/**
 * Created by Hp on 4/21/2017.
 */

public class Meals{

    private int id;
    private String title;
    private String recipe;
    private int numberOfServings;
    private int prepTimeHour;
    private int getPrepTimeMinute;
    private double createdAt;
    private int mealType;

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

    public int getGetPrepTimeMinute() {return getPrepTimeMinute;}

    public void setGetPrepTimeMinute(int getPrepTimeMinute) {this.getPrepTimeMinute = getPrepTimeMinute;}

    public double getCreatedAt() {return createdAt;}

    public void setCreatedAt(int createdAt) {this.createdAt = createdAt;}

    public int getMealType() {return mealType;}

    public void setMealType(int mealType) {this.mealType = mealType;}
}
