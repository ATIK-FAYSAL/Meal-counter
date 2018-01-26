package com.atik_faysal.mealcounter;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Context;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.atik_faysal.backend.InformationCheckBackgroundTask;
import com.atik_faysal.backend.InformationCheckBackgroundTask.OnAsyncTaskInterface;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
/**
 * Created by USER on 1/26/2018.
 */

public class ForgetPassword extends AppCompatActivity
{


        private EditText eUserName,eFaWord,ePhone;
        private Toolbar toolbar;
        private Button bContinue;

        private String userName,fWord,phone;
        private static final String FILE_URL = "http://192.168.56.1/checkInformation.php";
        private static String POST_DATA;


        private AlertDialogClass dialogClass;
        private CheckInternetIsOn internetIsOn;


        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.foget_password);
                initComponent();
        }

        private void initComponent()
        {
                eFaWord = findViewById(R.id.eFavourite);
                eUserName = findViewById(R.id.eUserName);
                bContinue = findViewById(R.id.bContinue);
                toolbar = findViewById(R.id.toolbar1);
                ePhone = findViewById(R.id.ePhone);
                setSupportActionBar(toolbar);
                setToolbar();
                onButtonClick();
                //someMethod = new NeedSomeMethod();
                //someMethod.setToolbar(toolbar,ForgetPassword.this);
                dialogClass = new AlertDialogClass(this);
                internetIsOn = new CheckInternetIsOn(this);
        }

        private void setToolbar()
        {
                toolbar.setTitleTextColor(getResources().getColor(R.color.offWhite));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                toolbar.setNavigationIcon(R.drawable.icon_back);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                finish();
                        }
                });
        }

        private void getInformation()
        {
                userName = eUserName.getText().toString();
                fWord = eFaWord.getText().toString();
                phone = ePhone.getText().toString();

                if(phone.length()==11)phone = "+88"+phone;
        }

        private void onButtonClick()
        {
                bContinue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                getInformation();

                                if(internetIsOn.isOnline())
                                {
                                        try {
                                                POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8")+"&"
                                                        +URLEncoder.encode("fWord","UTF-8")+"="+URLEncoder.encode(fWord,"UTF-8")+"&"
                                                        +URLEncoder.encode("phoneNumber","UTF-8")+"="+URLEncoder.encode(phone,"UTF-8");
                                        } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                        }

                                        InformationCheckBackgroundTask checkBackgroundTask = new InformationCheckBackgroundTask(ForgetPassword.this);
                                        checkBackgroundTask.setOnResultListener(onAsyncTaskInterface);
                                        checkBackgroundTask.execute(FILE_URL,POST_DATA);
                                }else dialogClass.noInternetConnection();
                        }
                });
        }


        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        runOnUiThread(new Runnable() {
                                public void run() {
                                        switch (message) {
                                                case "success":
                                                        Toast.makeText(ForgetPassword.this,"Success",Toast.LENGTH_LONG).show();
                                                        break;
                                                case "word error":
                                                        eFaWord.setError("Invalid favourite word");
                                                        break;
                                                case "phone error":
                                                        ePhone.setError("Invalid phone");
                                                        break;
                                                case "offline":
                                                        dialogClass.noInternetConnection();
                                                        break;
                                                default:
                                                        eUserName.setError("Invalid UserName");
                                                        Toast.makeText(ForgetPassword.this,message+".Please retry with valid UserName",Toast.LENGTH_LONG).show();
                                                        break;
                                        }
                                }
                        });
                }
        };

}
