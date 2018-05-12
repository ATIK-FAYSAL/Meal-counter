package com.atik_faysal.mealcounter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.WindowManager;
import android.widget.Toast;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.backend.SharedPreferenceData;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by USER on 1/25/2018.
 */

public class NeedSomeMethod
{

        private Context context;
        private Activity activity;
        DatabaseBackgroundTask backgroundTask;
        SharedPreferenceData sharedPreferenceData;
        CheckInternetIsOn internetIsOn;
        AlertDialogClass dialogClass;


        //alphabet array
        private String[] capitalLatter = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        private String[] encryptCap = {"F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","A","B","C","D","E"};

        private String[] smallLatter = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
        private String[] encryptSma = {"f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","a","b","c","d","e"};

        public NeedSomeMethod(Context context)
        {
                this.context = context;
                activity = (Activity)context;
                sharedPreferenceData = new SharedPreferenceData(context);
                internetIsOn = new CheckInternetIsOn(context);
                dialogClass = new AlertDialogClass(context);
        }

        //get current time and date
        @SuppressLint("SimpleDateFormat")
        public String getDateWithTime()
        {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MMM.dd hh:mm aaa");
                return dateFormat.format(calendar.getTime());
        }

        //get current time and date
        @SuppressLint("SimpleDateFormat")
        public String getDate()
        {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMMM-dd");
                return dateFormat.format(calendar.getTime());
        }

        //get current time and date
        @SuppressLint("SimpleDateFormat")
        public String getMonth()
        {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMMM-dd");
                String date = dateFormat.format(calendar.getTime());
                String[] value = date.split("-");
                return value[1];
        }

        //encrypt password key=5 using cisear cipher
        public String encryptPassword(String pass)
        {
                StringBuilder encryptPass = new StringBuilder();
                int i=0;
                while(i<pass.length())
                {
                        if((pass.charAt(i)>='a'&&pass.charAt(i)<='z'))
                        {
                                for (int j=0;j<smallLatter.length;j++)
                                {
                                        if(smallLatter[j].equals(Character.toString(pass.charAt(i))))
                                        {
                                                encryptPass.append(encryptSma[j]);
                                                break;
                                        }
                                }
                        }else if ((pass.charAt(i)>='A'&&pass.charAt(i)<='Z'))
                        {
                                for (int j=0;j<smallLatter.length;j++)
                                {
                                        if(capitalLatter[j].equals(Character.toString(pass.charAt(i))))
                                        {
                                                encryptPass.append(encryptCap[j]);
                                                break;
                                        }
                                }
                        }else encryptPass.append(Character.toString(pass.charAt(i)));
                        i++;
                }

                return encryptPass.toString();
        }

        //close top all activity and go to specific activity
        public void closeActivity(Activity context, Class<?> clazz) {
                Intent intent = new Intent(context, clazz);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                context.finish();
        }

        //refresh current page
        public void reloadPage(final SwipeRefreshLayout refreshLayout, final Class<?>nameOfClass)
        {
                refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                                refreshLayout.setRefreshing(true);

                                (new Handler()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                                refreshLayout.setRefreshing(false);
                                                context.startActivity(new Intent(context,nameOfClass));
                                                activity.finish();
                                        }
                                },2500);
                        }
                });
        }

        //current user status
        public void userCurrentStatus(String currentUser,String status)
        {
                String url = "http://192.168.56.1/currentStatus.php";
                String data;
                try {
                        data = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8")+"&"
                                +URLEncoder.encode("status","UTF-8")+"="+URLEncoder.encode(status,"UTF-8");
                        backgroundTask = new DatabaseBackgroundTask(context);
                        backgroundTask.execute(url,data);
                } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                }
        }

        //get current user group name
        public void myGroupName(String currentUser)
        {
                String url = "http://192.168.56.1/alreadyMember.php";
                String data;
                if(internetIsOn.isOnline())
                {
                        try {
                                data = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8")+"&"
                                +URLEncoder.encode("action","UTF-8")+"="+URLEncoder.encode("name","UTF-8");
                                backgroundTask = new DatabaseBackgroundTask(context);
                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                backgroundTask.execute(url,data);
                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }
                }else dialogClass.noInternetConnection();
        }

        public void progressDialog(String message)
        {
                final ProgressDialog ringProgressDialog = ProgressDialog.show(context, "Please wait", message, true);
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
                                Toast.makeText(context,"Task complete.",Toast.LENGTH_SHORT).show();
                                activity.finish();
                        }
                });
        }

        public void progress(String message, final String toast)
        {
                final ProgressDialog ringProgressDialog = ProgressDialog.show(context, "Please wait", message, true);
                ringProgressDialog.setCancelable(true);
                new Thread(new Runnable() {
                        @Override
                        public void run() {
                                try {
                                        Thread.sleep(1500);
                                } catch (Exception e) {
                                }
                                ringProgressDialog.dismiss();
                        }
                }).start();
                ringProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                                Toast.makeText(context,toast,Toast.LENGTH_SHORT).show();
                        }
                });
        }

        public void closeSessionAlert(String message, final String path)
        {
                final ProgressDialog ringProgressDialog = ProgressDialog.show(context, "Please wait", message, true);
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
                               activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                iOSDialogBuilder builder = new iOSDialogBuilder(context);

                                builder.setTitle("SUCCESS")
                                        .setSubtitle("Your current session closed successfully.Monthly report save in your SD card ("+path+")")
                                        .setBoldPositiveLabel(true)
                                        .setCancelable(false)
                                        .setPositiveListener("ok",new iOSDialogClickListener() {
                                                @Override
                                                public void onClick(iOSDialog dialog) {
                                                        dialog.dismiss();
                                                        closeActivity(activity,HomePageActivity.class);

                                                }
                                        }).build().show();
                        }
                });
        }

        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String result) {
                        activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        if(!result.equals("failed"))
                                                sharedPreferenceData.myGroup(result);
                                        else if(result.equals("failed"))
                                                sharedPreferenceData.myGroup("nope");
                                }
                        });
                }
        };

}
