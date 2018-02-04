package com.atik_faysal.backend;

import android.content.SharedPreferences;
import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.atik_faysal.mealcounter.R;

/**
 * Created by USER on 1/18/2018.
 */

public class SharedPreferenceData
{
        //Object creation
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;

        //static variable
        private static final String MEAL_INPUT_TYPE = "mInputType";
        private static final String COST_INPUT_TYPE = "cInputType";
        private static final String INPUT_TASK_COMPLETE = "inputTask";
        private static final String REMEMBER_ME = "rememberMe";
        private static final String USER_LOGIN = "userLogIn";
        private static final String MEMBER_TYPE = "mType";

        private Context context;

        public SharedPreferenceData (Context context)
        {
                this.context = context;
        }

        public void saveMealInputType(String prefName,String value)
        {
                sharedPreferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString(MEAL_INPUT_TYPE,value);
                editor.apply();
        }

        public String returnMealInputType(String prefName)
        {
                String getValue;

                sharedPreferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
                getValue = sharedPreferences.getString(MEAL_INPUT_TYPE,"null");
                return getValue;
        }

        public void saveCostInputType(String prefName,String value)
        {
                sharedPreferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString(COST_INPUT_TYPE,value);
                editor.apply();
        }

        public String returnCostInputType(String prefName)
        {
                String getValue;

                sharedPreferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
                getValue = sharedPreferences.getString(COST_INPUT_TYPE,"null");
                return getValue;
        }

        public void inputTaskComplete(String prefName,boolean flag)
        {
                sharedPreferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putBoolean(INPUT_TASK_COMPLETE,flag);
                editor.apply();
        }

        public boolean returnInputTaskResult(String prefName)
        {
                boolean flag;
                sharedPreferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
                flag = sharedPreferences.getBoolean(INPUT_TASK_COMPLETE,false);
                return flag;
        }

        public void saveUserNamePassword(String prefName,String userName,String password,boolean flag)
        {
                sharedPreferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("userName",userName);
                editor.putString("password",password);
                editor.putBoolean("remember",flag);
                editor.apply();
        }

        public String getUserName(String prefName)
        {
                sharedPreferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
                String value;
                value = sharedPreferences.getString("userName","");
                return value;
        }

        public String getPassword(String prefName)
        {
                sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
                String value;
                value = sharedPreferences.getString("password","");
                return value;
        }

        public void currentUserInfo(String prefName, String userName, String password)
        {
                sharedPreferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("userName",userName);
                editor.putString("password",password);
                editor.apply();
        }

        public String getCurrentUserName(String prefName)
        {
                String value;
                sharedPreferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
                value = sharedPreferences.getString("userName","null");
                return value;
        }

        public String getCurrentPassword(String prefName)
        {
                String value;
                sharedPreferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
                value = sharedPreferences.getString("password","null");
                return value;
        }

        public void ifUserLogIn(String prefName,boolean flag)
        {
                sharedPreferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putBoolean("logout",flag);
                editor.apply();
        }

        public boolean returnUserLogIn(String prefName)
        {
                sharedPreferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
                return sharedPreferences.getBoolean("logout",false);
        }

        public boolean checkRemember(String prefName)
        {
                boolean flag;
                sharedPreferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
                flag = sharedPreferences.getBoolean("remember",false);

                return flag;
        }


        public void userType(String type)
        {
                sharedPreferences = context.getSharedPreferences(MEMBER_TYPE,Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("type",type);
                editor.apply();
        }


        public String getUserType()
        {
                String value;
                sharedPreferences = context.getSharedPreferences(MEMBER_TYPE,Context.MODE_PRIVATE);
                value = sharedPreferences.getString("type","null");
                return value;
        }

}
