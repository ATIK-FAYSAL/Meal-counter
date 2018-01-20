package com.atik_faysal.backend;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;


import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;
import com.atik_faysal.mealcounter.CreateNewAccount;



/**
 * Created by USER on 1/19/2018.
 */

public class PhoneNumberVerification extends AppCompatActivity
{

        private static final int FRAMEWORK_REQUEST_CODE = 1;

        private int nextPermissionsRequestCode = 4000;
        private final Map<Integer, OnCompleteListener> permissionsListeners = new HashMap<>();
        private Context context;

        private CreateNewAccount createNewAccount;

        public PhoneNumberVerification(Context context)
        {
                this.context = context;
                activity = (Activity)context;
                createNewAccount = new CreateNewAccount();
        }

        private Activity activity;


        public interface OnCompleteListener {
                void onComplete();
        }

        @Override
        protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
        {

                super.onActivityResult(requestCode, resultCode, data);

                if (requestCode != FRAMEWORK_REQUEST_CODE) {
                        return;
                }

                final String toastMessage;
                final AccountKitLoginResult loginResult = AccountKit.loginResultWithIntent(data);
                if (loginResult == null || loginResult.wasCancelled())
                {
                        toastMessage = "Cancelled";
                        Toast.makeText(context,toastMessage,Toast.LENGTH_SHORT).show();
                }
                else if (loginResult.getError() != null) {
                        Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
                } else {
                        final AccessToken accessToken = loginResult.getAccessToken();
                        if (accessToken != null) {

                                AccountKit.getCurrentAccount(new  AccountKitCallback<Account>() {
                                        @Override
                                        public void onSuccess(final Account account) {
                                                Toast.makeText(context,"phone: "+account.getPhoneNumber(),Toast.LENGTH_LONG).show();
                                                //createNewAccount.getPhoneNumber(account.getPhoneNumber().toString());
                                        }

                                        @Override
                                        public void onError(final AccountKitError error) {
                                        }
                                });
                        } else {
                                toastMessage = "Unknown response type";
                                Toast.makeText(context,toastMessage,Toast.LENGTH_SHORT).show();
                        }
                }

        }

        public void onLogin(final LoginType loginType)
        {
                final Intent intent = new Intent(context, AccountKitActivity.class);
                final AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder = new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType, AccountKitActivity.ResponseType.TOKEN);
                final AccountKitConfiguration configuration = configurationBuilder.build();
                intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configuration);
                OnCompleteListener completeListener = new OnCompleteListener() {
                        @Override
                        public void onComplete() {
                                activity.startActivityForResult(intent, FRAMEWORK_REQUEST_CODE);
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
                int googlePlayServicesAvailable = apiAvailability.isGooglePlayServicesAvailable(context);
                return googlePlayServicesAvailable == ConnectionResult.SUCCESS;
        }

        private boolean canReadSmsWithoutPermission() {
                final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
                int googlePlayServicesAvailable = apiAvailability.isGooglePlayServicesAvailable(context);
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
        public void onRequestPermissionsResult(final int requestCode,final @NonNull String permissions[], final @NonNull int[] grantResults) {
                final OnCompleteListener permissionsListener = permissionsListeners.remove(requestCode);
                if (permissionsListener != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        permissionsListener.onComplete();
                }
        }
}
