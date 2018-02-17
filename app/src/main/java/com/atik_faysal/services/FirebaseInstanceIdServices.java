package com.atik_faysal.services;

import com.atik_faysal.backend.RegisterDeviceToken;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by USER on 2/14/2018.
 */

public class FirebaseInstanceIdServices extends FirebaseInstanceIdService
{
        private SharedPreferenceData sharedPreferenceData;
        private NeedSomeMethod someMethod;

        private final static String USER_INFO = "currentInfo";

        @Override
        public void onTokenRefresh() {
                String token = FirebaseInstanceId.getInstance().getToken();

                sharedPreferenceData = new SharedPreferenceData(this);
                someMethod  = new NeedSomeMethod(this);

                String userName = sharedPreferenceData.getUserName(USER_INFO);
                String date = someMethod.getDate();

                RegisterDeviceToken.registerToken(token,userName,date);
        }
}
