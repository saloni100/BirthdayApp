package com.informaticsmatrix.birthdayapp.utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


import com.informaticsmatrix.birthdayapp.greenDao.Birthday;
import com.informaticsmatrix.birthdayapp.reciever.BirthdayNotificationAlarmReceiver;

import java.util.Calendar;
import java.util.Date;



public class BirthdayAlarmManager {

    public static void setAlarm(Context context, Birthday birthday,boolean nextYear) {

        Date dob = birthday.getDobDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dob);
        int day = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH);
        int hour=cal.get(Calendar.HOUR);
        int min=cal.get(Calendar.MINUTE);

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 1);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        int year= DateTimeFormatter.getYear(day,month);
        calendar.set(Calendar.YEAR,(nextYear?year+1:year));
        calendar.set(Calendar.AM_PM, Calendar.AM);

        Intent myIntent = new Intent(context,

                BirthdayNotificationAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

        long YEARLY_MILLISECONDS = 31536000000L;
      //  alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), YEARLY_MILLISECONDS, pendingIntent);
          alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        System.out.println("Birthday notification alarm set");


    }


}
