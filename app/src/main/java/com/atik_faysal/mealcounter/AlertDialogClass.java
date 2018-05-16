package com.atik_faysal.mealcounter;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;

/**
 * Created by USER on 1/21/2018.
 * noInternetConnection-->if user is in offline ,it show a message
 */

public class AlertDialogClass extends AlertDialog
{
        private Context context;
        private AlertDialog.Builder builder;
        private AlertDialog alertDialog;
        private CheckInternetIsOn internetIsOn;
        private NeedSomeMethod someMethod;

        private Activity activity;

        private OnAsyncTaskInterface onAsyncTaskInterface;

        //constructor
        public AlertDialogClass(Context context)
        {
                super(context);
                this.context = context;
                builder = new AlertDialog.Builder(context);
                activity = (Activity) context;
                internetIsOn = new CheckInternetIsOn(context);
        }

        public void onSuccessListener(OnAsyncTaskInterface onAsyncTaskInterface)
        {
                if(onAsyncTaskInterface!=null)
                        this.onAsyncTaskInterface = onAsyncTaskInterface;
        }

        //if you are not connected with internet
        public void noInternetConnection()
        {
                Button bSetting;
                builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_no_internet,null);
                bSetting = view.findViewById(R.id.bSetting);

                builder.setView(view);
                builder.setCancelable(false);
                alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();

                bSetting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                                alertDialog.dismiss();
                        }
                });
        }

        //if you are not a member of group
        public void notMember()
        {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                iOSDialogBuilder builder = new iOSDialogBuilder(context);

                builder.setTitle("Not member")
                        .setSubtitle("Please first join a group.")
                        .setBoldPositiveLabel(true)
                        .setCancelable(false)
                        .setPositiveListener("ok",new iOSDialogClickListener() {
                                @Override
                                public void onClick(iOSDialog dialog) {
                                        dialog.dismiss();
                                        activity.finish();

                                }
                        }).build().show();

        }

        //if you are already member
        public void alreadyMember(String message)
        {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                iOSDialogBuilder builder = new iOSDialogBuilder(context);

                builder.setTitle("Attention")
                        .setSubtitle(message)
                        .setBoldPositiveLabel(true)
                        .setCancelable(false)
                        .setPositiveListener("ok",new iOSDialogClickListener() {
                                @Override
                                public void onClick(iOSDialog dialog) {
                                        dialog.dismiss();
                                        activity.finish();
                                }
                        }).build().show();
        }

        //execution failed
        public void error(String message)
        {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                iOSDialogBuilder builder = new iOSDialogBuilder(context);

                builder.setTitle("Attention")
                        .setSubtitle(message)
                        .setBoldPositiveLabel(true)
                        .setCancelable(false)
                        .setPositiveListener("ok",new iOSDialogClickListener() {
                                @Override
                                public void onClick(iOSDialog dialog) {
                                        dialog.dismiss();

                                }
                        }).build().show();
        }

        //warning
        public void warning(String message)
        {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                iOSDialogBuilder builder = new iOSDialogBuilder(context);

                builder.setTitle("Warning")
                        .setSubtitle(message)
                        .setBoldPositiveLabel(true)
                        .setCancelable(false)
                        .setPositiveListener("Yes",new iOSDialogClickListener() {
                                @Override
                                public void onClick(iOSDialog dialog) {
                                        onAsyncTaskInterface.onResultSuccess("yes");
                                        dialog.dismiss();

                                }
                        }).setNegativeListener("No", new iOSDialogClickListener() {
                        @Override
                        public void onClick(iOSDialog dialog) {
                                onAsyncTaskInterface.onResultSuccess("no");
                                dialog.dismiss();
                        }
                }).build().show();
        }

        //if execution success
        public void success(String message)
        {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                iOSDialogBuilder builder = new iOSDialogBuilder(context);

                builder.setTitle("Success")
                        .setSubtitle(message)
                        .setBoldPositiveLabel(true)
                        .setCancelable(false)
                        .setPositiveListener("ok",new iOSDialogClickListener() {
                                @Override
                                public void onClick(iOSDialog dialog) {
                                        dialog.dismiss();

                                }
                        }).build().show();
        }
}
