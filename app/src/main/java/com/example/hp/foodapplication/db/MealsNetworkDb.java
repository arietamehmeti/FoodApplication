package com.example.hp.foodapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Hp on 4/17/2017.
 */

public class MealsNetworkDb extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MealType.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_MEAL_TYPES =
            "CREATE TABLE " + MealsNetworkContract.MealType.TABLE_NAME + " (" +
                    MealsNetworkContract.MealType._ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    MealsNetworkContract.MealType.COLUMN_MEALTYPE_TITLE + TEXT_TYPE + COMMA_SEP +
                    MealsNetworkContract.MealType.COLUMN_MEALTYPE_PRIORITY+ INT_TYPE +
                    " )";
    private static final String SQL_CREATE_MEAL=
            "CREATE TABLE " + MealsNetworkContract.Meal.TABLE_NAME + " (" +
                    MealsNetworkContract.Meal._ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    MealsNetworkContract.Meal.COLUMN_MEAL_TITLE+ TEXT_TYPE + COMMA_SEP +
                    MealsNetworkContract.Meal.COLUMN_MEAL_RECEIPE+ TEXT_TYPE + COMMA_SEP +
                    MealsNetworkContract.Meal.COLUMN_MEAL_NO_SERVINGS+ INT_TYPE +  COMMA_SEP +
                    MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_HOUR+ INT_TYPE + COMMA_SEP +
                    MealsNetworkContract.Meal.COLUMN_MEAL_PREP_TIME_MINUTE+ INT_TYPE + COMMA_SEP +
                    MealsNetworkContract.Meal.COLUMN_MEAL_CREATED_AT+ INT_TYPE + COMMA_SEP +
                    MealsNetworkContract.Meal.COLUMN_MEAL_TYPE+ INT_TYPE +

                    " )";

    private static final String SQL_DELETE_MEAL_TYPES =
            "DROP TABLE IF EXISTS " + MealsNetworkContract.MealType.TABLE_NAME;

    private static final String SQL_DELETE_MEAL =
            "DROP TABLE IF EXISTS " + MealsNetworkContract.Meal.TABLE_NAME;

    public MealsNetworkDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MEAL_TYPES );
        db.execSQL(SQL_CREATE_MEAL);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade
        // policy is to simply discard the data and start over
        db.execSQL(SQL_DELETE_MEAL_TYPES);
        db.execSQL(SQL_DELETE_MEAL);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
