package com.atik_faysal.mealcounter;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by USER on 1/21/2018.
 */

public class CheckInternetIsOn
{
        private Context context;
        public CheckInternetIsOn(Context context)
        {
                this.context = context;
        }
        public boolean isOnline()
        {
                ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Service.CONNECTIVITY_SERVICE);
                if(connectivityManager!=null)
                {
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                        if(networkInfo!=null)
                        {
                                if(networkInfo.getState()==NetworkInfo.State.CONNECTED)
                                {
                                        return true;
                                }
                        }
                }
                return false;
        }
}
