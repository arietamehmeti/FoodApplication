package com.example.hp.foodapplication.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.hp.foodapplication.Meals;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
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
    public static final String ACTION_CREATE_MEAL= "com.example.hp.foodapplication.CREATE_MEAL";

    public static final String ACTION_GET_MEAL_RESULT = "com.example.hp.foodapplication.GET_MEAL_RESULT";
    public static final String ACTION_CREATE_MEAL_RESULT = "com.example.hp.foodapplication.CREATE_MEAL_RESULT";

    public static final String EXTRA_TITLE = "meal.title";
    public static final String EXTRA_RECIPE = "meal.recipe";
    public static final String EXTRA_NO_SERVINGS = "meal.servings";
    public static final String EXTRA_PREP_TIME_HOUR = "meal.prepTimeHour";
    public static final String EXTRA_PREP_TIME_MIN = "meal.prepTImeMin";

    public static final String EXTRA_MEAL_RESULT = "meal.result";
    public static final String EXTRA_CREATE_MEAL_RESULT = "createMeal.result";

    private static String GET_MEAL_URL = "";
    private static String CREATE_MEAL_URL = "";

    private static final int PROGRESS_NOTIFICATION_ID = 187;



    public MealService(){
        super("Meal Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        typeId = Integer.parseInt(intent.getStringExtra(MEAL_TYPE));
        Log.d(LOG_TAG, "on handle intent - service" + intent.getStringExtra(MEAL_TYPE));

        GET_MEAL_URL = "http://clubs-sdmdcity.rhcloud.com/rest/types/"+ typeId +"/meals";
        CREATE_MEAL_URL = "http://clubs-sdmdcity.rhcloud.com/rest/types/"+ typeId +"/meals";

        String action = intent.getAction();
        if (ACTION_CREATE_MEAL.equals(action)){
            insertMeal(intent);
        }else if(ACTION_GET_MEAL.equals(action)) {
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

            conn.connect();

            int response = conn.getResponseCode();
            Log.d(LOG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            String result = convertStreamToString(is);

            Intent resultIntent = new Intent(ACTION_GET_MEAL_RESULT);
            resultIntent.putExtra(EXTRA_MEAL_RESULT, result);

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


    private void insertMeal(Intent intent){
        try {
            URL url = new URL(CREATE_MEAL_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.addRequestProperty("Content-Type", "application/json");

            String title = intent.getStringExtra(EXTRA_TITLE);
            String recipe = intent.getStringExtra(EXTRA_RECIPE);
            String noOfServings = intent.getStringExtra(EXTRA_NO_SERVINGS);
            String prepTimeHour = intent.getStringExtra(EXTRA_PREP_TIME_HOUR);
            String prepTimeMin = intent.getStringExtra(EXTRA_PREP_TIME_MIN);


            Meals meal = new Meals();
            meal.setTitle(title);
            meal.setRecipe(recipe);
            meal.setPrepTimeHour(Integer.parseInt(prepTimeHour));
            meal.setPrepTimeMinute(Integer.parseInt(prepTimeMin));
            meal.setNumberOfServings(Integer.parseInt(noOfServings));

            String studentJson = new Gson().toJson(meal);

            Log.d(LOG_TAG, studentJson);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            writer.write(studentJson);
            writer.flush();
            writer.close();

            conn.getOutputStream().close();

            // Starts the post
            conn.connect();

            int response = conn.getResponseCode();

            sendNotification(response);
            Log.d(LOG_TAG, "The response is: " + response);

            Intent resultIntent = new Intent(ACTION_CREATE_MEAL_RESULT);
            resultIntent.putExtra(EXTRA_CREATE_MEAL_RESULT, "Created student. Server responded with status " + response);

            LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);

        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception creating students", e);
        }

    }
    private void sendNotification(int response) {
        Intent intent = new Intent(this, MealService.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        String message;
        String contentTitle = "New Meal";


        if(response == 204){
            message = "Your meal has been successfully created!";
        } else {
            message = "Failed to create your meal.";
        }

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setTicker(message)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(contentTitle)
                .setContentText(message)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager activityNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        activityNotificationManager.notify(PROGRESS_NOTIFICATION_ID, notification);
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

