package com.atik_faysal.mealcounter;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.EditText;
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
import com.atik_faysal.backend.InformationCheckBackgroundTask.OnAsyncTaskInterface;

/**
 * initComponent-->Void.    initialize all component and object,also call some method.
 * userInformation-->Void.  get userInformation from component.
 * onButtonClick-->Void.    start action by clicking on button.
 * checkUserInformation-->Boolean.  check user information,if  information follow condition then return true ,otherwise return false
 * getDate-->String ,get current date from system and return
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

        //String variable declaration
        private String name,userName,address,password,email,favouriteWord;
        private final static String FILE_URL = "http://192.168.56.1/userNameExist.php";
        private static String POST_DATA ;

        //class object declaration
        private CreateMemberBackgroundTask createMemberBackgroundTask;
        private InformationCheckBackgroundTask informationCheck;
        private CheckInternetIsOn internetIsOn;
        private AlertDialogClass dialogClass;
        private NeedSomeMethod someMethod;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.create_account);
                initComponent();//initialize all component and variable
        }



        private interface OnCompleteListener {
                void onComplete();
        }

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
                //object initialize
                //numberVerification = new PhoneNumberVerification(this);
                createMemberBackgroundTask = new CreateMemberBackgroundTask(this);
                informationCheck = new InformationCheckBackgroundTask(this);
                internetIsOn = new CheckInternetIsOn(this);
                dialogClass = new AlertDialogClass(this);
                someMethod = new NeedSomeMethod();
                //calling method
                onButtonClick();
        }

        private void userInformation()
        {
                name = eName.getText().toString();
                userName = eUserName.getText().toString();
                email = eEmail.getText().toString();
                password = ePassword.getText().toString();
                address = eAddress.getText().toString();
                favouriteWord = eFavourite.getText().toString();
        }

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

                                InformationCheckBackgroundTask informationCheckBackgroundTask = new InformationCheckBackgroundTask(CreateNewAccount.this);

                                userInformation();

                                if(internetIsOn.isOnline())
                                {
                                        if(checkUserInformation(name,userName,email,password))
                                        {
                                                try {
                                                        POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8");
                                                }catch(UnsupportedEncodingException e) {
                                                        e.printStackTrace();
                                                }

                                                informationCheckBackgroundTask.setOnResultListener(onAsyncTaskInterface);
                                                informationCheckBackgroundTask.execute(FILE_URL,POST_DATA);
                                        }
                                }else dialogClass.noInternetConnection();
                        }
                });
        }

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
                        ePassword.setError("Too short");
                        ePassword.requestFocus();
                        flag = false;
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
                return flag;
        }


        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {

                @Override
                public void onResultSuccess(final String message) {
                        runOnUiThread(new Runnable() {
                                public void run() {
                                        switch (message) {
                                                case "not exist":
                                                        onLogin(LoginType.PHONE);
                                                        break;
                                                case "offline":
                                                        dialogClass.noInternetConnection();
                                                        break;
                                                default:
                                                        eUserName.setError("already exist");
                                                        break;
                                        }
                                }
                        });
                }
        };


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
                        finish();
                        Toast.makeText(CreateNewAccount.this, toastMessage, Toast.LENGTH_SHORT).show();
                } else if (loginResult.getError() != null) {
                        Toast.makeText(CreateNewAccount.this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                        final AccessToken accessToken = loginResult.getAccessToken();
                        if (accessToken != null) {

                                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                                        @Override
                                        public void onSuccess(final Account account) {
                                                //Toast.makeText(CreateNewAccount.this,account.getPhoneNumber().toString()+" phone number verified",Toast.LENGTH_SHORT).show();

                                                new CreateMemberBackgroundTask(CreateNewAccount.this).execute("insertMember",name,userName,email,account.getPhoneNumber().toString(),address,password,someMethod.getDate(),favouriteWord);
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
