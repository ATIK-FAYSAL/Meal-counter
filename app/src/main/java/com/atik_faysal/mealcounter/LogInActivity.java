package com.atik_faysal.mealcounter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.backend.UserLogIn;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

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

        private UserLogIn userLogIn;
        private CheckInternetIsOn checkInternet;
        private AlertDialogClass dialogClass;
        private SharedPreferenceData sharedPreferenceData;


        private static final String REMEMBER_ME = "rememberMe";
        private static final String USER_LOGIN = "userLogIn";

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

        private void initComponent()
        {
                txtSingUp = findViewById(R.id.txtSignUp);
                txtForgotPass = findViewById(R.id.txtForgotPass);
                bSignIn = findViewById(R.id.bSignIn);
                checkBox = findViewById(R.id.cRemember);
                txtUserName = findViewById(R.id.eUserName);
                txtUserPassword = findViewById(R.id.ePassword);

                userLogIn = new UserLogIn(this);
                checkInternet = new CheckInternetIsOn(this);
                dialogClass = new AlertDialogClass(this);
                sharedPreferenceData = new SharedPreferenceData(this);
        }

        private void actionComponent()
        {
                txtSingUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                startActivity(new Intent(LogInActivity.this,CreateNewAccount.class));
                        }
                });
                bSignIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                //Fabric.with(LogInActivity.this, new Crashlytics());
                                //forceCrash();
                                if(checkInternet.isOnline())
                                {
                                        if(txtUserName.getText().toString().isEmpty()||txtUserPassword.getText().toString().isEmpty())
                                        {
                                                if(txtUserName.getText().toString().isEmpty())txtUserName.setError("Input valid userName");
                                                else if(txtUserPassword.getText().toString().isEmpty())txtUserPassword.setError("Input valid password");
                                        }else
                                        {
                                                if(checkInternet.isOnline())new UserLogIn(LogInActivity.this).execute("login",txtUserName.getText().toString(),txtUserPassword.getText().toString());
                                                else dialogClass.ifNoInternet();
                                        }
                                }else
                                {
                                        dialogClass.ifNoInternet();
                                }
                        }
                });
        }

        private void closeApp()
        {
                if(getIntent().getBooleanExtra("flag",false))finish();
        }

        public void onClickCheckBox(View view)
        {
                if(checkBox.isChecked())
                        sharedPreferenceData.saveUserNamePassword(REMEMBER_ME,txtUserName.getText().toString(),txtUserPassword.getText().toString(),true);
                else
                        sharedPreferenceData.saveUserNamePassword(REMEMBER_ME,txtUserName.getText().toString(),txtUserPassword.getText().toString(),false);
        }



        public void forceCrash() {
                throw new RuntimeException("This is a crash");
        }

}
