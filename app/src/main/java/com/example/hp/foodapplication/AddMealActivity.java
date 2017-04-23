package com.example.hp.foodapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hp.foodapplication.db.MealsNetworkContract;
import com.example.hp.foodapplication.db.MealsNetworkDb;

import java.util.Date;

public class AddMealActivity extends AppCompatActivity {

    public static final String MEAL_TYPE_ADD = "Meal Type to add";
    public static final String LOG_TAG = "AddMealActivity";
    private int mealTypeId;
    private MealsNetworkDb db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        db = MealsNetworkDb.getInstance(this);

        Intent intent = getIntent();
        mealTypeId = Integer.parseInt(intent.getStringExtra(MEAL_TYPE_ADD));

        Button addBtn = (Button) findViewById(R.id.add_meal);

        final EditText title = (EditText) findViewById( R.id.et_title);
        final EditText recipe = (EditText) findViewById(R.id.et_recipe);
        final EditText prepTimeHour = (EditText) findViewById(R.id.et_prep_time_hour);
        final EditText prepTimeMin = (EditText) findViewById(R.id.et_prep_time_minute);
        final EditText servings = (EditText) findViewById(R.id.et_servings);



        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insertMealToDb(title.getText().toString(), recipe.getText().toString(), Integer.parseInt(prepTimeHour.getText().toString()),
                        Integer.parseInt(prepTimeMin.getText().toString()),  Integer.parseInt(servings.getText().toString()));
            }
        });
    }

    private void insertMealToDb(String title, String recipe, int prepTimeHour, int prepTimeMin, int servings){
        ContentValues values = new ContentValues();
        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_TITLE, title);
        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_RECEIPE, recipe);
        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_NO_SERVINGS, servings);
        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_HOUR, prepTimeHour);
        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_MINUTE, prepTimeMin);
//        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_CREATED_AT, new Date());
        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_TYPE, mealTypeId);


        SQLiteDatabase dbHelper = db.getWritableDatabase();

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = dbHelper.insert(
                MealsNetworkContract.Meal.TABLE_NAME, // the table to insert to
                null, // nullColumnHack - if the values are empty you need this
                values); // all the data to insert

        Log.d(LOG_TAG, "row (addMealAct): " + newRowId);
    }
}
