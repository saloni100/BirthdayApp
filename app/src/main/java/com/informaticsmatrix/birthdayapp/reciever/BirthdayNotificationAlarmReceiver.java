package com.informaticsmatrix.birthdayapp.reciever;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;


import com.informaticsmatrix.birthdayapp.BirthdayApplication;
import com.informaticsmatrix.birthdayapp.R;
import com.informaticsmatrix.birthdayapp.greenDao.Birthday;
import com.informaticsmatrix.birthdayapp.greenDao.BirthdayDao;
import com.informaticsmatrix.birthdayapp.greenDao.DaoSession;
import com.informaticsmatrix.birthdayapp.utility.BirthdayAlarmManager;
import com.informaticsmatrix.birthdayapp.utility.Utility;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class BirthdayNotificationAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        BirthdayApplication mApplication = ((BirthdayApplication)context.getApplicationContext());

        DaoSession daoSession = mApplication.getDaoSession();
        BirthdayDao birthdayDao = daoSession.getBirthdayDao();
        ArrayList<Birthday> birthdayList = (ArrayList<Birthday>) birthdayDao.loadAll();
        ArrayList<String> message = new ArrayList<String>();

        Date newDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(newDate);
        int currentDay = cal.get(Calendar.DATE);
        int currentMonth = cal.get(Calendar.MONTH);

        if (birthdayList != null && birthdayList.size() > 0) {

            for (Birthday birthday : birthdayList) {

                Calendar calender = Calendar.getInstance();
                calender.setTime(birthday.getDobDate());
                int day = calender.get(Calendar.DATE);
                int month = calender.get(Calendar.MONTH);
                if (currentDay == day && currentMonth == month) {
                    BirthdayAlarmManager.setAlarm(context,birthday,true);
                    message.add("Its " + birthday.getName() + "'s birthday today");
                    sendMessage(birthday.getPhone(),birthday.getMessage());
                    Log.d("BIRTHDAY_RECEIVER","Notification : ON  It's " + birthday.getName() + " birthday");
                }


            }
        }

        sendNotification(context,message);

    }

    private void sendMessage(String phone, String message) {
        if(phone!=null && message!=null&& !TextUtils.isEmpty(phone)){
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, message, null, null);
        }
    }

    private void sendNotification(Context context,ArrayList<String> message) {

        if (message != null && message.size() > 0) {
            int mNotificationId = 1;
            NotificationManager mNotifyMgr;
            NotificationCompat.Builder mBuilder;
            for (String string : message) {
                mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic)
                                .setContentTitle(context.getString(R.string.app_name))
                                .setContentText(string).setWhen(System.currentTimeMillis());
                mNotifyMgr.notify(mNotificationId++, mBuilder.build());
            }
        }
    }


}
