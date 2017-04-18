package com.example.hp.foodapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.foodapplication.db.MealsNetworkContract;
import com.example.hp.foodapplication.db.MealsNetworkDb;
import com.example.hp.foodapplication.services.MealTypesService;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    // Define a projection that specifies which columns from the database
    // you will actually use after this query.
    // To save on resources only return the column values that you actually need.
    private static final String[] PROJECTION = {
            MealsNetworkContract.MealType._ID,
            MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_TITLE
    };

    private final static String[] FROM_COLUMNS = {
            MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_TITLE
     };

    private final static int[] TO_IDS = {
            R.id.tv_meal_title,
     };

    // How you want the results sorted in the resulting Cursor
    private static final String SORT_ORDER = MealsNetworkContract.MealType.COLUMN_MEAL_TYPE_PRIORITY + " ASC";


    private MealsNetworkDb dbHelper;
    private CursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);
        dbHelper = new MealsNetworkDb(this);
        Log.d(LOG_TAG, "on create ");

        getAllMealTypes();

        adapter = new SimpleCursorAdapter(this, R.layout.meal_type_layout, null, FROM_COLUMNS, TO_IDS, 0);
        ((SimpleCursorAdapter) adapter).setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() != R.id.tv_meal_title) {
                    return false;
                }
                return true;
            }

            });
    }

    private void getAllMealTypes() {
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//
//        Cursor cursor = db.query(
//                MealsNetworkContract.MealType.TABLE_NAME,           // The table to query
//                PROJECTION,                                             // The columns to return
//                null,                                                   // The columns for the WHERE clause
//                null,                                                   // The values for the WHERE clause
//                null,                                                   // don't group the rows
//                null,                                                   // don't filter by row groups
//                SORT_ORDER                                              // The sort order
//        );
//
//        int titleColumn = cursor.getColumnIndexOrThrow(MealsNetworkContract.MealType.COLUMN_MEALTYPE_TITLE);
//        while (cursor.moveToNext()) {
//            String title = cursor.getString(titleColumn);
//        }
//
//        TextView resultsTextView = (TextView) findViewById(R.id.tv_meal_title);
//
//        cursor.close();

        Intent intent = new Intent(this, MealTypesService.class);
        intent.setAction(MealTypesService.ACTION_GET_MEAL_TYPES);
        Log.d(LOG_TAG, "getAllMeal types");
        Log.d(LOG_TAG, "" +MealTypesService.ACTION_GET_MEAL_TYPES);

        startService(intent);
    }
}
