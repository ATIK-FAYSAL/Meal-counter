package com.atik_faysal.mealcounter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.atik_faysal.backend.InfoBackgroundTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by USER on 1/25/2018.
 */

public class NeedSomeMethod
{

        Context context;
        Activity activity;

        public NeedSomeMethod(Context context)
        {
                this.context = context;
                activity = (Activity)context;
        }


        public String getDate()
        {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MMM.dd hh:mm aaa");
                String date = dateFormat.format(calendar.getTime());
                return date;
        }


        public void closeActivity(Activity context, Class<?> clazz) {
                Intent intent = new Intent(context, clazz);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                context.finish();
        }


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

}
