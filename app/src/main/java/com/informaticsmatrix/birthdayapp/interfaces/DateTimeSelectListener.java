package com.informaticsmatrix.birthdayapp.interfaces;


import java.util.Date;

public interface DateTimeSelectListener {
    void onDateSelected( int year, int month, int day,String dateString);
    void onTimeSelected(int hour,int min,int AM_PM,String time);
}
