package com.informaticsmatrix.birthdayapp.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.informaticsmatrix.birthdayapp.BirthdayApplication;
import com.informaticsmatrix.birthdayapp.R;
import com.informaticsmatrix.birthdayapp.greenDao.Birthday;
import com.informaticsmatrix.birthdayapp.greenDao.BirthdayDao;
import com.informaticsmatrix.birthdayapp.greenDao.DaoSession;
import com.informaticsmatrix.birthdayapp.interfaces.DateTimeSelectListener;
import com.informaticsmatrix.birthdayapp.utility.AppConstant;
import com.informaticsmatrix.birthdayapp.utility.BirthdayAlarmManager;
import com.informaticsmatrix.birthdayapp.utility.DateTimeFormatter;
import com.informaticsmatrix.birthdayapp.utility.Utility;

import java.util.Calendar;
import java.util.Date;

public class AddBirthdayActivity extends AppCompatActivity implements View.OnClickListener, DateTimeSelectListener {

    private Birthday mBirthday =null;
    private TextView dob,phone,messageTime,time,button;
    private EditText name,birthdayMessage;
    private int day,month,year,hour=12,minute=0,AM_PM=Calendar.AM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_birthday);
        initViews();
        if(getIntent()!=null && getIntent().getParcelableExtra(AppConstant.DATA)!=null){
            getSupportActionBar().setTitle(R.string.title_update_birthday);
            button.setText(R.string.update_birthday);
            mBirthday =getIntent().getParcelableExtra(AppConstant.DATA);
            setDataInViews();

        }
        else {
            getSupportActionBar().setTitle(R.string.title_add_birthday);
        }
    }

    private void initViews() {
        dob=findViewById(R.id.birthdayDate);
        phone=findViewById(R.id.phone);
        messageTime=findViewById(R.id.messageTimeText);
        time=findViewById(R.id.messageTime);
        name=findViewById(R.id.name);
        birthdayMessage=findViewById(R.id.birthdayMessage);
        button=findViewById(R.id.button);
        time.setOnClickListener(this);
        phone.setOnClickListener(this);
        dob.setOnClickListener(this);
        button.setOnClickListener(this);

    }

    private void setDataInViews() {
        name.setText(mBirthday.getName());
        phone.setText(mBirthday.getPhone());
        birthdayMessage.setText(mBirthday.getMessage());
        dob.setText(mBirthday.getDobDate().toString());
        messageTime.setText(DateTimeFormatter.getTime(mBirthday.getDobDate()));
        time.setText(DateTimeFormatter.getTime(mBirthday.getDobDate()));
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.button: addUpdateBirthday(); break;
            case R.id.messageTime: openTimePicker(); break;
            case R.id.phone: openContacts();break;
            case R.id.birthdayDate:openDatePicker(); break;
        }
    }

    private void openDatePicker() {
        if(mBirthday!=null && mBirthday.getDobDate()!=null ) {
            Date date=mBirthday.getDobDate();
            Calendar dob = Calendar.getInstance();
            dob.setTime(date);
            DialogFragment newFragment = DatePickerFragment.newInstance(this,dob.get(Calendar.YEAR),dob.get(Calendar.MONTH),dob.get(Calendar.DATE));
            newFragment.show(getSupportFragmentManager(), "datePicker");

        }
        else {
            DialogFragment newFragment = new DatePickerFragment(this);
            newFragment.show(getSupportFragmentManager(), "datePicker");
        }
    }

    private void openContacts() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent,AppConstant.PICK_CONTACT );

    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (reqCode==AppConstant.PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();
            Cursor c =  getContentResolver().query(contactData, null, null, null, null);
            if (c.moveToFirst()) {


                String nameString = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if(TextUtils.isEmpty(name.getText()) && nameString!=null){
                    name.setText(nameString);
                    name.setSelection(nameString.length());
                }
                String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                String phoneNumber="";
                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,
                        null, null);
                while (phones.moveToNext()) {
                    phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    break;
                }

                if(phoneNumber!=null)
                    phone.setText(phoneNumber);
                phones.close();
            }


        }
    }



    private void openTimePicker() {
        if(mBirthday!=null && mBirthday.getDobDate()!=null ) {
            Date date=DateTimeFormatter.convertTo24HourFormat(mBirthday.getDobDate());
            Calendar dob = Calendar.getInstance();
            dob.setTime(date);
            DialogFragment newFragment = TimePickerFragment.newInstance(this, dob.get(Calendar.HOUR), dob.get(Calendar.MINUTE));
            newFragment.show(getSupportFragmentManager(), "timePicker");
        }

        else{
            DialogFragment newFragment = new TimePickerFragment(this);
            newFragment.show(getSupportFragmentManager(), "timePicker");
        }

    }

    private void addUpdateBirthday() {
        if(validateViews()){
            Birthday birthday=new Birthday();
            birthday.setName(name.getText().toString());
            birthday.setPhone(phone.getText().toString());
            birthday.setMessage(birthdayMessage.getText().toString());
            birthday.setDobDate(createAlarmDate());
            birthday.setAge(DateTimeFormatter.getAge(createAlarmDate()));
            birthday.setDobString(DateTimeFormatter.getDOBString(createAlarmDate()));
            if(mBirthday!=null){
                birthday.setId(mBirthday.getId());
                updateBirthday(birthday);
            }
            else{
                addNewBirthday(birthday);
            }
            BirthdayAlarmManager.setAlarm(this,birthday,false);
            Utility.showMessage(this,"Your Message will be send to "+birthday.getName().toUpperCase()+" on his/her birthday at "+
                    messageTime.getText());
            finish();
        }


    }

    private void addNewBirthday(Birthday birthday) {
        DaoSession daoSession = ((BirthdayApplication) getApplication()).getDaoSession();
        BirthdayDao birthdayDao = daoSession.getBirthdayDao();
        birthdayDao.insert(birthday);
    }

    private void updateBirthday(Birthday birthday) {
        DaoSession daoSession = ((BirthdayApplication) getApplication()).getDaoSession();
        BirthdayDao birthdayDao = daoSession.getBirthdayDao();
        birthdayDao.update(birthday);

//        groceryDao.deleteAll();
//        groceryDao.delete(grocery);
//        groceryDao.insert(grocery);
//        groceryDao.load(grocery_id);
//        groceryDao.insertOrReplaceInTx(groceryEntities);
    }

    private boolean validateViews() {
        if(TextUtils.isEmpty(name.getText().toString())){
            name.setError("Name is missing");
            return false;
        }
        if(TextUtils.isEmpty(phone.getText().toString())){
            phone.setError("Phone No. is missing");
            return false;
        } if(TextUtils.isEmpty(dob.getText().toString())){
            dob.setError("Birthday is missing");
            return false;
        } if(TextUtils.isEmpty(birthdayMessage.getText().toString())){
            birthdayMessage.setError("Message is missing");
            return false;
        }

        return true;
    }

    @Override
    public void onDateSelected(int year, int month, int day, String dateString) {
        dob.setText(dateString);
        this.year=year;
        this.month=month;
        this.day=day;
    }

    @Override
    public void onTimeSelected(int hour, int min, int AM_PM, String string) {
        messageTime.setText(string);
        time.setText(string);
        this.hour=hour;
        this.minute=min;
        this.AM_PM=AM_PM;
    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private DateTimeSelectListener listener;
        public TimePickerFragment( ) {
        }

        @SuppressLint("ValidFragment")
        public TimePickerFragment(DateTimeSelectListener listener) {
            this.listener=listener;
        }

        public static TimePickerFragment newInstance(DateTimeSelectListener listener,int hour,int min) {
            Bundle b = new Bundle();
            b.putInt("hour", hour);
            b.putInt("min", min);

            Fragment f = new TimePickerFragment(listener);
            f.setArguments(b);
            return (TimePickerFragment) f;
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Update using the arguments
            Bundle args = getArguments();
            if (args != null) {
                hour = args.getInt("hour");
                minute = args.getInt("min");
            }


            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            if(listener!=null){

                listener.onTimeSelected(hourOfDay,minute,DateTimeFormatter.getAM_PM(hourOfDay),DateTimeFormatter.updateTime(hourOfDay,minute));
            }
        }
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {


        private DateTimeSelectListener listener;

        public static DatePickerFragment newInstance(DateTimeSelectListener listener,int year,int month,int day) {
            Bundle b = new Bundle();
            b.putInt("year", year);
            b.putInt("month", month);
            b.putInt("day", day);

            Fragment f = new DatePickerFragment(listener);
            f.setArguments(b);
            return (DatePickerFragment) f;
        }
        //
        @SuppressLint("ValidFragment")
        public DatePickerFragment(DateTimeSelectListener listener) {
            this.listener=listener;
        }
        public DatePickerFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Update using the arguments
            Bundle args = getArguments();
            if (args != null) {
                year = args.getInt("year");
                month = args.getInt("month");
                day = args.getInt("day");
            }

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            if(listener!=null){
                listener.onDateSelected(year, month, day,day+"-"+DateTimeFormatter.getMonthName(month)+"-"+year);
            }

        }
    }


    private Date createAlarmDate(){
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, hour, minute);
        c.set(Calendar.AM_PM,AM_PM);
        Date date = c.getTime();
        return date;
    }
}
