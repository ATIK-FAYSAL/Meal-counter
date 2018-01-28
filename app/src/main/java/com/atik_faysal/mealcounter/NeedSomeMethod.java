package com.atik_faysal.mealcounter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by USER on 1/25/2018.
 */

public class NeedSomeMethod
{

        Context context;

        public NeedSomeMethod(Context context)
        {
                this.context = context;
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

}
