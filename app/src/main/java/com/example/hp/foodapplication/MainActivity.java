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
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
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
            MealsNetworkContract.MealType._ID,
            MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_TITLE,
            MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_PRIORITY
    };
    // How you want the results sorted in the resulting Cursor
    private static final String SORT_ORDER = MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_PRIORITY + " ASC";

    private final static String[] FROM_COLUMNS = { MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_TITLE };

    private final static int[] TO_IDS = {R.id.tv_meal_title};

    private MealsNetworkDb db;
    private CursorAdapter adapter;
    private Cursor cursor;


    private BroadcastReceiver getAllMealTypesResultBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String mealTypesResult = intent.getStringExtra(MealTypesService.EXTRA_MEAL_TYPES_RESULT);
            Log.d(LOG_TAG, "on recieve boradcast" + mealTypesResult);

            MealTypes[] mealTypes = new Gson().fromJson(mealTypesResult, MealTypes[].class);

            for (int i = 0; i < mealTypes.length; i++) {
                Log.d(LOG_TAG, "to json" + mealTypes[i]);

                insertMealTypes(mealTypes[i].getId(), mealTypes[i].getName(), mealTypes[i].getPriority());
            }
    }
    };

    private void insertMealTypes(int id, String title, int priority) {
        ContentValues values = new ContentValues();
        values.put(MealsNetworkContract.MealType._ID, id);
        values.put(MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_TITLE, title);
        values.put(MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_PRIORITY, priority);

        SQLiteDatabase dbHelper = db.getWritableDatabase();

        long newRowId;
        newRowId = dbHelper.insert(
                MealsNetworkContract.MealType.TABLE_NAME,
                null,
                values);

        Log.d(LOG_TAG, "row : " + values.describeContents());

    }

    private MealTypes getMealTypeById(long id){
        SQLiteDatabase dbHelper = db.getReadableDatabase();
        MealTypes ml = new MealTypes();
        cursor = dbHelper.query(
                MealsNetworkContract.MealType.TABLE_NAME,
                PROJECTION,
                MealsNetworkContract.MealType._ID +"=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            ml.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MealsNetworkContract.MealType._ID)));
            ml.setName(cursor.getString(cursor.getColumnIndexOrThrow(MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_TITLE)));
            return ml;
        }
        return null;
    }

    private void getAllMealTypesfromDB() {
        SQLiteDatabase dbHelper = db.getReadableDatabase();

        cursor = dbHelper.query(
                MealsNetworkContract.MealType.TABLE_NAME,
                PROJECTION,
                null,
                null,
                null,
                null,
                SORT_ORDER
        );

        //REMOVE
        int firstNameColumn = cursor.getColumnIndexOrThrow(MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_TITLE);
        int lastNameColumn = cursor.getColumnIndexOrThrow(MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_PRIORITY);


        while (cursor.moveToNext()) {
            Log.d(LOG_TAG, "row : " + cursor.getString(firstNameColumn) + " ----- " + cursor.getString(lastNameColumn));
        }
        // END REMOVE
        adapter = new SimpleCursorAdapter(this, R.layout.meal_type_layout, cursor, FROM_COLUMNS, TO_IDS, 0);
        ListView resultsListView = (ListView) findViewById(R.id.lv_results);
        resultsListView.setAdapter(adapter);
    }

    private void getAllMealTypes() {
        Intent intent = new Intent(this, MealTypesService.class);
        intent.setAction(MealTypesService.ACTION_GET_MEAL_TYPES);

        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = MealsNetworkDb.getInstance(this);
        Log.d(LOG_TAG, "on create");

//        getAllMealTypes();
        getAllMealTypesfromDB();

        ListView listview = (ListView) findViewById(R.id.lv_results);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent = new Intent(MainActivity.this, MealsActivity.class);
                Log.d(LOG_TAG, ""+ getMealTypeById(id));

                intent.putExtra(MealsActivity.MEAL_TYPE_ID, getMealTypeById(id));
                startActivity(intent);
            }
        });
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
        cursor.close();

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

        broadcastManager.unregisterReceiver(getAllMealTypesResultBroadcastReceiver);
    }

}
