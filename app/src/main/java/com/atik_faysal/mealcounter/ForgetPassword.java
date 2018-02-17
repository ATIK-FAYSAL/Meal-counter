package com.atik_faysal.mealcounter;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.atik_faysal.backend.ChangeYourPassword;
import com.atik_faysal.backend.InfoBackgroundTask;
import com.atik_faysal.backend.InfoBackgroundTask.OnAsyncTaskInterface;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
                eUserName = findViewById(R.id.groupId);
                bContinue = findViewById(R.id.bContinue);
                toolbar = findViewById(R.id.toolbar1);
                ePhone = findViewById(R.id.gType);
                setSupportActionBar(toolbar);
                setToolbar();
                onButtonClick();
                dialogClass = new AlertDialogClass(this);
                internetIsOn = new CheckInternetIsOn(this);
        }

        private void setToolbar()
        {
                toolbar.setTitleTextColor(getResources().getColor(R.color.white));
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

        private boolean checkUserInfo(String phone,String userName)
        {
                boolean flag = true;


                if(eFaWord.getText().toString().isEmpty())
                {
                        flag = false;
                        eFaWord.setError("Invalid");
                        eFaWord.requestFocus();
                }

                if(phone.length()!=11&&phone.length()!=14)
                {
                        if(ePhone.getText().toString().isEmpty())ePhone.requestFocus();
                        flag = false;
                        ePhone.setError("Invalid number");
                }

                if(userName.length()<3)
                {
                        if(eUserName.getText().toString().isEmpty())eUserName.requestFocus();
                        flag = false;
                        eUserName.setError("Invalid userName");
                }

                return flag;
        }

        private void onButtonClick()
        {
                bContinue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                getInformation();

                                if(internetIsOn.isOnline())
                                {
                                        if(checkUserInfo(phone,userName))
                                        {
                                                try {
                                                        POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8")+"&"
                                                                +URLEncoder.encode("fWord","UTF-8")+"="+URLEncoder.encode(fWord,"UTF-8")+"&"
                                                                +URLEncoder.encode("phoneNumber","UTF-8")+"="+URLEncoder.encode(phone,"UTF-8");
                                                } catch (UnsupportedEncodingException e) {
                                                        e.printStackTrace();
                                                }

                                                InfoBackgroundTask checkBackgroundTask = new InfoBackgroundTask(ForgetPassword.this);
                                                checkBackgroundTask.setOnResultListener(onAsyncTaskInterface);
                                                checkBackgroundTask.execute(FILE_URL,POST_DATA);
                                        }
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

                                                        if(phone.length()==14)phone = phone.substring(3,phone.length());

                                                        Intent page = new Intent(ForgetPassword.this,ChangeYourPassword.class);
                                                        page.putExtra("phone",phone);
                                                        page.putExtra("userName",userName);
                                                        startActivity(page);
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
                                                case "failed":
                                                        break;
                                                case "We can not recognized you":
                                                        eUserName.setError("Invalid UserName");
                                                        Toast.makeText(ForgetPassword.this,message+".Please retry with valid UserName",Toast.LENGTH_LONG).show();
                                                        break;
                                        }
                                }
                        });
                }
        };

}
