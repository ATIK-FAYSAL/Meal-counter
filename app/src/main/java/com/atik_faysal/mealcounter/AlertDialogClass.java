package com.atik_faysal.mealcounter;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by USER on 1/21/2018.
 * noInternetConnection-->if user is in offline ,it show a message
 */

public class AlertDialogClass
{
        Context context;
        AlertDialog.Builder builder;
        AlertDialog alertDialog;

        Activity activity;

        public AlertDialogClass(Context context)
        {
                this.context = context;
                builder = new AlertDialog.Builder(context);
                activity = (Activity) context;
        }

        public void noInternetConnection()
        {
                Button bSetting;
                builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.internet_alert,null);

                bSetting = view.findViewById(R.id.bSetting);
                builder.setView(view);

                alertDialog = builder.create();
                alertDialog.show();

                bSetting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                                alertDialog.dismiss();
                        }
                });
        }

}
