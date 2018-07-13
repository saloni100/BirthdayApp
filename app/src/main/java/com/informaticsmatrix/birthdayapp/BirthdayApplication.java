package com.informaticsmatrix.birthdayapp;

import android.app.Application;

import com.informaticsmatrix.birthdayapp.greenDao.DaoMaster;
import com.informaticsmatrix.birthdayapp.greenDao.DaoSession;

import org.greenrobot.greendao.database.Database;


public class BirthdayApplication extends Application {

    public static final boolean ENCRYPTED = true;
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"birthdays-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        ///// Using the below lines of code we can toggle ENCRYPTED to true or false in other to use either an encrypted database or not.
//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "birthdays-db-encrypted" : "birthdays-db");
//        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
//        daoSession = new DaoMaster(db).newSession();
    }

    public  DaoSession getDaoSession() {
        return daoSession;
    }

}