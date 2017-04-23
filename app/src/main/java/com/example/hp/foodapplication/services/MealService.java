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
 * Created by Hp on 4/21/2017.
 */

public class MealService extends IntentService{

    public static final String MEAL_TYPE = "Meal Type extra";

    private static int typeId;

    private static final String LOG_TAG = "MealService";

    public static final String ACTION_GET_MEAL= "com.example.hp.foodapplication.GET_MEAL";

    public static final String ACTION_GET_MEAL_RESULT = "com.example.hp.foodapplication.GET_MEAL_RESULT";

    public static final String EXTRA_MEAL_RESULT = "meal.result";

    private static String GET_MEAL_URL = "";


    public MealService(){
        super("Meal Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        typeId = Integer.parseInt(intent.getStringExtra(MEAL_TYPE));
        Log.d(LOG_TAG, "on handle intent - service" + intent.getStringExtra(MEAL_TYPE));

        GET_MEAL_URL = "http://clubs-sdmdcity.rhcloud.com/rest/types/"+ typeId +"/meals";

        String action = intent.getAction();
        if (ACTION_GET_MEAL.equals(action)) {
            getMealTypes(intent);
        } else {
            throw new UnsupportedOperationException("No implementation for action " + action);
        }
    }

    private void getMealTypes(Intent intent) {
        Log.d(LOG_TAG, "get meal mtd" );

        InputStream is = null;

        try {
            URL url = new URL(GET_MEAL_URL);
            Log.d(LOG_TAG, "GET_MEAL_URL :  " + GET_MEAL_URL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Starts the query
            conn.connect();

            int response = conn.getResponseCode();
            Log.d(LOG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a bitmap
            String result = convertStreamToString(is);

            Intent resultIntent = new Intent(ACTION_GET_MEAL_RESULT);
            resultIntent.putExtra(EXTRA_MEAL_RESULT, result);

            LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
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

