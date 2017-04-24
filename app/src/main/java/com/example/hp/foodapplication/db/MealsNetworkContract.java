package com.example.hp.foodapplication.db;

import android.provider.BaseColumns;

/**
 * Created by Hp on 4/17/2017.
 */

public final class MealsNetworkContract {

    private MealsNetworkContract(){
    }
    public static abstract class MealType implements BaseColumns {
        public static final String TABLE_NAME = "meal_type";
        public static final String COLUMN_MEAL_TYPE_TITLE = "type_title";
        public static final String COLUMN_MEAL_TYPE_PRIORITY = "priority";
    }
    public static abstract class Meal implements BaseColumns {
        public static final String TABLE_NAME = "meal";
        public static final String COLUMN_MEAL_TITLE = "title";
        public static final String COLUMN_MEAL_RECEIPE = "receipe";
        public static final String COLUMN_MEAL_NO_SERVINGS = "no_of_servings";
        public static final String COLUMN_MEAL_PREP_TIME_HOUR = "prep_time_hour";
        public static final String COLUMN_MEAL_PREP_TIME_MINUTE = "prep_time_minute";
        public static final String COLUMN_MEAL_CREATED_AT = "created_at";
        public static final String COLUMN_MEAL_TYPE = "meal_type";
        public static final String COLUMN_NAME_STATUS = "0";
    }

}
