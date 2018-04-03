package com.atik_faysal.others;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by USER on 2/21/2018.
 */

public class NoResultFound
{
        Context context;
        Activity activity;
        CheckInternetIsOn internetIsOn;
        DatabaseBackgroundTask backgroundTask;
        AlertDialogClass dialogClass;
        Class<?> nameOfClass;

        public NoResultFound(Context context)
        {
                this.context = context;
                activity = (Activity)context;
                internetIsOn = new CheckInternetIsOn(context);
                dialogClass = new AlertDialogClass(context);
        }

        public void checkJoinRequest(String userName,Class<?>nameOfClass,String action)
        {
                this.nameOfClass = nameOfClass;
                String post ;
                //String url = "http://192.168.56.1/noResultFound.php";

                if(internetIsOn.isOnline())
                {
                        try {
                                post = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8")+"&"
                                        +URLEncoder.encode("action","UTF-8")+"="+URLEncoder.encode(action,"UTF-8");
                                backgroundTask = new DatabaseBackgroundTask(context);
                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                backgroundTask.execute(context.getResources().getString(R.string.noResultFound),post);
                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }
                }else dialogClass.noInternetConnection();
        }


        private OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String result) {
                        activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        switch (result)
                                        {
                                                case "success":
                                                        context.startActivity(new Intent(context,nameOfClass));
                                                        break;

                                                default:
                                                        context.startActivity(new Intent(context,EmptyActivity.class));
                                                        break;
                                        }
                                }
                        });
                }
        };

}
