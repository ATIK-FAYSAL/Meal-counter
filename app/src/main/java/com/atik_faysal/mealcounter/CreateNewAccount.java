package com.atik_faysal.mealcounter;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.atik_faysal.backend.*;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;

import io.fabric.sdk.android.services.common.CommonUtils;

/**
 * initComponent-->Void.    initialize all component and object,also call some method.
 * userInformation-->Void.  get userInformation from component.
 * onButtonClick-->Void.    start action by clicking on button.
 * checkUserInformation-->Boolean.  check user information,if  information follow condition then return true ,otherwise return false
 */


public class CreateNewAccount extends AppCompatActivity
{
        //phone number verification variable
        private static final int FRAMEWORK_REQUEST_CODE = 1;
        private int nextPermissionsRequestCode = 4000;
        private final Map<Integer, OnCompleteListener> permissionsListeners = new HashMap<>();
        //finish

        //component variable
        private EditText eName,eUserName,ePassword,eAddress,eEmail,eFavourite;
        private TextView txtSign,txtProceed;
        private ProgressBar progressBar;
        private Toolbar toolbar;
        private LinearLayout layout2,layout3,layout4,layout5,layout6,layout7;
        //String variable declaration
        private String name,userName,address,password,email,favouriteWord;
        private TextView txtPassErr;

        private final static String FILE_URL = "http://192.168.56.1/userNameExist.php";
        private final static String FILE = "http://192.168.56.1/insert_member_info.php";
        private static String POST_DATA ;
        private final static String USER_LOGIN = "userLogIn";

        //class object declaration
        private DatabaseBackgroundTask informationCheck;
        private CheckInternetIsOn internetIsOn;
        private AlertDialogClass dialogClass;
        private NeedSomeMethod someMethod;
        private SharedPreferenceData sharedPreferenceData;
        DatabaseBackgroundTask databaseBackgroundTask;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.create_account);
                initComponent();//initialize all component and variable
                onTextChangeListener();
                setToolbar();
        }

        private interface OnCompleteListener {
                void onComplete();
        }

        // //initialize all user information related variable by getText from textView or editText
        private void initComponent()
        {
                //component initialize
                eName = findViewById(R.id.txtName);
                eUserName = findViewById(R.id.txtUserName);
                eEmail = findViewById(R.id.txtEmail);
                eAddress = findViewById(R.id.txtAddress);
                ePassword = findViewById(R.id.txtPassword);
                txtSign = findViewById(R.id.txtSignIn);
                txtProceed = findViewById(R.id.txtProceed);
                eFavourite = findViewById(R.id.txtFavourite);
                progressBar = findViewById(R.id.progress);
                toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);


                layout2 = findViewById(R.id.layout2);
                layout3 = findViewById(R.id.layout3);
                layout4 = findViewById(R.id.layout4);
                layout5 = findViewById(R.id.layout5);
                layout6 = findViewById(R.id.layout6);
                layout7 = findViewById(R.id.layout7);
                txtPassErr = findViewById(R.id.passErr);

                //object initialize
                informationCheck = new DatabaseBackgroundTask(this);
                internetIsOn = new CheckInternetIsOn(this);
                dialogClass = new AlertDialogClass(this);
                someMethod = new NeedSomeMethod(this);
                sharedPreferenceData = new SharedPreferenceData(CreateNewAccount.this);
                //calling method
                onButtonClick();
        }

        //setBackgroundColor(getResources().getColor(CommonUtils.getNextRandomColor()));

        //onText change listener,action will be change on text change
        private void onTextChangeListener()
        {
                eName.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable s) {
                                boolean flag = true;
                                if(eName.getText().toString().length()<3||eName.getText().toString().length()>20)
                                        layout3.setBackground(getDrawable(R.color.error));
                                else
                                {
                                        for(int i=0;i<eName.length();i++)
                                        {
                                                if(((eName.getText().toString().charAt(i)>='a')&&(eName.getText().toString().charAt(i)<='z'))||
                                                        ((eName.getText().toString().charAt(i)>='A')&&(eName.getText().toString().charAt(i)<='Z'))||
                                                        eName.getText().toString().charAt(i)==' '||eName.getText().toString().charAt(i)==':'||eName.getText().toString().charAt(i)=='.'||eName.getText().toString().charAt(i)=='_')
                                                        layout3.setBackground(getDrawable(R.color.green));
                                                else flag = false;
                                        }
                                        if(!flag)layout3.setBackground(getDrawable(R.color.error));
                                }
                        }
                });

                eUserName.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable s) {
                                if(eUserName.getText().toString().length()<5||eUserName.getText().toString().length()>15)
                                        layout2.setBackground(getDrawable(R.color.error));
                                else
                                        layout2.setBackground(getDrawable(R.color.green));
                        }
                });

                eEmail.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable s) {
                                boolean flag = true;
                                if(!eEmail.getText().toString().contains("@"))
                                        flag = false;
                                else
                                {
                                        String[] email = eEmail.getText().toString().split("@");
                                        if(email[0].length()<3||email[0].length()>30)
                                                flag = false;
                                }

                                if(flag)
                                        layout4.setBackground(getDrawable(R.color.green));
                                else
                                        layout4.setBackground(getDrawable(R.color.error));
                        }
                });


                eAddress.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable s) {
                                if(eAddress.getText().toString().length()<10||eAddress.getText().toString().length()>30)
                                        layout5.setBackground(getDrawable(R.color.error));
                                else layout5.setBackground(getDrawable(R.color.green));
                        }
                });

                ePassword.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable s) {
                                if(ePassword.getText().toString().length()<6||eAddress.getText().toString().length()>15)
                                        layout6.setBackground(getDrawable(R.color.error));
                                else layout6.setBackground(getDrawable(R.color.green));
                                if(ePassword.getText().toString().length()>=6&&ePassword.getText().toString().length()<=9)
                                        txtPassErr.setText("Too short");
                                else if(ePassword.getText().toString().length()>=10&&ePassword.getText().toString().length()<=13)
                                {
                                        txtPassErr.setText("Medium");
                                        txtPassErr.setTextColor(Color.parseColor("#00E676"));
                                }
                                else if(ePassword.getText().toString().length()>=14&&ePassword.getText().toString().length()<=15)
                                {
                                        txtPassErr.setText("Strong");
                                        txtPassErr.setTextColor(Color.parseColor("#00E676"));
                                }
                        }
                });

                eFavourite.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable s) {
                                if(eFavourite.getText().toString().length()<4||eFavourite.getText().toString().length()>15)
                                        layout7.setBackground(getDrawable(R.color.error));
                                else
                                        layout7.setBackground(getDrawable(R.color.green));
                        }
                });
        }

        //set a toolbar,above the page
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

        //initialize all user information related variable by getText from textView or editText
        private void userInformation()
        {
                name = eName.getText().toString();
                userName = eUserName.getText().toString();
                email = eEmail.getText().toString();
                password = ePassword.getText().toString();
                password = someMethod.encryptPassword(password);//initialize new encrypt password
                address = eAddress.getText().toString();
                favouriteWord = eFavourite.getText().toString();
        }

        //when user click on button,this method action will start,
        private void onButtonClick()
        {
                txtSign.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                startActivity(new Intent(CreateNewAccount.this,LogInActivity.class));
                                finish();
                        }
                });

                txtProceed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                                databaseBackgroundTask = new DatabaseBackgroundTask(CreateNewAccount.this);
                                userInformation();//method

                                if(internetIsOn.isOnline())
                                {
                                        if(checkUserInformation(name,userName,email,password))
                                        {
                                                try {
                                                        POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8");
                                                        databaseBackgroundTask.setOnResultListener(onAsyncTaskInterface);
                                                        databaseBackgroundTask.execute(FILE_URL,POST_DATA);
                                                }catch(UnsupportedEncodingException e) {
                                                        e.printStackTrace();
                                                }
                                        }
                                }else dialogClass.noInternetConnection();
                        }
                });
        }
        /*
               check all user information.
               return boolean type value.when user input value to follow all input condition then it will return true,otherwise return false.
         */
        private boolean checkUserInformation(String name,String userName,String email,String password)
        {
                boolean flag = true;

                if(userName.isEmpty())
                {
                        eUserName.setError("empty field");
                        return false;
                }

                if(name.isEmpty())
                {
                        eName.setError("empty field");
                        return false;
                }

                if(email.isEmpty())
                {
                        eEmail.setError("empty field");
                        return false;
                }
                if(password.isEmpty())
                {
                        ePassword.setError("empty field");
                        return false;
                }


                if(password.length()<6)
                {
                        ePassword.setError("Invalid password");
                        ePassword.requestFocus();
                        flag = false;
                }
                if(password.length()>15)
                {
                        ePassword.setError("Invalid password");
                        ePassword.requestFocus();
                        flag =false;
                }

                if(eAddress.getText().toString().isEmpty())
                {
                        eAddress.setError("Invalid address");
                        flag = false;
                        eAddress.requestFocus();
                }

                if(eFavourite.getText().toString().isEmpty())
                {
                        eFavourite.setError("empty field");
                        flag = false;
                        eFavourite.requestFocus();
                }

                if(!email.contains("@"))
                {
                        eEmail.setError("Invalid email");
                        eEmail.requestFocus();
                        flag = false;
                }

                if(name.length()<3)
                {
                        eName.setError("Invalid name");
                        eName.requestFocus();
                        flag = false;
                }

                for(char c : name.toCharArray()){
                        if(Character.isDigit(c)){
                                eName.setError("Invalid Name");
                                flag = false;
                                eName.requestFocus();
                        }
                }

                if(userName.length()<5)
                {
                        eUserName.setError("User name must be in 6 to 12 character");
                        eUserName.requestFocus();
                        flag = false;
                }

                if(eAddress.getText().toString().length()<10||eAddress.getText().toString().length()>30)
                {
                        flag = false;
                        eAddress.setError("Must be in 10-30 characters");
                        eAddress.requestFocus();
                }
                return flag;
        }

        //new user registration process
        private void newUserRegistration(String phoneNumber)
        {
               if(internetIsOn.isOnline())
               {
                       try {
                               String memberType = "nope";
                               String taka = "0";
                               String groupId = "Null";

                               POST_DATA = URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"
                                       +URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8")+"&"
                                       +URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"
                                       +URLEncoder.encode("phone","UTF-8")+"="+URLEncoder.encode(phoneNumber,"UTF-8")+"&"
                                       +URLEncoder.encode("address","UTF-8")+"="+URLEncoder.encode(address,"UTF-8")+"&"
                                       +URLEncoder.encode("taka","UTF-8")+"="+URLEncoder.encode(taka,"UTF-8")+"&"
                                       +URLEncoder.encode("memberType","UTF-8")+"="+URLEncoder.encode(memberType,"UTF-8")+"&"
                                       +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8")+"&"
                                       +URLEncoder.encode("groupId","UTF-8")+"="+URLEncoder.encode(groupId,"UTF-8")+"&"
                                       +URLEncoder.encode("date","UTF-8")+"="+URLEncoder.encode(someMethod.getDate(),"UTF-8")+"&"
                                       +URLEncoder.encode("favouriteWord","UTF-8")+"="+URLEncoder.encode(favouriteWord,"UTF-8");

                               informationCheck = new DatabaseBackgroundTask(this);
                               informationCheck.setOnResultListener(taskInterface);
                               informationCheck.execute(FILE,POST_DATA);
                       } catch (UnsupportedEncodingException e) {
                               e.printStackTrace();
                       }
               }else dialogClass.noInternetConnection();
        }


        //interface,use for check if username is already exist or not ...
        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {

                @Override
                public void onResultSuccess(final String result) {
                        runOnUiThread(new Runnable() {
                                public void run() {
                                        switch (result) {
                                                case "success":
                                                        onLogin(LoginType.PHONE);
                                                        break;
                                                case "offline":
                                                        dialogClass.noInternetConnection();
                                                        break;
                                                default:
                                                        dialogClass.error("Execution failed.Please try again with another username.");
                                                        break;
                                        }
                                }
                        });
                }
        };

        //interface,use for if user information successfully insert or not.....
        OnAsyncTaskInterface taskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String result) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        switch (result)
                                        {
                                                case "success"://insert_member_info.php file return success
                                                        Thread thread = new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                        try
                                                                        {
                                                                                Thread.sleep(2500);

                                                                                //current user info,user log in status,user type save in shared preference
                                                                                sharedPreferenceData.ifUserLogIn(USER_LOGIN,true);
                                                                                sharedPreferenceData.currentUserInfo(userName,password);
                                                                                sharedPreferenceData.userType("member");
                                                                                someMethod.closeActivity(CreateNewAccount.this,HomePageActivity.class);
                                                                                finish();
                                                                        }catch (InterruptedException e)
                                                                        {
                                                                                e.printStackTrace();
                                                                        }
                                                                }
                                                        });
                                                        thread.start();
                                                        break;
                                                default:
                                                        dialogClass.error("Execution failed.please try again after sometimes.");
                                                        break;
                                        }
                                }
                        });
                }
        };



        //phone number verification with facebook kid

        @Override
        protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
        {

                super.onActivityResult(requestCode, resultCode, data);

                if (requestCode != FRAMEWORK_REQUEST_CODE) {
                        return;
                }

                final String toastMessage;
                final AccountKitLoginResult loginResult = AccountKit.loginResultWithIntent(data);
                if (loginResult == null || loginResult.wasCancelled()) {
                        toastMessage = "Cancelled";
                        Toast.makeText(CreateNewAccount.this, toastMessage, Toast.LENGTH_SHORT).show();
                } else if (loginResult.getError() != null) {
                        Toast.makeText(CreateNewAccount.this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                        final AccessToken accessToken = loginResult.getAccessToken();
                        if (accessToken != null) {

                                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                                        @Override
                                        public void onSuccess(final Account account) {
                                                String phoneNumber = account.getPhoneNumber().toString();

                                                if(phoneNumber!=null)
                                                {
                                                        if(phoneNumber.length()==14)
                                                                phoneNumber = phoneNumber.substring(3);

                                                        newUserRegistration(phoneNumber);
                                                        progressBar.setVisibility(View.VISIBLE);
                                                }
                                        }

                                        @Override
                                        public void onError(final AccountKitError error) {
                                        }
                                });
                        } else {
                                toastMessage = "Unknown response type";
                                Toast.makeText(CreateNewAccount.this, toastMessage, Toast.LENGTH_SHORT).show();
                        }
                }

        }

        public void onLogin(final LoginType loginType)
        {
                final Intent intent = new Intent(CreateNewAccount.this, AccountKitActivity.class);
                final AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder = new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType, AccountKitActivity.ResponseType.TOKEN);
                final AccountKitConfiguration configuration = configurationBuilder.build();
                intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configuration);
                OnCompleteListener completeListener = new OnCompleteListener() {
                        @Override
                        public void onComplete() {
                                startActivityForResult(intent, FRAMEWORK_REQUEST_CODE);
                        }
                };
                if (configuration.isReceiveSMSEnabled() && !canReadSmsWithoutPermission()) {
                        final OnCompleteListener receiveSMSCompleteListener = completeListener;
                        completeListener = new OnCompleteListener() {
                                @Override
                                public void onComplete() {
                                        requestPermissions(
                                                android.Manifest.permission.RECEIVE_SMS,
                                                com.atik_faysal.mealcounter.R.string.permissions_receive_sms_title,
                                                com.atik_faysal.mealcounter.R.string.permissions_receive_sms_message,
                                                receiveSMSCompleteListener);
                                }
                        };
                }
                if (configuration.isReadPhoneStateEnabled() && !isGooglePlayServicesAvailable()) {
                        final OnCompleteListener readPhoneStateCompleteListener = completeListener;
                        completeListener = new OnCompleteListener() {
                                @Override
                                public void onComplete() {
                                        requestPermissions(
                                                Manifest.permission.READ_PHONE_STATE,
                                                com.atik_faysal.mealcounter.R.string.permissions_read_phone_state_title,
                                                com.atik_faysal.mealcounter.R.string.permissions_read_phone_state_message,
                                                readPhoneStateCompleteListener);
                                }
                        };
                }
                completeListener.onComplete();
        }

        private boolean isGooglePlayServicesAvailable() {
                final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
                int googlePlayServicesAvailable = apiAvailability.isGooglePlayServicesAvailable(CreateNewAccount.this);
                return googlePlayServicesAvailable == ConnectionResult.SUCCESS;
        }

        private boolean canReadSmsWithoutPermission() {
                final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
                int googlePlayServicesAvailable = apiAvailability.isGooglePlayServicesAvailable(CreateNewAccount.this);
                if (googlePlayServicesAvailable == ConnectionResult.SUCCESS) {
                        return true;
                }
                return false;
        }

        private void requestPermissions(
                final String permission,
                final int rationaleTitleResourceId,
                final int rationaleMessageResourceId,
                final OnCompleteListener listener) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        if (listener != null) {
                                listener.onComplete();
                        }
                        return;
                }

                checkRequestPermissions(permission, rationaleTitleResourceId, rationaleMessageResourceId, listener);
        }

        @TargetApi(23)
        private void checkRequestPermissions(final String permission, final int rationaleTitleResourceId, final int rationaleMessageResourceId, final OnCompleteListener listener) {
                if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                        if (listener != null) {
                                listener.onComplete();
                        }
                        return;
                }

                final int requestCode = nextPermissionsRequestCode++;
                permissionsListeners.put(requestCode, listener);

                if (shouldShowRequestPermissionRationale(permission)) {
                        new AlertDialog.Builder(this).setTitle(rationaleTitleResourceId).setMessage(rationaleMessageResourceId).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, final int which) {
                                        requestPermissions(new String[] { permission }, requestCode);
                                }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, final int which) {
                                        permissionsListeners.remove(requestCode);
                                }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();
                } else requestPermissions(new String[]{ permission }, requestCode);

        }

        @TargetApi(23)
        @SuppressWarnings("unused")
        @Override
        public void onRequestPermissionsResult(final int requestCode, final @NonNull String permissions[], final @NonNull int[] grantResults) {
                final OnCompleteListener permissionsListener = permissionsListeners.remove(requestCode);
                if (permissionsListener != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        permissionsListener.onComplete();
                }
        }
}