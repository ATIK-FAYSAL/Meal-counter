package com.atik_faysal.backend;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.atik_faysal.mealcounter.EditYourProfile;

/**
 * Created by USER on 1/18/2018.
 */

public class SharedPreferenceData
{
        //Object creation
        private SharedPreferences sharedPreferences;
        private SharedPreferences.Editor editor;

        //static variable
        private static final String INPUT_TASK_COMPLETE = "inputTask";
        private static final String REMEMBER_ME = "rememberMe";
        private static final String USER_LOGIN = "userLogIn";
        private static final String MEMBER_TYPE = "mType";
        private static final String GROUP_NAME = "groupName";
        private static final String GROUP_TYPE = "groupType";
        private final static String USER_INFO = "currentInfo";
        private final static String USER_IMAGE = "image";
        private final static String SESSION = "session";
        private final static String IS_IMAGE_SAVE = "isSave";
        private final static String IMAGE_PATH = "path";
        private final static String IMAGE_NAME = "imageName";
        private Context context;

        public SharedPreferenceData (Context context)
        {
                this.context = context;
        }

        //this mehtod contain username ,password,and checkbox status
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

        public void currentUserInfo(String userName, String password)
        {
                sharedPreferences = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("userName",userName);
                editor.putString("password",password);
                editor.apply();
        }

        public String getCurrentUserName()
        {
                String value;
                sharedPreferences = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE);
                value = sharedPreferences.getString("userName","null");
                return value;
        }

        public String getCurrentPassword()
        {
                String value;
                sharedPreferences = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE);
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

        public void myGroup(String name)
        {
                sharedPreferences = context.getSharedPreferences(GROUP_NAME,Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("group",name);
                editor.apply();
        }

        public String getMyGroupName()
        {
                String value;
                sharedPreferences = context.getSharedPreferences(GROUP_NAME,Context.MODE_PRIVATE);
                value = sharedPreferences.getString("group","null");
                return value;
        }

        public void myGroupType(String type)
        {
                sharedPreferences = context.getSharedPreferences(GROUP_TYPE,Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("type",type);
                editor.apply();
        }

        public String getMyGroupType()
        {
                String value;
                sharedPreferences = context.getSharedPreferences(GROUP_TYPE,Context.MODE_PRIVATE);
                value = sharedPreferences.getString("type","null");
                return value;
        }

        //save user profile image
        public void myImage(Bitmap bitmap, boolean flag)
        {
                sharedPreferences = context.getSharedPreferences(USER_IMAGE,Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();

                EditYourProfile yourProfile = new EditYourProfile();
                String imagePath = yourProfile.convertImageToString(bitmap);
                editor.putString("path",imagePath);
                editor.putBoolean("isSave",flag);
                editor.apply();
        }

        public Bitmap getMyImage()
        {
                sharedPreferences = context.getSharedPreferences(USER_IMAGE,Context.MODE_PRIVATE);
                String imagePath = sharedPreferences.getString(IMAGE_PATH,"null");
                byte[] decodedByte = Base64.decode(imagePath, 0);
                return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        }

        public boolean myImageIsSave()
        {
                sharedPreferences = context.getSharedPreferences(USER_IMAGE,Context.MODE_PRIVATE);
                return sharedPreferences.getBoolean(IS_IMAGE_SAVE,false);
        }

        public String getmyCurrentSession()
        {
                String value;
                sharedPreferences = context.getSharedPreferences(SESSION,Context.MODE_PRIVATE);
                value = sharedPreferences.getString("session","null");
                return value;
        }

        public void myCurrentSession(String session)
        {
                sharedPreferences = context.getSharedPreferences(SESSION,Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("session",session);
                editor.apply();
        }

        @SuppressLint("CommitPrefEdits")
        public void saveMyImageName(String imageName)
        {
                sharedPreferences = context.getSharedPreferences(IMAGE_NAME,Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("imgName",imageName);
                editor.apply();
        }

        public String getMyImageName()
        {
                String value;
                sharedPreferences = context.getSharedPreferences(IMAGE_NAME,Context.MODE_PRIVATE);
                value = sharedPreferences.getString("imgName","null");
                return value;
        }

        public void clearAllData()
        {
                String[] prefNames = new String[]{USER_IMAGE,USER_INFO,MEMBER_TYPE,GROUP_NAME,GROUP_TYPE,SESSION};
                for(int i=0;i<prefNames.length-1;i++)
                {
                        sharedPreferences = context.getSharedPreferences(prefNames[i],Context.MODE_PRIVATE);
                        sharedPreferences.edit().clear().apply();
                }
        }
}
