package com.informaticsmatrix.birthdayapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.informaticsmatrix.birthdayapp.BirthdayApplication;
import com.informaticsmatrix.birthdayapp.R;
import com.informaticsmatrix.birthdayapp.adapter.BirthdayAdapter;
import com.informaticsmatrix.birthdayapp.greenDao.Birthday;
import com.informaticsmatrix.birthdayapp.greenDao.BirthdayDao;
import com.informaticsmatrix.birthdayapp.greenDao.DaoSession;
import com.informaticsmatrix.birthdayapp.interfaces.OnItemClickListener;
import com.informaticsmatrix.birthdayapp.utility.AppConstant;

import java.util.ArrayList;

import static com.informaticsmatrix.birthdayapp.utility.Utility.sdkVersionGreaterThanLollipop;

public class BirthdayListActivity extends AppCompatActivity implements OnItemClickListener {

    private ArrayList<Birthday>birthdayArrayList=new ArrayList<>();
    private BirthdayAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday_list);
        getSupportActionBar().setTitle(R.string.title_birthday);
        initViews();

        if(sdkVersionGreaterThanLollipop())
            checkPermissions();
    }

    private void initViews() {
        FloatingActionButton addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(BirthdayListActivity.this,AddBirthdayActivity.class);
                startActivity(intent);
            }
        });
        adapter=new BirthdayAdapter(birthdayArrayList,this);
        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBirthdayList();
    }

    private void updateBirthdayList() {
        DaoSession daoSession = ((BirthdayApplication) getApplication()).getDaoSession();
        BirthdayDao birthdayDao = daoSession.getBirthdayDao();
        ArrayList<Birthday> list = (ArrayList<Birthday>) birthdayDao.loadAll();
        adapter.upldateList(list);
    }

    @Override
    public void onItemClick(Birthday birthday) {
        Intent intent=new Intent(BirthdayListActivity.this,AddBirthdayActivity.class);
        intent.putExtra(AppConstant.DATA,birthday);
        startActivity(intent);
    }

    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    AppConstant.REQUEST_CODE_READ_CONTACTS);

        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    AppConstant.REQUEST_CODE_SEND_SMS);

        }

    }

    private void showAlertDialog(String message, final int requestcode, final String[] permission) {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(false);
        alertBuilder.setTitle(R.string.permission_required);
        alertBuilder.setMessage(message+"\nPlease click OK button to request permission again.");
        alertBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(BirthdayListActivity.this,
                        permission,
                        requestcode);
            }
        });
        alertBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });

        AlertDialog alert = alertBuilder.create();
        alert.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode==AppConstant.REQUEST_CODE_READ_CONTACTS) {

            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 & grantResults[0] == PackageManager.PERMISSION_GRANTED) {
           //do nothing

            } else if( grantResults[0] == PackageManager.PERMISSION_DENIED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    showAlertDialog(getString(R.string.message_alert),AppConstant.REQUEST_CODE_READ_CONTACTS,new String[]{Manifest.permission.READ_CONTACTS});
                }
                else {
                    Toast.makeText(this,
                            R.string.message_alert,Toast.LENGTH_LONG).show();
                }
            }


        }

        if (requestCode==AppConstant.REQUEST_CODE_SEND_SMS) {

            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 & grantResults[0] == PackageManager.PERMISSION_GRANTED) {
           //do nothing

            } else if( grantResults[0] == PackageManager.PERMISSION_DENIED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.SEND_SMS)) {
                    showAlertDialog(getString(R.string.message_sms_alert),AppConstant.REQUEST_CODE_SEND_SMS,new String[]{Manifest.permission.SEND_SMS});
                }
                else {
                    Toast.makeText(this,
                            R.string.message_sms_alert,Toast.LENGTH_LONG).show();
                }
            }


        }
    }

}
