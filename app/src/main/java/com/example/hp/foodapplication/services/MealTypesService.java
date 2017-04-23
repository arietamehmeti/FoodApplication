package com.example.hp.foodapplication.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Hp on 4/17/2017.
 */

public class MealTypesService extends IntentService {

    private static final String LOG_TAG = "MealTypesService";

    public static final String ACTION_GET_MEAL_TYPES= "com.example.hp.foodapplication.GET_MEAL_TYPES";

    public static final String ACTION_GET_MEAL_TYPES_RESULT = "com.example.hp.foodapplication.GET_MEAL_TYPES_RESULT";

    public static final String EXTRA_MEAL_TYPES_RESULT = "mealTypes.result";

    private static final String GET_MEAL_TYPES_URL = "http://clubs-sdmdcity.rhcloud.com/rest/types";


    public MealTypesService(){
        super("Meal Types Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(LOG_TAG, "on handle intent - service" );

        String action = intent.getAction();
        if (ACTION_GET_MEAL_TYPES.equals(action)) {
            getMealTypes(intent);
        } else {
            throw new UnsupportedOperationException("No implementation for action " + action);
        }
    }

    private void getMealTypes(Intent intent) {
        Log.d(LOG_TAG, "get meal typs mtd" );

        InputStream is = null;

        try {
            URL url = new URL(GET_MEAL_TYPES_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();

            int response = conn.getResponseCode();
            Log.d(LOG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            String result = convertStreamToString(is);

            Intent resultIntent = new Intent(ACTION_GET_MEAL_TYPES_RESULT);
            resultIntent.putExtra(EXTRA_MEAL_TYPES_RESULT, result);
            Log.d(LOG_TAG, "The obj is: " + result);

            LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);

        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception fetching students", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Exception closing stream", e);
                }
            }
        }
    }

    private String convertStreamToString(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = is.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }

        return baos.toString();
    }
}
