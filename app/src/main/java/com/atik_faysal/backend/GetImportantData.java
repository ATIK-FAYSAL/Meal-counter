package com.atik_faysal.backend;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.interfaces.InfoInterfaces;
import com.atik_faysal.mealcounter.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by USER on 2/28/2018.
 */

public class GetImportantData
{
        private Context context;
        private Activity activity;
        private DatabaseBackgroundTask backgroundTask;
        private SharedPreferenceData sharedPreferenceData;
        private CheckInternetIsOn internetIsOn;
        private AlertDialogClass dialogClass;
        private InfoInterfaces infoInterfaces;

        public GetImportantData(Context context)
        {
                this.context = context;
                activity = (Activity)context;
                sharedPreferenceData = new SharedPreferenceData(context);
                internetIsOn = new CheckInternetIsOn(context);
                dialogClass = new AlertDialogClass(context);
        }

        //get current user group type
        public void myGroupType(String currentUser)
        {
                //String url = "http://192.168.56.1/alreadyMember.php";
                String data;
                if(internetIsOn.isOnline())
                {
                        try {
                                data = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8")+"&"
                                +URLEncoder.encode("action","UTF-8")+"="+URLEncoder.encode("type","UTF-8");
                                backgroundTask = new DatabaseBackgroundTask(context);
                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                backgroundTask.execute(context.getResources().getString(R.string.alreadyMember),data);
                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }
                }else dialogClass.noInternetConnection();
        }


        //get all shopping list from database
        public void getAllShoppingCost(String FILE,String DATA,InfoInterfaces interfaces)
        {
                this.infoInterfaces = interfaces;
                if(internetIsOn.isOnline())
                {
                        //DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getCurrentUserName(),"UTF-8");
                        backgroundTask = new DatabaseBackgroundTask(context);
                        backgroundTask.setOnResultListener(taskInterface);
                        backgroundTask.execute(FILE,DATA);
                }
        }


        public void getCurrentSession(String user)
        {
                String data;
                if(internetIsOn.isOnline())
                {
                        try {
                                data = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(user,"UTF-8")+"&"
                                        +URLEncoder.encode("action","UTF-8")+"="+URLEncoder.encode("session","UTF-8");
                                backgroundTask = new DatabaseBackgroundTask(context);
                                backgroundTask.setOnResultListener(anInterface);
                                backgroundTask.execute(context.getResources().getString(R.string.alreadyMember),data);
                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }
                }else dialogClass.noInternetConnection();
        }

        //get shopping list as json format
        private OnAsyncTaskInterface taskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String result) {
                        activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        if(result!=null)
                                                infoInterfaces.getInfo(result);

                                }
                        });
                }
        };

        //group type
        private OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String result) {
                        activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        if(!result.equals("failed"))
                                                sharedPreferenceData.myGroupType(result);
                                }
                        });
                }
        };


        private OnAsyncTaskInterface anInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(String message) {
                        sharedPreferenceData.myCurrentSession(message);
                }
        };

}
