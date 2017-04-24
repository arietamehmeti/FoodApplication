package com.example.hp.foodapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MealRecipeActivity extends AppCompatActivity {

    public static final String MEAL_SELECTED = "";

    public static final String LOG_TAG="Meal recepie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_recipe);

        DateUtility util = new DateUtility();

        Intent intent = getIntent();
        Meals ml = (Meals) intent.getSerializableExtra(MEAL_SELECTED);

        Log.d(LOG_TAG, "" + ml.getPrepTimeHour()+ " minute " + ml.getPrepTimeMinute() );


        TextView title = (TextView) findViewById(R.id.title);
        title.setText(ml.getTitle());

        TextView recipe = (TextView) findViewById(R.id.recipe);
        recipe.setText(ml.getRecipe());

        TextView servings = (TextView) findViewById(R.id.servings);
        servings.setText(String.valueOf(ml.getNumberOfServings()));

        TextView prep_time = (TextView) findViewById(R.id.prep_time);
        prep_time.setText(String.valueOf(ml.getPrepTimeHour()) + " : " + String.valueOf(ml.getPrepTimeMinute()));

        TextView creation_date= (TextView) findViewById(R.id.creation_date);
        creation_date.setText(String.valueOf(util.millisecsToDate(ml.getCreatedAt())));
    }
}
