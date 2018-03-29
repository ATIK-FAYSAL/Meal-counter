package com.atik_faysal.services;

import com.atik_faysal.backend.RegisterDeviceToken;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by USER on 2/14/2018.
 */

public class FirebaseInstanceIdServices extends FirebaseInstanceIdService
{

        private final static String USER_INFO = "currentInfo";

        @Override
        public void onTokenRefresh() {
                String token = FirebaseInstanceId.getInstance().getToken();

                SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(this);

                String userName = sharedPreferenceData.getUserName(USER_INFO);
                String date = getDate();

                RegisterDeviceToken.registerToken(token,userName,date);
        }

        public String getDate()
        {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMMM-dd");
                String date = dateFormat.format(calendar.getTime());
                return date;
        }
}
