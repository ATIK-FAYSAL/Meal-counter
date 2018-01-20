package com.atik_faysal.backend;

import android.content.SharedPreferences;
import android.content.Context;

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

        public void saveCostInputType(String prefName,String value)
        {
                sharedPreferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString(COST_INPUT_TYPE,value);
                editor.apply();
        }


        public void inputTaskComplete(String prefName,boolean flag)
        {
                sharedPreferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putBoolean(INPUT_TASK_COMPLETE,flag);
                editor.apply();
        }

        public String returnMealInputType(String prefName)
        {
                String getValue;

                sharedPreferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
                getValue = sharedPreferences.getString(MEAL_INPUT_TYPE,"null");
                return getValue;
        }

        public String returnCostInputType(String prefName)
        {
                String getValue;

                sharedPreferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
                getValue = sharedPreferences.getString(COST_INPUT_TYPE,"null");
                return getValue;
        }

        public boolean returnInputTaskResult(String prefName)
        {
                boolean flag;
                sharedPreferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
                flag = sharedPreferences.getBoolean(INPUT_TASK_COMPLETE,false);
                return flag;
        }

}
