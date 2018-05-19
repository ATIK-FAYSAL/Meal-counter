package com.atik_faysal.mealcounter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.others.DesEncryptionAlgo;
import com.google.android.gms.ads.AdView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 *initComponent-->Void.    initialize all component and object,also call some method.
 * onBackPressed-->close this app
 * actionComponent-->on button click,go to another activity
 * onClickCheckBox -->void.if check box is true then userName and password will save or if false you have to input again userName and password
 */

public class LogInActivity extends AppCompatActivity
{

        private TextView txtSingUp,txtForgotPass;
        private Button bSignIn;
        private CheckBox checkBox;
        private EditText txtUserName,txtUserPassword;

        private CheckInternetIsOn checkInternet;
        private AlertDialogClass dialogClass;
        private SharedPreferenceData sharedPreferenceData;
        private DatabaseBackgroundTask backgroundTask;
        private NeedSomeMethod someMethod;
        private DesEncryptionAlgo encryptionAlgo;

        private String userName,password;
        private static final String REMEMBER_ME = "rememberMe";
        private static final String USER_LOGIN = "userLogIn";
        //private static final String URL = "http://192.168.56.1/user_log_in.php";
        private static String DATA;

        private int errorCount=0;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.log_in_page);
                initComponent();
                actionComponent();
                closeApp();
        }

        @Override
        protected void onStart() {
                super.onStart();

                if(sharedPreferenceData.returnUserLogIn(USER_LOGIN))startActivity(new Intent(LogInActivity.this,HomePageActivity.class));

                if(sharedPreferenceData.checkRemember(REMEMBER_ME))
                {
                        checkBox.setChecked(true);
                        txtUserName.setText(sharedPreferenceData.getUserName(REMEMBER_ME));
                        txtUserPassword.setText(sharedPreferenceData.getPassword(REMEMBER_ME));
                }else
                {
                        checkBox.setChecked(false);
                        txtUserName.setText("");
                        txtUserPassword.setText("");
                }
        }

        @Override
        public void onBackPressed() {
                Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("flag",true);
                startActivity(intent);
        }

        //initialize all object and UI component
        private void initComponent()
        {
                txtSingUp = findViewById(R.id.txtSignUp);
                txtForgotPass = findViewById(R.id.txtForgotPass);
                bSignIn = findViewById(R.id.bSignIn);
                checkBox = findViewById(R.id.cRemember);
                txtUserName = findViewById(R.id.txtName);
                txtUserPassword = findViewById(R.id.ePassword);
                AdView adView = findViewById(R.id.adView);

                checkInternet = new CheckInternetIsOn(this);
                dialogClass = new AlertDialogClass(this);
                sharedPreferenceData = new SharedPreferenceData(this);
                someMethod = new NeedSomeMethod(this);
                encryptionAlgo = new DesEncryptionAlgo(this);
                someMethod.setAdmob(adView);
        }

        //button click to take action
        private void actionComponent()
        {
                txtSingUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                startActivity(new Intent(LogInActivity.this,RegisterUser.class));
                        }
                });

                bSignIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                //startActivity(new Intent(LogInActivity.this,HomePageActivity.class));

                                userName = txtUserName.getText().toString();
                                password = txtUserPassword.getText().toString();
                                password = encryptionAlgo.encryptPass(password);//get new encrypt password

                                backgroundTask = new DatabaseBackgroundTask(LogInActivity.this);

                                if(checkInternet.isOnline())
                                {
                                        if(txtUserName.getText().toString().isEmpty()||txtUserPassword.getText().toString().isEmpty())
                                        {
                                                if(userName.isEmpty())txtUserName.setError("Input valid userName");
                                                else if(password.isEmpty())txtUserPassword.setError("Input valid password");
                                        }else
                                        {
                                                try {
                                                        DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8")+"&"
                                                                +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");

                                                        backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                                        backgroundTask.execute(getResources().getString(R.string.logIn),DATA);
                                                } catch (UnsupportedEncodingException e) {
                                                        e.printStackTrace();
                                                }
                                        }

                                }else dialogClass.noInternetConnection();
                        }
                });

                txtForgotPass.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                startActivity(new Intent(LogInActivity.this,ForgetPassword.class));
                        }
                });
        }

        //exit from app
        private void closeApp()
        {
                if(getIntent().getBooleanExtra("flag",false))finish();
        }

        //check checkBox status
        public void onClickCheckBox(View view)
        {
                if(checkBox.isChecked())//this mehtod contain username ,password,and checkbox status
                        sharedPreferenceData.saveUserNamePassword(REMEMBER_ME,txtUserName.getText().toString(),txtUserPassword.getText().toString(),true);
                else//this mehtod contain username ,password,and checkbox status
                        sharedPreferenceData.saveUserNamePassword(REMEMBER_ME,txtUserName.getText().toString(),txtUserPassword.getText().toString(),false);
        }

        //after successfully login next activity will start.
        private void successfullyLogin(final String result)
        {
                final ProgressDialog ringProgressDialog = ProgressDialog.show(this, "Please wait", "Authenticating.....", true);
                ringProgressDialog.setCancelable(true);
                new Thread(new Runnable() {
                        @Override
                        public void run() {
                                try {
                                        Thread.sleep(2500);
                                } catch (Exception e) {
                                }
                                ringProgressDialog.dismiss();
                        }
                }).start();
                ringProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                                sharedPreferenceData.ifUserLogIn(USER_LOGIN,true);
                                sharedPreferenceData.currentUserInfo(userName,password);
                                sharedPreferenceData.userType(result);
                                startActivity(new Intent(LogInActivity.this,HomePageActivity.class));
                        }
                });
        }

        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String result) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        switch (result)
                                        {
                                                case "member":
                                                case "nope":
                                                case "admin":
                                                       successfullyLogin(result);
                                                        break;
                                                case "failed":
                                                        if(errorCount>=5)
                                                        {
                                                                dialogClass.error("your account is locked,please contact with admin or change your password.");
                                                                someMethod.userCurrentStatus(userName,"locked");
                                                        }else
                                                        {
                                                                dialogClass.error("Failed to login.please retry with valid username and password.");
                                                                errorCount++;
                                                        }
                                                        break;
                                                case "locked":
                                                        dialogClass.error("your account is locked,please contact with admin or change your password.");
                                                        break;
                                                default:
                                                        dialogClass.error("Execution failed.please try again.");
                                                        break;
                                        }
                                }
                        });
                }
        };
}