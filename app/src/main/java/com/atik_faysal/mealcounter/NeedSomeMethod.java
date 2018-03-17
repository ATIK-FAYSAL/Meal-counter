package com.atik_faysal.mealcounter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.backend.SharedPreferenceData;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by USER on 1/25/2018.
 */

public class NeedSomeMethod
{

        Context context;
        Activity activity;
        DatabaseBackgroundTask backgroundTask;
        SharedPreferenceData sharedPreferenceData;
        CheckInternetIsOn internetIsOn;
        AlertDialogClass dialogClass;


        StringBuilder encryptPass ;

        //alphabet array
        String[] capitalLatter = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        String[] encryptCap = {"F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","A","B","C","D","E"};

        String[] smallLatter = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
        String[] encryptSma = {"f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","a","b","c","d","e"};

        public NeedSomeMethod(Context context)
        {
                this.context = context;
                activity = (Activity)context;
                sharedPreferenceData = new SharedPreferenceData(context);
                internetIsOn = new CheckInternetIsOn(context);
                dialogClass = new AlertDialogClass(context);
        }

        //get current time and date
        public String getDate()
        {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MMM.dd hh:mm aaa");
                String date = dateFormat.format(calendar.getTime());
                return date;
        }

        //encrypt password key=5 using cisear cipher
        public String encryptPassword(String pass)
        {
                encryptPass = new StringBuilder();
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
                                },3000);
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

        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String result) {
                        activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        if(!result.equals("failed"))
                                                sharedPreferenceData.myGroup(result);
                                }
                        });
                }
        };

}
