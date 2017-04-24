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
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.example.hp.foodapplication.db.MealsNetworkContract;
import com.example.hp.foodapplication.db.MealsNetworkDb;
import com.example.hp.foodapplication.services.MealTypesService;
import com.google.gson.Gson;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    private MealsNetworkDb db;
    private CursorAdapter adapter;
    private Cursor cursor;
    private SwipeRefreshLayout swipeMealTypes;

    private static final String[] PROJECTION = {
            MealsNetworkContract.MealType._ID,
            MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_TITLE,
            MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_PRIORITY
    };

    private static final String SORT_ORDER = MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_PRIORITY + " ASC";

    private static final String[] FROM_COLUMNS = { MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_TITLE };

    private static final int[] TO_IDS = {R.id.tv_meal_title};

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
        if(!dataAlreadyInDB(MealsNetworkContract.MealType.TABLE_NAME, MealsNetworkContract.MealType._ID, String.valueOf(id))) {
            ContentValues values = new ContentValues();
            values.put(MealsNetworkContract.MealType._ID, id);
            values.put(MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_TITLE, title);
            values.put(MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_PRIORITY, priority);

            SQLiteDatabase dbHelper = db.getWritableDatabase();
            dbHelper.insert(
                    MealsNetworkContract.MealType.TABLE_NAME,
                    null,
                    values);

            Log.d(LOG_TAG, "row : " + values.describeContents());
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
            ml.setPriority(cursor.getInt(cursor.getColumnIndexOrThrow(MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_PRIORITY)));
            return ml;
        }
        return null;
    }

    private void getAllMealTypesFromDB() {
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

        adapter = new SimpleCursorAdapter(this, R.layout.meal_type_layout, cursor, FROM_COLUMNS, TO_IDS, 0);
        ListView resultsListView = (ListView) findViewById(R.id.lv_results);
        resultsListView.setAdapter(adapter);
    }

    private void getAllMealTypes() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Intent intent = new Intent(this, MealTypesService.class);
            intent.setAction(MealTypesService.ACTION_GET_MEAL_TYPES);
            startService(intent);
        } else {
            Toast.makeText(MainActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        swipeMealTypes.setRefreshing(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = MealsNetworkDb.getInstance(this);

        swipeMealTypes = (SwipeRefreshLayout) findViewById(R.id.swipe_meal_types);
        swipeMealTypes.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TextView tx = (TextView) findViewById(R.id.notify_refresh);
                tx.setText("");
                getAllMealTypes();
            }
        });

        ListView listview = (ListView) findViewById(R.id.lv_results);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent = new Intent(MainActivity.this, MealsActivity.class);
                Log.d(LOG_TAG, ""+ getMealTypeById(id));

                intent.putExtra(MealsActivity.MEAL_TYPE_ID, getMealTypeById(id));
                startActivity(intent);
            }
        });

        getAllMealTypesFromDB();
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
