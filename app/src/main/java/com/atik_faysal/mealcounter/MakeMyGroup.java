package com.atik_faysal.mealcounter;

import android.app.TimePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import android.text.format.DateFormat;
import java.util.Calendar;

import com.atik_faysal.interfaces.OnAsyncTaskInterface;

/**
 * Created by USER on 1/22/2018.
 * initComponent-->Void.    initialize all component and object,also call some method.
 * getInformation-->void ,initialize variable by value ,which getting from component,
 */

public class MakeMyGroup extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener
{
        private EditText groupName,groupId,groupAddress,groupDescription;
        private Button bCreate;
        private RadioButton rPublic;
        private Toolbar toolbar;
        private TextView fTime;
        private ProgressBar progressBar;

        private String gName,gId,gAddress,gDescription;
        private String currentUserName;
        //private final static String FILE_URL  = "http://192.168.56.1/alreadyMember.php";
        //private final static String URL = "http://192.168.56.1/createGroup.php";
        private String POST_DATA;
        private static String DATA;
        private String groupType="";
        private String time;

        private int hour;
        private int minute;

        //Class object
        private SharedPreferenceData sharedPreferenceData;
        private NeedSomeMethod someMethod;
        private CheckInternetIsOn internetIsOn;
        private AlertDialogClass dialogClass;
        private Calendar calendar;
        private TimePickerDialog timePickerDialog;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.make_group_layout);
                initComponent();
                addTextChangeListener();
                onButtonClick();
                setToolbar();
        }

        // //initialize all user information related variable by getText from textView or editText
        private void initComponent()
        {
                groupName = findViewById(R.id.groupName);
                groupId = findViewById(R.id.gId);
                groupAddress = findViewById(R.id.gAddress);
                groupDescription = findViewById(R.id.gDescription);
                bCreate = findViewById(R.id.bCreate);
                rPublic = findViewById(R.id.rSecret);
                fTime = findViewById(R.id.fTime);
                toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                calendar = Calendar.getInstance();
                progressBar = findViewById(R.id.progress);

                //set scrollview in description editText
                groupDescription.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                                if(v.getId() == R.id.txtNotice){
                                        v.getParent().requestDisallowInterceptTouchEvent(true);
                                        switch (event.getAction() & MotionEvent.ACTION_MASK){
                                                case MotionEvent.ACTION_UP:
                                                        v.getParent().requestDisallowInterceptTouchEvent(false);
                                                        break;
                                        }
                                }
                                return false;
                        }
                });


                sharedPreferenceData = new SharedPreferenceData(this);
                currentUserName = sharedPreferenceData.getCurrentUserName();
                someMethod = new NeedSomeMethod(this);
                internetIsOn = new CheckInternetIsOn(this);
                dialogClass = new AlertDialogClass(this);
        }

        //set validation for text
        private void addTextChangeListener()
        {
                final Drawable icon = getResources().getDrawable(R.drawable.icon_done);
                icon.setBounds(0,0,icon.getIntrinsicWidth(),icon.getIntrinsicHeight());

                groupName.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable s) {
                                if(groupName.getText().toString().length()<3||groupName.getText().toString().length()>20)
                                        groupName.setError("Invalid");
                                else
                                        groupName.setError("Valid",icon);

                        }
                });

                groupId.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable s) {
                                if(groupId.getText().toString().length()<5||groupId.getText().toString().length()>15)
                                        groupId.setError("Invalid");
                                else
                                        groupId.setError("Valid",icon);
                        }
                });

                groupDescription.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable s) {
                               if(groupDescription.getText().toString().length()<20||groupDescription.getText().toString().length()>200)
                                       groupDescription.setError("Invalid");
                               else groupDescription.setError("Valid",icon);
                        }
                });

                groupAddress.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable s) {
                                if(groupAddress.getText().toString().length()<10||groupAddress.getText().toString().length()>30)
                                        groupAddress.setError("Invalid");
                                else groupAddress.setError("Valid",icon);
                        }
                });
        }

        //set toolbar
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

        //get all group information
        private void getGroupInformation()
        {
                gName = groupName.getText().toString();
                gId = groupId.getText().toString();
                gAddress = groupAddress.getText().toString();
                gDescription = groupDescription.getText().toString();
                time = fTime.getText().toString();
        }

        //check group information,to follow the input policy
        private boolean checkInfo()
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

                if(groupDescription.getText().toString().length()<20||groupDescription.getText().toString().length()>200)
                        groupDescription.setError("Invalid description");

                return flag;
        }

        //select group type.public,close,secret,
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

        //button action
        private void onButtonClick()
        {
                bCreate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                getGroupInformation();

                                DatabaseBackgroundTask backgroundTask = new DatabaseBackgroundTask(MakeMyGroup.this);

                                if(!currentUserName.isEmpty())
                                {
                                        try {
                                                POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUserName,"UTF-8")+"&"
                                                +URLEncoder.encode("action","UTF-8")+"="+URLEncoder.encode("name","UTF-8");
                                                if(checkInfo())
                                                {
                                                        if(internetIsOn.isOnline())
                                                        {
                                                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                                                backgroundTask.execute(getResources().getString(R.string.alreadyMember),POST_DATA);
                                                        }else dialogClass.noInternetConnection();
                                                }
                                        } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
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

        //process group info and ready to upload
        private void createNewGroup()
        {
                try {
                       DATA =  URLEncoder.encode("groupName","UTF-8")+"="+URLEncoder.encode(gName,"UTF-8")+"&"
                                +URLEncoder.encode("groupId","UTF-8")+"="+URLEncoder.encode(gId,"UTF-8")+"&"
                                +URLEncoder.encode("groupAddress","UTF-8")+"="+URLEncoder.encode(gAddress,"UTF-8")+"&"
                                +URLEncoder.encode("groupDescription","UTF-8")+"="+URLEncoder.encode(gDescription,"UTF-8")+"&"
                                +URLEncoder.encode("groupAdmin","UTF-8")+"="+URLEncoder.encode(currentUserName,"UTF-8")+"&"
                                +URLEncoder.encode("date","UTF-8")+"="+URLEncoder.encode(someMethod.getDateWithTime(),"UTF-8")+"&"
                                +URLEncoder.encode("groupType","UTF-8")+"="+URLEncoder.encode(groupType,"UTF-8")+"&"
                                +URLEncoder.encode("time","UTF-8")+"="+URLEncoder.encode(time,"UTF-8");

                        DatabaseBackgroundTask backgroundTask = new DatabaseBackgroundTask(this);
                       backgroundTask.setOnResultListener(taskInterface);
                       backgroundTask.execute(getResources().getString(R.string.createGroup),DATA);

                } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                }
        }

        //interface,insert new group information
        OnAsyncTaskInterface taskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String result) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        switch (result)
                                        {
                                                case "success":
                                                        sharedPreferenceData.userType("admin");
                                                        Toast.makeText(MakeMyGroup.this,"New group created successfully.",Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        break;
                                                case "exist":
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        dialogClass.error("Error : This group id already exist.please try again with another group id.");
                                                        break;
                                                default:
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        dialogClass.error("Error : Execution failed.please try again.");
                                                        break;
                                        }
                                }
                        });
                }
        };

        //interface,check if user already a group member or not,php file alreadyMember.php
        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        runOnUiThread(new Runnable() {
                                public void run() {
                                        switch (message) {
                                                case "Null":
                                                        if(internetIsOn.isOnline())
                                                        {
                                                                progressBar.setVisibility(View.VISIBLE);
                                                                Thread thread = new Thread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                                try
                                                                                {
                                                                                        Thread.sleep(2500);
                                                                                        createNewGroup();
                                                                                }catch (InterruptedException e)
                                                                                {
                                                                                        e.printStackTrace();
                                                                                }
                                                                        }
                                                                });
                                                                thread.start();
                                                        }else dialogClass.noInternetConnection();
                                                        break;
                                                case "offline":
                                                        dialogClass.noInternetConnection();
                                                        break;
                                                default:
                                                        dialogClass.alreadyMember("You are already a member of "+message+".Please leave from previous group and retry.");
                                                        break;
                                        }
                                }
                        });
                }
        };


        //user can fixed time for last meal input or shopping cost input.
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                int hourFinal = hourOfDay;
                int minFinal = minute;

                String format;
                String sHour;
                if(hourFinal >12)
                {
                        hourFinal -=12;
                        format = " PM";
                }else if(hourFinal ==0)
                {
                        hourFinal = 12;
                        format = " AM";
                }else if(hourFinal ==12)
                        format = " PM";
                else
                        format = " AM";

                sHour = String.valueOf(hourFinal);

                if(hourFinal <10)
                        sHour = "0"+String.valueOf(hourFinal);

                if(minute<10)
                        fTime.setText(sHour+" : 0"+String.valueOf(minFinal)+format);
                else
                        fTime.setText(sHour+" : "+String.valueOf(minFinal)+format);
        }
}
