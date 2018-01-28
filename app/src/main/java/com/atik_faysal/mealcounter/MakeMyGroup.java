package com.atik_faysal.mealcounter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.atik_faysal.backend.CreateGroupBackgroundTask;
import com.atik_faysal.backend.InformationCheckBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.atik_faysal.backend.InformationCheckBackgroundTask.OnAsyncTaskInterface;

/**
 * Created by USER on 1/22/2018.
 * initComponent-->Void.    initialize all component and object,also call some method.
 * getInformation-->void ,initialize variable by value ,which getting from component,
 */

public class MakeMyGroup extends AppCompatActivity
{
        protected EditText groupName,groupId,groupAddress,groupDescription;
        protected Button bCreate;


        private String gName,gId,gAddress,gDescription;
        private String currentUserName;
        private final static String USER_INFO = "currentInfo";
        private final static String FILE_URL  = "http://192.168.56.1/alreadyMember.php";
        private String POST_DATA;

        //Class object
        private SharedPreferenceData sharedPreferenceData;
        private NeedSomeMethod someMethod;
        private CheckInternetIsOn internetIsOn;
        private AlertDialogClass dialogClass;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.make_group_layout);
                initComponent();
                onButtonClick();
        }

        protected void initComponent()
        {
                groupName = findViewById(R.id.gName);
                groupId = findViewById(R.id.gId);
                groupAddress = findViewById(R.id.gAddress);
                groupDescription = findViewById(R.id.gDescription);
                bCreate = findViewById(R.id.bCreate);

                sharedPreferenceData = new SharedPreferenceData(this);
                currentUserName = sharedPreferenceData.getCurrentUserName(USER_INFO);
                someMethod = new NeedSomeMethod(this);
                internetIsOn = new CheckInternetIsOn(this);
                dialogClass = new AlertDialogClass(this);

        }

        private void getGroupInformation()
        {
                gName = groupName.getText().toString();
                gId = groupId.getText().toString();
                gAddress = groupAddress.getText().toString();
                gDescription = groupDescription.getText().toString();
        }

        protected boolean checkInfo()
        {
                boolean flag = true;
                if(gName.isEmpty())
                {
                        groupName.setError("Invalid name");
                        flag = false;
                }
                if(gId.isEmpty())
                {
                        groupId.setError("Invalid id");
                        flag = false;
                }
                if(gAddress.isEmpty())
                {
                        groupAddress.setError("Invalid address");
                        flag = false;
                }

                return flag;
        }


        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        runOnUiThread(new Runnable() {
                                public void run() {
                                        switch (message) {
                                                case "null":
                                                        new CreateGroupBackgroundTask(MakeMyGroup.this).execute(gName,gId,gAddress,gDescription,someMethod.getDate(),currentUserName);
                                                        break;
                                                case "offline":
                                                        dialogClass.noInternetConnection();
                                                        break;
                                                default:
                                                        Toast.makeText(MakeMyGroup.this,"You are already a member of "+message+".First Leave from previous group and retry. ",Toast.LENGTH_LONG).show();
                                                        break;
                                        }
                                }
                        });
                }
        };


        protected void onButtonClick()
        {
                bCreate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                getGroupInformation();

                                InformationCheckBackgroundTask backgroundTask = new InformationCheckBackgroundTask(MakeMyGroup.this);

                                if(!currentUserName.isEmpty())
                                {
                                        try {
                                                POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUserName,"UTF-8");
                                        } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                        }
                                        if(checkInfo())
                                        {
                                                if(internetIsOn.isOnline())
                                                {
                                                        backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                                        backgroundTask.execute(FILE_URL,POST_DATA);
                                                }else dialogClass.noInternetConnection();
                                        }

                                        else Toast.makeText(MakeMyGroup.this,"Please input valid information",Toast.LENGTH_SHORT).show();
                                }else Toast.makeText(MakeMyGroup.this,"Under construction",Toast.LENGTH_SHORT).show();
                        }
                });
        }
}
