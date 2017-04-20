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
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.example.hp.foodapplication.db.MealsNetworkContract;
import com.example.hp.foodapplication.db.MealsNetworkDb;
import com.example.hp.foodapplication.services.MealTypesService;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    private static final String[] PROJECTION = {
            MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_TITLE,
            MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_PRIORITY
    };

    // How you want the results sorted in the resulting Cursor
    private static final String SORT_ORDER = MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_PRIORITY + " ASC";


    private BroadcastReceiver getAllMealTypesResultBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String mealTypesResult = intent.getStringExtra(MealTypesService.EXTRA_MEAL_TYPES_RESULT);

            Log.d(LOG_TAG, "on recieve boradcast" + mealTypesResult);

//
            MealTypes[] mealTypes = new Gson().fromJson(mealTypesResult, MealTypes[].class);
//
//            String result = "";


//
            for (int i = 0; i < mealTypes.length; i++) {
                Log.d(LOG_TAG, "to json" + mealTypes[i]);

                insertMealTypes(mealTypes[i].getId(), mealTypes[i].getName(), mealTypes[i].getPriority());
            }
//
//            TextView resultsTextView = (TextView) findViewById(R.id.tv_results);
//            resultsTextView.setText(result);
        }
    };

    private MealsNetworkDb db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new MealsNetworkDb(this);

//        getAllMealTypes();
        getAllMealTypesfromDB();
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

        IntentFilter getStudentsResultIntentFilter = new IntentFilter(MealTypesService.ACTION_GET_MEAL_TYPES_RESULT);
        broadcastManager.registerReceiver(getAllMealTypesResultBroadcastReceiver, getStudentsResultIntentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

        broadcastManager.unregisterReceiver(getAllMealTypesResultBroadcastReceiver);
    }


    private void insertMealTypes(int id, String title, int priority) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(MealsNetworkContract.MealType._ID, id);
        values.put(MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_TITLE, title);
        values.put(MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_PRIORITY, priority);

        SQLiteDatabase dbHelper = db.getWritableDatabase();

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = dbHelper.insert(
                MealsNetworkContract.MealType.TABLE_NAME, // the table to insert to
                null, // nullColumnHack - if the values are empty you need this
                values); // all the data to insert

        Log.d(LOG_TAG, "row : " + values.describeContents());


        Toast.makeText(MainActivity.this, "New record inserted - ID " + newRowId, Toast.LENGTH_SHORT).show();
    }

    private void getAllMealTypesfromDB() {
        SQLiteDatabase dbHelper = db.getReadableDatabase();

        Cursor cursor = dbHelper.query(
                MealsNetworkContract.MealType.TABLE_NAME,           // The table to query
                PROJECTION,                                             // The columns to return
                null,                                                   // The columns for the WHERE clause
                null,                                                   // The values for the WHERE clause
                null,                                                   // don't group the rows
                null,                                                   // don't filter by row groups
                SORT_ORDER                                              // The sort order
        );

        String result = "";

        int firstNameColumn = cursor.getColumnIndexOrThrow(MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_TITLE);
        int lastNameColumn = cursor.getColumnIndexOrThrow(MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_PRIORITY);


        while (cursor.moveToNext()) {
            Log.d(LOG_TAG, "row : " + cursor.getString(firstNameColumn) + " ----- " + cursor.getString(lastNameColumn));

//            String firstName = cursor.getString(firstNameColumn);
//            String lastName = cursor.getString(lastNameColumn);
//
//            result += firstName + "\t" + lastName + "\n";
        }
//
//        TextView resultsTextView = (TextView) findViewById(R.id.tv_results);
//        resultsTextView.setText(result);
//
        cursor.close();
    }







    private void getAllMealTypes() {
        Intent intent = new Intent(this, MealTypesService.class);
        intent.setAction(MealTypesService.ACTION_GET_MEAL_TYPES);

        startService(intent);
    }
}
