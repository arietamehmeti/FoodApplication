package com.example.hp.foodapplication;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.hp.foodapplication.db.MealsNetworkContract;
import com.example.hp.foodapplication.db.MealsNetworkDb;
import com.example.hp.foodapplication.services.MealService;
import com.google.gson.Gson;

public class MealsActivity extends AppCompatActivity {

    public static final String MEAL_TYPE_ID = "";
    public static final String LOG_TAG = "MealsActivity";

    private int mealTypeId;
    private String mealTypeAsString;

    private MealsNetworkDb db;
    private CursorAdapter adapter;
    private Cursor cursor;

    private static final String[] PROJECTION = {
            MealsNetworkContract.Meal._ID,
            MealsNetworkContract.Meal.COLUMN_MEAL_TITLE,
            MealsNetworkContract.Meal.COLUMN_MEAL_CREATED_AT,
            MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_HOUR,
            MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_MINUTE,
            MealsNetworkContract.Meal.COLUMN_MEAL_RECEIPE,
            MealsNetworkContract.Meal.COLUMN_MEAL_NO_SERVINGS,
            MealsNetworkContract.Meal.COLUMN_MEAL_TYPE
    };

    private static final String[] FROM_COLUMNS = { MealsNetworkContract.Meal.COLUMN_MEAL_TITLE,
            MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_HOUR,
            MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_MINUTE
    };

    private static final int[] TO_IDS= { R.id.tv_title_meal,
            R.id.tv_prep_hour,
            R.id.tv_prep_min
    };

    private static final String SORT_ORDER = MealsNetworkContract.Meal.COLUMN_MEAL_CREATED_AT + " ASC";

    private BroadcastReceiver getAllMealsResultBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//            String mealResult = intent.getStringExtra(MealService.EXTRA_MEAL_RESULT);
//
//            Log.d(LOG_TAG, "on recieve boradcast meal" + mealResult);
//
//            Meals[] meal = new Gson().fromJson(mealResult, Meals[].class);
//
//            for (int i = 0; i < meal.length; i++) {
//                Log.d(LOG_TAG, "to json" + meal[i]);
//                meal[i].setMealType(Integer.parseInt(MEAL_TYPE_ID));
//                insertMeal(meal[i].getId(), meal[i].getTitle(), meal[i].getCreatedAt(), meal[i].getRecipe(),meal[i].getNumberOfServings(), meal[i].getPrepTimeHour(), meal[i].getMealType(), meal[i].getGetPrepTimeMinute() );
//            }
        }
    };

    private void insertMeal(int id, String title, double createdAt, String recipe, int servings, int prepTimeHour, int mealType, int prepTimeMin) {
        ContentValues values = new ContentValues();
        values.put(MealsNetworkContract.Meal._ID, id);
        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_TITLE, title);
        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_RECEIPE, recipe);
        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_NO_SERVINGS, servings);
        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_HOUR, prepTimeHour);
        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_MINUTE, prepTimeMin);
        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_CREATED_AT, createdAt);
        values.put(MealsNetworkContract.Meal.COLUMN_MEAL_TYPE, mealType);


        SQLiteDatabase dbHelper = db.getWritableDatabase();

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = dbHelper.insert(
                MealsNetworkContract.Meal.TABLE_NAME, // the table to insert to
                null, // nullColumnHack - if the values are empty you need this
                values); // all the data to insert

        Log.d(LOG_TAG, "row : " + values.describeContents());

    }

    private void getAllMeals() {
        Intent intent = new Intent(this, MealService.class);
        intent.putExtra(MealService.MEAL_TYPE, mealTypeAsString);
        intent.setAction(MealService.ACTION_GET_MEAL);
        startService(intent);
    }


    private void getAllMealsfromDB() {
        SQLiteDatabase dbHelper = db.getReadableDatabase();

        cursor = dbHelper.query(
                MealsNetworkContract.Meal.TABLE_NAME,           // The table to query
                PROJECTION,                                             // The columns to return
                null,                                                   // The columns for the WHERE clause
                null,                                                   // The values for the WHERE clause
                null,                                                   // don't group the rows
                null,                                                   // don't filter by row groups
                SORT_ORDER                                              // The sort order
        );

        //REMOVE
        int firstNameColumn = cursor.getColumnIndexOrThrow(MealsNetworkContract.Meal.COLUMN_MEAL_TITLE);
        int lastNameColumn = cursor.getColumnIndexOrThrow(MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_HOUR);
        int lastNameColumn2 = cursor.getColumnIndexOrThrow(MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_MINUTE);


        while (cursor.moveToNext()) {
            Log.d(LOG_TAG, "row : " + cursor.getString(firstNameColumn) + " ----- " + cursor.getString(lastNameColumn)+ " ----- " + cursor.getString(lastNameColumn2));

//            String firstName = cursor.getString(firstNameColumn);
//            String lastName = cursor.getString(lastNameColumn);
//
//            result += firstName + "\t" + lastName + "\n";
        }
        // END REMOVE
        adapter = new SimpleCursorAdapter(this, R.layout.meals_layout, cursor, FROM_COLUMNS, TO_IDS, 0);
        ListView resultsListView = (ListView) findViewById(R.id.lv_meals_results);
        resultsListView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);

        db = MealsNetworkDb.getInstance(this);

        Intent intent = getIntent();
//        String mealTypeChosen = intent.getStringExtra(MEAL_TYPE);
        mealTypeAsString  = intent.getStringExtra(MEAL_TYPE_ID);
        mealTypeId = Integer.parseInt(mealTypeAsString);

        Log.d(LOG_TAG, ""+ mealTypeId);

        getAllMeals();
        getAllMealsfromDB();
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

        IntentFilter getStudentsResultIntentFilter = new IntentFilter(MealService.ACTION_GET_MEAL_RESULT);
        broadcastManager.registerReceiver(getAllMealsResultBroadcastReceiver, getStudentsResultIntentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
//        cursor.close();

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

        broadcastManager.unregisterReceiver(getAllMealsResultBroadcastReceiver);
    }
}
