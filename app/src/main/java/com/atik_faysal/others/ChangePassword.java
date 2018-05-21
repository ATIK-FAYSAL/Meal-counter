package com.atik_faysal.others;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.R;
import com.google.android.gms.ads.AdView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ChangePassword extends AppCompatActivity
{
        private EditText txtOldPass,txtNewPass,txtConPass;
        private AlertDialogClass dialogClass;
        private NeedSomeMethod someMethod;
        private DatabaseBackgroundTask backgroundTask;
        private CheckInternetIsOn internetIsOn;
        private SharedPreferenceData sharedPreferenceData;
        private DesEncryptionAlgo encryptionAlgo;

        private String newPass,encryptNewPas;
        private static final String REMEMBER_ME = "rememberMe";

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.change_password);
                setToolbar();
                initComponent();
        }

        private void initComponent()
        {
                txtOldPass = findViewById(R.id.txtOldPass);
                txtNewPass = findViewById(R.id.txtNewPass);
                txtConPass = findViewById(R.id.txtConPass);
                AdView adView = findViewById(R.id.adView);
                Button bChange = findViewById(R.id.bChange);

                someMethod = new NeedSomeMethod(this);
                dialogClass = new AlertDialogClass(this);
                internetIsOn = new CheckInternetIsOn(this);
                backgroundTask = new DatabaseBackgroundTask(this);
                sharedPreferenceData = new SharedPreferenceData(this);
                encryptionAlgo = new DesEncryptionAlgo(this);

                someMethod.setAdmob(adView);

                bChange.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                String oldPass,conPass;
                                oldPass = txtOldPass.getText().toString();
                                newPass = txtNewPass.getText().toString();
                                conPass = txtConPass.getText().toString();
                                if(checkPassword(oldPass,newPass,conPass))
                                        changePassword(newPass);
                        }
                });
        }

        //set a toolbar,above the page
        protected void setToolbar()
        {
                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
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

        private void changePassword(String newPass)
        {
                if(internetIsOn.isOnline())
                {
                        encryptNewPas = encryptionAlgo.encryptPass(newPass);
                        Log.d("encryption pass ",encryptNewPas);
                        try
                        {
                                String data = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getCurrentUserName(),"UTF-8")+"&"
                                        +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(encryptNewPas,"UTF-8");
                                backgroundTask = new DatabaseBackgroundTask(ChangePassword.this);
                                backgroundTask.setOnResultListener(asyncTaskInterface);
                                backgroundTask.execute(getResources().getString(R.string.changePassword),data);
                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }
                }
        }

        private boolean checkPassword(String pass1,String pass2,String pass3)
        {
                if((pass1.length()<6||pass1.length()>16)||(pass2.length()<6||pass2.length()>16)||(pass3.length()<6||pass3.length()>16))
                {
                        dialogClass.error("Password must be in 6-25.Please input valid password");
                        return false;
                }

                if(!pass2.equals(pass3))
                {
                        dialogClass.error("Password does not match.Please input valid password");
                        return false;
                }

                if(!encryptionAlgo.encryptPass(pass1).equals(sharedPreferenceData.getCurrentPassword()))
                {
                        dialogClass.error("Current password does not match.Please input valid password");
                        return false;
                }

                return true;
        }

        private OnAsyncTaskInterface asyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        switch (message)
                                        {
                                                case "success"://this method contain username ,password,and checkbox status
                                                        sharedPreferenceData.saveUserNamePassword(REMEMBER_ME,sharedPreferenceData.getCurrentUserName(),newPass,true);
                                                        sharedPreferenceData.currentUserInfo(sharedPreferenceData.getCurrentUserName(),encryptNewPas);
                                                        someMethod.progress("Changing password...","Your password has been changed.");
                                                        break;
                                                case "failed":
                                                        dialogClass.error("Execution failed.Please try again.");
                                                        break;
                                        }
                                }
                        });
                }
        };

}
