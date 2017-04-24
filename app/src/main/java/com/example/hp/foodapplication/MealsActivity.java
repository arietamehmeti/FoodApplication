package com.example.hp.foodapplication;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.foodapplication.db.MealsNetworkContract;
import com.example.hp.foodapplication.db.MealsNetworkDb;
import com.example.hp.foodapplication.services.MealService;
import com.google.gson.Gson;


public class MealsActivity extends AppCompatActivity {

    public static final String MEAL_TYPE_ID = "";
    public static final String LOG_TAG = "MealsActivity";
    private MealTypes mealType;
    private MealsNetworkDb db;
    private CursorAdapter adapter;
    private Cursor cursor;
    private SwipeRefreshLayout swipeActivitiesContainer;

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

    private static final String[] FROM_COLUMNS = {
            MealsNetworkContract.Meal.COLUMN_MEAL_TITLE,
            MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_HOUR,
            MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_MINUTE
    };

    private static final int[] TO_IDS= {
            R.id.tv_title_meal,
            R.id.tv_prep_hour,
            R.id.tv_prep_min
    };

    private static final String SORT_ORDER = MealsNetworkContract.Meal.COLUMN_MEAL_CREATED_AT + " ASC";

    private BroadcastReceiver getAllMealsResultBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String mealResult = intent.getStringExtra(MealService.EXTRA_MEAL_RESULT);

            Meals[] meal = new Gson().fromJson(mealResult, Meals[].class);

            for (int i = 0; i < meal.length; i++) {
                Log.d(LOG_TAG, "to json" + meal[i]);
                insertMeal(meal[i].getId(), meal[i].getTitle(), meal[i].getCreatedAt(), meal[i].getRecipe(),meal[i].getNumberOfServings(),meal[i].getPrepTimeHour(), meal[i].getMealType(), meal[i].getPrepTimeMinute() );
            }
        }
    };

    private void insertMeal(int id, String title, double createdAt, String recipe, int servings, int prepTimeHour, MealTypes mealType, int prepTimeMin) {

        if(!dataAlreadyInDB(MealsNetworkContract.Meal.TABLE_NAME, MealsNetworkContract.Meal._ID, String.valueOf(id))) {
            ContentValues values = new ContentValues();
            values.put(MealsNetworkContract.Meal._ID, id);
            values.put(MealsNetworkContract.Meal.COLUMN_MEAL_TITLE, title);
            values.put(MealsNetworkContract.Meal.COLUMN_MEAL_RECEIPE, recipe);
            values.put(MealsNetworkContract.Meal.COLUMN_MEAL_NO_SERVINGS, servings);
            values.put(MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_HOUR, prepTimeHour);
            values.put(MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_MINUTE, prepTimeMin);
            values.put(MealsNetworkContract.Meal.COLUMN_MEAL_CREATED_AT, createdAt);
            values.put(MealsNetworkContract.Meal.COLUMN_MEAL_TYPE, mealType.getId());

            SQLiteDatabase dbHelper = db.getWritableDatabase();

            long newRowId;
            newRowId = dbHelper.insert(
                    MealsNetworkContract.Meal.TABLE_NAME,
                    null,
                    values);
            Log.d(LOG_TAG, "row : " + newRowId);
        }
    }

    private boolean dataAlreadyInDB(String TableName, String dbfield, String fieldValue) {
        MealsNetworkDb sqldb = MealsNetworkDb.getInstance(this);
        SQLiteDatabase db = sqldb.getReadableDatabase();

        String Query = "Select * from " + TableName + " where " + dbfield + " = " + fieldValue;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    private void getAllMeals(int mealTypeId) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Intent intent = new Intent(this, MealService.class);
            intent.setAction(MealService.ACTION_GET_MEAL);
            intent.putExtra(MealService.MEAL_TYPE, String.valueOf(mealTypeId));
            startService(intent);
        } else {
            Toast.makeText(MealsActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        swipeActivitiesContainer.setRefreshing(false);
    }


    private void getAllMealsFromDB(int mealTypeID) {
        SQLiteDatabase dbHelper = db.getReadableDatabase();

        cursor = dbHelper.query(
                MealsNetworkContract.Meal.TABLE_NAME,
                PROJECTION,
                MealsNetworkContract.Meal.COLUMN_MEAL_TYPE + "=?",
                new String[]{String.valueOf(mealTypeID)},
                null,
                null,
                SORT_ORDER
        );

        adapter = new SimpleCursorAdapter(this, R.layout.meals_layout, cursor, FROM_COLUMNS, TO_IDS, 0);
        ListView resultsListView = (ListView) findViewById(R.id.lv_meals_results);
        resultsListView.setAdapter(adapter);
    }

    private Meals getMealById(long id){
        SQLiteDatabase dbHelper = db.getReadableDatabase();
        Meals mealSelected = new Meals();
        cursor = dbHelper.query(
                MealsNetworkContract.Meal.TABLE_NAME,
                PROJECTION,
                MealsNetworkContract.Meal._ID +"=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            mealSelected.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MealsNetworkContract.Meal.COLUMN_MEAL_TITLE)));
            mealSelected.setRecipe(cursor.getString(cursor.getColumnIndexOrThrow(MealsNetworkContract.Meal.COLUMN_MEAL_RECEIPE)));
            mealSelected.setNumberOfServings(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MealsNetworkContract.Meal.COLUMN_MEAL_NO_SERVINGS))));
            mealSelected.setPrepTimeHour(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_HOUR))));
            mealSelected.setPrepTimeMinute(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_MINUTE))));
            mealSelected.setCreatedAt(cursor.getColumnIndexOrThrow(MealsNetworkContract.Meal.COLUMN_MEAL_CREATED_AT));
        }
//        cursor.close();
        return mealSelected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);

        db = MealsNetworkDb.getInstance(this);
        Intent intent = getIntent();
        mealType = (MealTypes) intent.getSerializableExtra(MEAL_TYPE_ID);

        TextView tx = (TextView) findViewById(R.id.tab);
        tx.setText(mealType.getName());

        swipeActivitiesContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_meals);
        swipeActivitiesContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllMeals(mealType.getId());
            }
        });

        ListView listview = (ListView) findViewById(R.id.lv_meals_results);
        Button addBtn = (Button) findViewById(R.id.add_button);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent = new Intent(MealsActivity.this, MealRecipeActivity.class);
                Log.d(LOG_TAG, "minute " + getMealById(id).getPrepTimeMinute() + "hour "+ getMealById(id).getPrepTimeHour());
                intent.putExtra(MealRecipeActivity.MEAL_SELECTED, getMealById(id));
                startActivity(intent);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MealsActivity.this, AddMealActivity.class);
                intent.putExtra(AddMealActivity.MEAL_TYPE_ADD, mealType);
                startActivity(intent);
            }
        });
        getAllMealsFromDB(mealType.getId());
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
