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
import android.widget.Toast;

import com.example.hp.foodapplication.db.MealsNetworkContract;
import com.example.hp.foodapplication.db.MealsNetworkDb;
import com.example.hp.foodapplication.services.MealService;
import static com.example.hp.foodapplication.ConnectionStateReceiver.sendCachedMeals;
import java.util.Date;

public class AddMealActivity extends AppCompatActivity {

    public static final String MEAL_TYPE_ADD = "Meal Type to add";
    public static final String LOG_TAG = "AddMealActivity";
    private static final int INITIAL_STATUS = 0;
    private MealTypes mealType;
    private MealsNetworkDb db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        db = MealsNetworkDb.getInstance(this);
        Intent intent = getIntent();
        mealType = (MealTypes) intent.getSerializableExtra(MEAL_TYPE_ADD);

        Button addBtn = (Button) findViewById(R.id.add_meal);

        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String title = ((EditText) findViewById( R.id.et_title)).getText().toString();
                final String recipe = ((EditText) findViewById(R.id.et_recipe)).getText().toString();
                final String prepTimeHour = ((EditText) findViewById(R.id.et_prep_time_hour)).getText().toString();
                final String prepTimeMin = ((EditText) findViewById(R.id.et_prep_time_minute)).getText().toString();
                final String servings = ((EditText) findViewById(R.id.et_servings)).getText().toString();

                insertMeal(title, recipe, servings, prepTimeHour, prepTimeMin);
                sendCachedMeals(getApplicationContext());
                finish();
            }
        });
    }

    private void insertMealToDb(String title, String recipe, int prepTimeHour, int prepTimeMin, int servings){

        DateUtility dateUtility = new DateUtility();
        long ts = dateUtility.dateToMillisecs(new Date().toString());

        Log.d(LOG_TAG, "date" + ts + "------------------- " + new Date().toString());

        ContentValues values = new ContentValues();
        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_TITLE, title);
        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_RECEIPE, recipe);
        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_NO_SERVINGS, servings);
        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_HOUR, prepTimeHour);
        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_MINUTE, prepTimeMin);
        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_CREATED_AT, ts);
        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_TYPE, mealType.getId());


        SQLiteDatabase dbHelper = db.getWritableDatabase();

        long newRowId;
        newRowId = dbHelper.insert(
                MealsNetworkContract.Meal.TABLE_NAME,
                null,
                values);

        Log.d(LOG_TAG, "row (addMealAct): " + newRowId);
    }

    private void insertMeal(String title, String recipe, String noOfServings, String prepTimeHour, String prepTimeMinute){
        Intent intent = new Intent(this, MealService.class);
        intent.setAction(MealService.ACTION_CREATE_MEAL);

        DateUtility dateUtility = new DateUtility();
        long ts = dateUtility.dateToMillisecs(new Date().toString());

        intent.putExtra(MealService.EXTRA_CREATED_AT, String.valueOf(ts));
        intent.putExtra(MealService.MEAL_TYPE, String.valueOf(mealType.getId()));
        intent.putExtra(MealService.EXTRA_MEAL_TYPE, mealType);
        intent.putExtra(MealService.EXTRA_TITLE, title);
        intent.putExtra(MealService.EXTRA_RECIPE, recipe);
        intent.putExtra(MealService.EXTRA_NO_SERVINGS, noOfServings);
        intent.putExtra(MealService.EXTRA_PREP_TIME_HOUR, prepTimeHour);
        intent.putExtra(MealService.EXTRA_PREP_TIME_MIN, prepTimeMinute);

        startService(intent);

        Toast.makeText(AddMealActivity.this, "Your meal has been created.", Toast.LENGTH_SHORT).show(); //NOT HERE (MAKE CHECK

//        insertMealToDb(title, recipe, Integer.parseInt(prepTimeHour),Integer.parseInt(prepTimeMinute),  Integer.parseInt(noOfServings));

    }

}
