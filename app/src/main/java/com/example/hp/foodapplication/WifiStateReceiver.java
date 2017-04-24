package com.example.hp.foodapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.hp.foodapplication.db.MealsNetworkContract;
import com.example.hp.foodapplication.db.MealsNetworkDb;
import com.example.hp.foodapplication.services.MealService;

/**
 * Created by Hp on 4/24/2017.
 */

//public class WifiStateReceiver extends BroadcastReceiver {
 //   @Override
//    public void onReceive(Context context, Intent intent) {
//        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
//        if (networkInfo != null && networkInfo.isConnected()) {
//            sendCachedActivities(context.getApplicationContext());
//        }
//    }
//
//    static void sendCachedActivities(Context context) {
//
//        MealsNetworkDb dbHelper = MealsNetworkDb.getInstance(context);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        String[] selectionArg = { "0" };
//        Cursor cursor = db.rawQuery("SELECT * FROM " + MealsNetworkContract.Meal.TABLE_NAME + " WHERE " + MealsNetworkContract.Meal.COLUMN_NAME_STATUS + " = ?", selectionArg);
//
//
//        while (cursor.moveToNext()) {
//
//            //String id = cursor.getString(cursor.getColumnIndexOrThrow(MealsNetworkContract.Meal._ID));
//            String title = cursor.getString(cursor.getColumnIndexOrThrow(MealsNetworkContract.Meal.COLUMN_MEAL_TITLE));
//            String recipe = cursor.getString(cursor.getColumnIndexOrThrow(MealsNetworkContract.Meal.COLUMN_MEAL_RECEIPE));
//            int noServings = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MealsNetworkContract.Meal.COLUMN_MEAL_NO_SERVINGS)));
//            int prepTimeHour = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_HOUR)));
//            int prepTimeMin = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_MINUTE)));
//
//
//
//            Intent intent = new Intent(context, MealService.class);
//            intent.setAction(MealService.ACTION_CREATE_MEAL);
//
//            intent.putExtra(MealService.EXTRA_TITLE, title);
//            intent.putExtra(MealService.EXTRA_RECIPE, recipe);
//            intent.putExtra(MealService.EXTRA_NO_SERVINGS, noServings);
//            intent.putExtra(MealService.EXTRA_PREP_TIME_HOUR, prepTimeHour);
//            intent.putExtra(MealService.EXTRA_PREP_TIME_MIN, prepTimeMin);
//
//            context.startService(intent);
//        }
//        cursor.close();
//        db.close();
//    }
//}
