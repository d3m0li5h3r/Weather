package com.d3m0li5h3r.apps.weather.utils;

import android.graphics.drawable.GradientDrawable;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by d3m0li5h3r on 22/4/16.
 */
public class Utils {
    public static boolean isDay(int seconds) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());

        calendar.setTimeInMillis(seconds * 1000L);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        return 7 <= hourOfDay && 19 >= hourOfDay;
    }

    public static void changeBackground(boolean isDayTime, View view) {
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        if (isDayTime)
            drawable.setGradientRadius(800);
        else
            drawable.setGradientRadius(2000);

        view.setBackground(drawable);
    }
}
