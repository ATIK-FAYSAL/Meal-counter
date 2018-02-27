package com.atik_faysal.mealcounter;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
        private TextView userNameErr,fWordErr,phoneErr;

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
                addTextChangeListener();
        }

        //initialize all object and UI component
        private void initComponent()
        {
                eFaWord = findViewById(R.id.eFavourite);
                eUserName = findViewById(R.id.txtUserName);
                bContinue = findViewById(R.id.bContinue);
                toolbar = findViewById(R.id.toolbar1);
                ePhone = findViewById(R.id.txtPhoneNumber);

                userNameErr = findViewById(R.id.userNameErr);
                fWordErr = findViewById(R.id.fWordErr);
                phoneErr = findViewById(R.id.phoneErr);

                setSupportActionBar(toolbar);
                setToolbar();
                onButtonClick();
                dialogClass = new AlertDialogClass(this);
                internetIsOn = new CheckInternetIsOn(this);
        }

        private void addTextChangeListener()
        {
                eUserName.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable s) {
                                if(eUserName.getText().toString().length()<5||eUserName.getText().toString().length()>15)
                                        userNameErr.setText("Invalid username");
                                else
                                        userNameErr.setText("");

                        }
                });

                eFaWord.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable s) {
                                if(eFaWord.getText().toString().length()<4||eFaWord.getText().toString().length()>15)
                                        fWordErr.setText("Must be in 4 to 15 characters");
                                else
                                        fWordErr.setText("");
                        }
                });

                ePhone.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable s) {
                                if(ePhone.getText().toString().length()!=11&&ePhone.getText().toString().length()!=14)
                                        phoneErr.setText("Invalid phone");
                                else
                                {
                                        switch (ePhone.getText().toString().length())
                                        {
                                                case 11:
                                                        if(ePhone.getText().toString().charAt(0)=='0'&&ePhone.getText().toString().charAt(1)=='1')
                                                                phoneErr.setText("");
                                                        else
                                                                phoneErr.setText("Invalid phone");
                                                        break;
                                                case 14:
                                                        if(ePhone.getText().toString().charAt(0)=='+'&&ePhone.getText().toString().charAt(1)=='8'
                                                                && ePhone.getText().toString().charAt(2)=='8'&&ePhone.getText().toString().charAt(3)=='0'
                                                                &&ePhone.getText().toString().charAt(4)=='1')
                                                                phoneErr.setText("");
                                                        else
                                                                phoneErr.setText("Invalid phone");
                                                        break;
                                        }
                                }
                        }
                });
        }

        //set toolbar above the page
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

        //get user information from UI
        private void getInformation()
        {
                userName = eUserName.getText().toString();
                fWord = eFaWord.getText().toString();
                phone = ePhone.getText().toString();

                if(phone.length()==14)phone = phone.substring(3);
        }

        //check user information,here check all user input condition.
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

        //button click to take action
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

        //interface check user information for change your password
        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        runOnUiThread(new Runnable() {
                                public void run() {
                                        switch (message) {//checkInformation.php file
                                                case "success":
                                                        Intent page = new Intent(ForgetPassword.this,ChangeYourPassword.class);
                                                        page.putExtra("phone",phone);
                                                        page.putExtra("userName",userName);
                                                        startActivity(page);
                                                        break;
                                                case "word error":
                                                        eFaWord.setError("Invalid favourite word");
                                                        break;
                                                case "phone error":
                                                        ePhone.setError("Invalid phone number");
                                                        break;
                                                case "offline":
                                                        dialogClass.noInternetConnection();
                                                        break;
                                                default:
                                                        dialogClass.error("Execution failed.We can not recognized you.");
                                                        break;
                                        }
                                }
                        });
                }
        };

}
