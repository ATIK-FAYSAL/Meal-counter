package com.atik_faysal.others;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateSession extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
{
        private NeedSomeMethod someMethod;
        private DatePickerDialog datePickerDialog;
        private AlertDialogClass dialogClass;
        private CheckInternetIsOn internetIsOn;
        private DatabaseBackgroundTask backgroundTask;
        private SharedPreferenceData sharedPreferenceData;

        TextView txtStartDate,txtEndDate,txtDuration;
        EditText txtSessionName;

        private int day,month,year,currentDays;

        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.create_session);

                initComponent();
                setToolbar();
        }

        public void initComponent()
        {
                someMethod = new NeedSomeMethod(this);
                Calendar calendar = Calendar.getInstance();
                dialogClass = new AlertDialogClass(this);
                internetIsOn = new CheckInternetIsOn(this);
                sharedPreferenceData = new SharedPreferenceData(this);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                month = calendar.get(Calendar.MONTH);
                year = calendar.get(Calendar.YEAR);
                currentDays = year*365+(month+1)*30+day;

                Button bDone,bCancel;

                bCancel = findViewById(R.id.bCancel);
                bDone = findViewById(R.id.bDone);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM-dd-yyyy");
                txtSessionName = findViewById(R.id.sessionName);
                txtStartDate = findViewById(R.id.startDate);
                txtEndDate = findViewById(R.id.endDate);
                txtDuration = findViewById(R.id.duration);

                txtEndDate.setClickable(true);

                txtStartDate.setText(dateFormat.format(calendar.getTime()));
                bCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                finish();
                        }
                });

                bDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                String[] value = txtDuration.getText().toString().split(" ");//split duration text
                                String start,end,duration,name;
                                name = txtSessionName.getText().toString();
                                start = txtStartDate.getText().toString();
                                end  = txtEndDate.getText().toString();
                                duration = value[0];
                                if(start.equals("")||name.equals("")||duration.equals("")||end.equals(""))
                                {
                                        if(name.equals(""))
                                                txtSessionName.setError("Invalid session title");
                                        else if(end.equals(""))
                                                txtEndDate.setError("Invalid date");

                                        return;
                                }

                                backgroundTask = new DatabaseBackgroundTask(CreateSession.this);
                                if(internetIsOn.isOnline())
                                {
                                        try {
                                                String data = URLEncoder.encode("group","UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getMyGroupName(),"UTF-8")+"&"
                                                        +URLEncoder.encode("start","UTF-8")+"="+URLEncoder.encode(start,"UTF-8")+"&"
                                                        +URLEncoder.encode("end","UTF-8")+"="+URLEncoder.encode(end,"UTF-8")+"&"
                                                        +URLEncoder.encode("session","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"
                                                        +URLEncoder.encode("duration","UTF-8")+"="+URLEncoder.encode(duration,"UTF-8");

                                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                                backgroundTask.execute(getResources().getString(R.string.createSession),data);
                                        } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                        }
                                }else dialogClass.noInternetConnection();
                        }
                });

                txtEndDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                datePickerDialog = new DatePickerDialog(CreateSession.this,CreateSession.this,day,month,year);
                                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                                datePickerDialog.show();
                        }
                });
        }


        //set toolbar
        private void setToolbar()
        {
                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
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

        @SuppressLint("SetTextI18n")
        @Override
        public void onDateSet(DatePicker datePicker, int yy, int mm, int dd) {
                day = dd;month = mm+1;year=yy;
                int endDays = day+month*30+year*365;

                if(endDays<=currentDays)
                        dialogClass.error("Invalid date.Please pick a valid date.");
                else
                {
                        txtEndDate.setText(calculateMonth(month)+"-"+day+"-"+year);
                        txtDuration.setText(String.valueOf(endDays-currentDays)+" Days");
                }
        }

        private String calculateMonth(int monthNumber)
        {
                String month = null;
                switch (monthNumber)
                {
                        case 1:
                                month = "January";
                                break;
                        case 2:
                                month = "February";
                                break;
                        case 3:
                                month = "March";
                                break;
                        case 4:
                                month = "April";
                                break;
                        case 5:
                                month = "May";
                                break;
                        case 6:
                                month = "June";
                                break;
                        case 7:
                                month = "July";
                                break;
                        case 8:
                                month = "August";
                                break;
                        case 9:
                                month = "September";
                                break;
                        case 10:
                                month = "October";
                                break;
                        case 11:
                                month = "November";
                                break;
                        case 12:
                                month = "December";
                                break;
                }

                return month;
        }


        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        switch (message)
                                        {
                                                case "success":
                                                        someMethod.progressDialog("Creating a new session...","New session created successfully");
                                                        break;
                                                default:
                                                        dialogClass.error("Execution failed.Please try again.");
                                                        break;
                                        }
                                }
                        });
                }
        };
}
