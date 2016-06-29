package com.devtau.recyclerview.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public abstract class Util {
    //даты храним как строки в соответствии с этим форматтером
    //не работает напрямую с Calendar. его нужно сначала перевести в Date
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);

    public static void loopChildren(ViewGroup parent, View.OnClickListener onClickListener) {
        for (int i = parent.getChildCount() - 1; i >= 0; i--) {
            if (parent.getChildAt(i) != null) {
                parent.getChildAt(i).setOnClickListener(onClickListener);
            }
        }
    }

    public static String getStringDateTimeFromCal(Calendar date){
        return String.format("%02d.%02d %02d:%02d",
                date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.MONTH) + 1,
                date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE));
    }

    public static String getStringDateFromCal(Calendar date, Context context){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTimeInMillis());
        Locale locale = context.getResources().getConfiguration().locale;
        return String.format(locale, "%02d.%02d.%02d",
                cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR) % 100);
    }

    public static String getStringTimeFromCal(Calendar date, Context context){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTimeInMillis());
        Locale locale = context.getResources().getConfiguration().locale;
        return String.format(locale, "%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }



    public static void logDate(String dateName, Calendar dateToLog, Context context){
        Locale locale = context.getResources().getConfiguration().locale;
        String log = String.format(locale, "%02d.%02d.%04d %02d:%02d:%02d", dateToLog.get(Calendar.DAY_OF_MONTH),
                dateToLog.get(Calendar.MONTH) + 1, dateToLog.get(Calendar.YEAR), dateToLog.get(Calendar.HOUR_OF_DAY),
                dateToLog.get(Calendar.MINUTE), dateToLog.get(Calendar.SECOND));
        if (dateName.length() >= 20) {
            Logger.d(dateName + log);
        } else {
            while (dateName.length() < 20) dateName += '.';
            Logger.d(dateName + log);
        }
    }

    //работает только с числом десятичных знаков 0-5
    public static double roundResult(double value, int decimalSigns) {
        if (decimalSigns < 0 || decimalSigns > 5) {
            Logger.d("decimalSigns meant to be bw 0-5. Request is: " + String.valueOf(decimalSigns));
            if (decimalSigns < 0) decimalSigns = 0;
            if (decimalSigns > 5) decimalSigns = 5;
        }
        double multiplier = Math.pow(10.0, (double) decimalSigns);//всегда .0
        long numerator  = Math.round(value * multiplier);
        return numerator / multiplier;
    }

    public static int generateInt(int from, int to) {
        to -= from;
        return from + (int) (Math.random() * ++to);
    }
}
