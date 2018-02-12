package com.atik_faysal.mealcounter;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.atik_faysal.backend.InfoBackgroundTask.OnAsyncTaskInterface;

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

        private OnAsyncTaskInterface onAsyncTaskInterface;

        public AlertDialogClass(Context context)
        {
                this.context = context;
                builder = new AlertDialog.Builder(context);
                activity = (Activity) context;
        }

        public void onSuccessListener(OnAsyncTaskInterface onAsyncTaskInterface)
        {
                if(onAsyncTaskInterface!=null)
                        this.onAsyncTaskInterface = onAsyncTaskInterface;
        }

        public void noInternetConnection()
        {
                Button bSetting;
                builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_no_internet,null);

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

        public void notMember()
        {
                Button bOk;
                TextView text1,text2;

                builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_not_member,null);

                bOk = view.findViewById(R.id.bOk);
                text1 = view.findViewById(R.id.text1);
                text2 = view.findViewById(R.id.text2);

                text1.setText("You are not a member of any group !");
                text2.setText("Please first join a group.");


                builder.setView(view);
                builder.setCancelable(false);
                alertDialog = builder.create();
                alertDialog.show();

                bOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                alertDialog.dismiss();
                                activity.finish();
                        }
                });
        }

        public void alreadyMember(String value)
        {
                Button bOk;
                TextView text1,text2;

                builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_not_member,null);

                text1 = view.findViewById(R.id.text1);
                text2 = view.findViewById(R.id.text2);

                text1.setText(value);
                text2.setText("Please first Leave from previous group and retry.");



                bOk = view.findViewById(R.id.bOk);
                builder.setView(view);
                builder.setCancelable(false);
                alertDialog = builder.create();
                alertDialog.show();

                bOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                alertDialog.dismiss();
                                activity.finish();
                        }
                });
        }

        public void error(String value)
        {
                Button bOk;
                TextView text1;

                builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_error,null);

                text1 = view.findViewById(R.id.text1);

                text1.setText(value);


                bOk = view.findViewById(R.id.bOk);
                builder.setView(view);
                builder.setCancelable(false);
                alertDialog = builder.create();
                alertDialog.show();

                bOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                alertDialog.dismiss();
                        }
                });
        }

        public void warning(String value)
        {
                builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_warning,null);

                builder.setView(view);
                builder.setCancelable(false);

                final Button bYes,bNo;
                TextView txtWarning;

                bYes = view.findViewById(R.id.bYes);
                bNo = view.findViewById(R.id.bNo);
                txtWarning = view.findViewById(R.id.text);

                txtWarning.setText(value);

                alertDialog = builder.create();
                alertDialog.show();

                bYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                onAsyncTaskInterface.onResultSuccess("yes");
                                alertDialog.dismiss();
                        }
                });

                bNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                onAsyncTaskInterface.onResultSuccess("no");
                                alertDialog.dismiss();
                        }
                });

        }

        public void success(String value)
        {
                Button bOk;
                ImageView imageView;
                TextView text1;

                builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_error,null);

                text1 = view.findViewById(R.id.text1);
                imageView = view.findViewById(R.id.imageSign);

                text1.setText(value);
                imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.icon_happy));

                bOk = view.findViewById(R.id.bOk);
                builder.setView(view);
                builder.setCancelable(false);
                alertDialog = builder.create();
                alertDialog.show();

                bOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                alertDialog.dismiss();
                        }
                });
        }
}
