package com.informaticsmatrix.birthdayapp.utility;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeFormatter {
    public static String getTime(Date date){
        String strigTime;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        strigTime=cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE)+" ";
        if (cal.get(Calendar.AM_PM) == Calendar.AM) {
            strigTime=strigTime+"AM";
        }
        else if(cal.get(Calendar.AM_PM) == Calendar.PM) {
            strigTime=strigTime+"PM";
        }
        return strigTime;
    }

    public static String getDOBString(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String strigDob = cal.get(Calendar.DAY_OF_MONTH) + "-" + getMonthName(cal.get(Calendar.MONTH));

        return strigDob;
    }

    public static String getMonthName(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }


    public static String getAge(Date date){
        Calendar dob = Calendar.getInstance();
        dob.setTime(date);

        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

    // Used to convert 24hr format to 12hr format with AM/PM values
    public static String updateTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();
        return aTime;
    }



    // Used to convert 24hr format to 12hr format with AM/PM values
    public static int getAM_PM(int hours) {

        boolean AM=false;

        if(hours < 12) {
            AM=true;
        } else {
            AM=false;
        }


        return (AM?Calendar.AM:Calendar.PM);
    }


    public static Date convertTo24HourFormat(Date time){
        // String time = "3:30 PM";
        //SimpleDateFormat date12Format = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm");

        try {
            String  string = (date24Format.format(time));
            Date date=date24Format.parse(string);
            return date;
        } catch (ParseException e) {


        }
        return null;
    }

    public static int getYear(int date, int month) {
        int year=0;
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        int calDate = calendar.get(Calendar.DATE);
       int calMonth=calendar.get(Calendar.MONTH);
      int  calYear=calendar.get(Calendar.YEAR);

      if(calMonth<month){
          return calYear;
      }
      else if(calMonth==month){
          if(calDate<=date)
              return calYear;

          else
              return calYear+1;
      }
      else
          return calYear+1;
    }
}
