package com.example.hp.foodapplication;

/**
 * Created by Tringa on 4/24/2017.
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtility {

    static long dateToMillisecs(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date convertedDate = new Date();

        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long dateInMillisecs = convertedDate.getTime();

        return dateInMillisecs;
    }

    static String millisecsToDate (long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);

        return dateFormat.format(calendar.getTime());
    }

}

