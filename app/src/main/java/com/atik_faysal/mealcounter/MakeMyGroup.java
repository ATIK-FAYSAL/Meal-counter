package com.atik_faysal.mealcounter;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.atik_faysal.backend.CreateGroupBackgroundTask;
import com.atik_faysal.backend.InfoBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import android.text.format.DateFormat;
import java.util.Calendar;

import com.atik_faysal.backend.InfoBackgroundTask.OnAsyncTaskInterface;

/**
 * Created by USER on 1/22/2018.
 * initComponent-->Void.    initialize all component and object,also call some method.
 * getInformation-->void ,initialize variable by value ,which getting from component,
 */

public class MakeMyGroup extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener
{
        protected EditText groupName,groupId,groupAddress,groupDescription;
        protected Button bCreate;
        protected RadioButton rPublic;
        protected Toolbar toolbar;
        protected TextView fTime;


        private String gName,gId,gAddress,gDescription;
        private String currentUserName;
        private final static String USER_INFO = "currentInfo";
        private final static String FILE_URL  = "http://192.168.56.1/alreadyMember.php";
        private String POST_DATA;
        private String groupType="";
        private String time;

        private int hour,minute,hourFinal,minFinal;

        //Class object
        private SharedPreferenceData sharedPreferenceData;
        private NeedSomeMethod someMethod;
        private CheckInternetIsOn internetIsOn;
        private AlertDialogClass dialogClass;
        private Calendar calendar;
        protected TimePickerDialog timePickerDialog;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.make_group_layout);
                initComponent();
                onButtonClick();
                setToolbar();
        }

        protected void initComponent()
        {
                groupName = findViewById(R.id.groupName);
                groupName.requestFocus();
                groupId = findViewById(R.id.gId);
                groupAddress = findViewById(R.id.gAddress);
                groupDescription = findViewById(R.id.gDescription);
                bCreate = findViewById(R.id.bCreate);
                rPublic = findViewById(R.id.rPublic);
                fTime = findViewById(R.id.fTime);
                toolbar = findViewById(R.id.tool);
                setSupportActionBar(toolbar);
                calendar = Calendar.getInstance();


                sharedPreferenceData = new SharedPreferenceData(this);
                currentUserName = sharedPreferenceData.getCurrentUserName(USER_INFO);
                someMethod = new NeedSomeMethod(this);
                internetIsOn = new CheckInternetIsOn(this);
                dialogClass = new AlertDialogClass(this);

        }

        private void setToolbar()
        {
                toolbar.setTitleTextColor(getResources().getColor(R.color.white));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                toolbar.setNavigationIcon(R.drawable.icon_back);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                finish();
                        }
                });
        }

        private void getGroupInformation()
        {
                gName = groupName.getText().toString();
                gId = groupId.getText().toString();
                gAddress = groupAddress.getText().toString();
                gDescription = groupDescription.getText().toString();
                time = fTime.getText().toString();
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

                for(char c : gName.toCharArray()){
                        if(Character.isDigit(c)){
                                groupName.setError("Invalid Name");
                                flag = false;
                                groupName.requestFocus();
                        }
                }

                if(groupType.isEmpty())
                {
                        flag = false;
                        rPublic.setError("Choose one option");
                }

                if(time.isEmpty())
                        time = "not set";

                return flag;
        }

        public void chooseGroupType(View view)
        {
                boolean checked = ((RadioButton)view).isChecked();

                switch (view.getId())
                {
                        case R.id.rPublic:
                                if(checked)groupType="public";
                                break;

                        case R.id.rClose:
                                if(checked)groupType="close";
                                break;

                        case R.id.rSecret:
                                if(checked)groupType="secret";
                                break;

                }
        }


        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        runOnUiThread(new Runnable() {
                                public void run() {
                                        switch (message) {
                                                case "Null":
                                                        new CreateGroupBackgroundTask(MakeMyGroup.this).execute(gName,gId,gAddress,gDescription,someMethod.getDate(),currentUserName,groupType,time);
                                                        sharedPreferenceData.userType("admin");
                                                        break;
                                                case "offline":
                                                        dialogClass.noInternetConnection();
                                                        break;
                                                default:
                                                        dialogClass.alreadyMember("You are already a member of "+message);
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

                                InfoBackgroundTask backgroundTask = new InfoBackgroundTask(MakeMyGroup.this);

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
                                }else Toast.makeText(MakeMyGroup.this,"Under construction",Toast.LENGTH_SHORT).show();
                        }
                });

                fTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                hour = calendar.get(Calendar.HOUR_OF_DAY);
                                minute = calendar.get(Calendar.MINUTE);
                                timePickerDialog = new TimePickerDialog(MakeMyGroup.this,MakeMyGroup.this,hour,minute,
                                       DateFormat.is24HourFormat(MakeMyGroup.this));
                                timePickerDialog.show();
                        }
                });
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hourFinal = hourOfDay;
                minFinal = minute;

                String format;
                String sHour;
                if(hourFinal>12)
                {
                        hourFinal-=12;
                        format = " PM";
                }else if(hourFinal==0)
                {
                        hourFinal = 12;
                        format = " AM";
                }else if(hourFinal==12)
                        format = " PM";
                else
                        format = " AM";

                sHour = String.valueOf(hourFinal);

                if(hourFinal<10)
                        sHour = "0"+String.valueOf(hourFinal);

                if(minute<10)
                        fTime.setText(sHour+" : 0"+String.valueOf(minFinal)+format);
                else
                        fTime.setText(sHour+" : "+String.valueOf(minFinal)+format);
        }
}
