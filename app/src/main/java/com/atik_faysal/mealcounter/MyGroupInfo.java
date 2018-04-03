package com.atik_faysal.mealcounter;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.backend.SharedPreferenceData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

/**
 * Created by USER on 2/5/2018.
 */

public class MyGroupInfo extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener
{

        private TextView groupId,gAdmin,gMember,gTime,gDate,gType;
        private EditText gName,gAddress,gDescription;
        private Button bEdit;
        private Toolbar toolbar;
        private SwipeRefreshLayout refreshLayout;

        //private static final String FILE_URL = "http://192.168.56.1/groupInfo.php";
        //private final static String EDIT_URL = "http://192.168.56.1/editGroupInfo.php";
        private static String POST_DATA ;
        private static String DATA;
        private String currentUser,userType;
        private final static String USER_INFO = "currentInfo";
        private String name,id,address,description,type,member,time,date,admin;

        private DatabaseBackgroundTask backgroundTask;
        private AlertDialogClass dialogClass;
        private CheckInternetIsOn internetIsOn;
        private NeedSomeMethod someMethod;
        private SharedPreferenceData sharedPreferenceData;
        private Calendar calendar;
        private TimePickerDialog timePickerDialog;

        private JSONObject jsonObject;
        private JSONArray jsonArray;

        private int hour,minute,hourFinal,minuteFinal;

        AlertDialog alertDialog;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.group_info);
                initComponent();
        }

        //initialize all user information related variable by getText from textView or editText
        private void initComponent()
        {
                groupId = findViewById(R.id.txtUserName);
                gAdmin = findViewById(R.id.txtTaka);
                gAddress = findViewById(R.id.gAddress);
                gMember = findViewById(R.id.gMember);
                gTime = findViewById(R.id.gTime);
                gDate = findViewById(R.id.gDate);
                gName = findViewById(R.id.groupName);
                gType = findViewById(R.id.txtPhoneNumber);
                gDescription = findViewById(R.id.gDescription);
                refreshLayout = findViewById(R.id.layout1);
                refreshLayout.setColorSchemeResources(R.color.color2,R.color.red,R.color.color6);
                bEdit = findViewById(R.id.buEdit);
                toolbar = findViewById(R.id.toolbar1);
                setSupportActionBar(toolbar);

                //editable false
                bEdit.setEnabled(false);
                gName.setEnabled(false);
                gAddress.setEnabled(false);
                gDescription.setEnabled(false);

                //object initialize
                dialogClass = new AlertDialogClass(this);
                someMethod = new NeedSomeMethod(this);
                internetIsOn = new CheckInternetIsOn(this);
                sharedPreferenceData = new SharedPreferenceData(this);
                calendar = Calendar.getInstance();

                //get current user info
                currentUser = sharedPreferenceData.getCurrentUserName();
                userType = sharedPreferenceData.getUserType();

                //calling method
                setToolbar();
                onButtonClickListener();
                initializeGroupInfo();
                someMethod.reloadPage(refreshLayout,MyGroupInfo.class);
        }

        //set a toolbar,above the page
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

        //group info to check that they follow the input condition
        private boolean checkGroupInfo(String name,String address,String desc)
        {
                boolean flag = true;

                if(name.isEmpty())
                {
                        flag = false;
                        gName.setError("Invalid name");
                }
                if(address.isEmpty())
                {
                        flag = false;
                        gAddress.setError("Invalid address");
                }
                if (desc.isEmpty())
                        description = "null";
                else description = gDescription.getText().toString();

                return flag;
        }

        //on button click
        private void onButtonClickListener()
        {
                //connect to online and ready to show gorup info
                bEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                String name,address,time,type,desc;

                                name = gName.getText().toString();
                                address = gAddress.getText().toString();
                                time = gTime.getText().toString();
                                type = gType.getText().toString();
                                desc = gDescription.getText().toString();

                                if(checkGroupInfo(name,address,desc))
                                {
                                       if(internetIsOn.isOnline())
                                       {
                                               try {
                                                       DATA = URLEncoder.encode("groupID","UTF-8")+"="+URLEncoder.encode(groupId.getText().toString(),"UTF-8")+"&"
                                                               +URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"
                                                               +URLEncoder.encode("address","UTF-8")+"="+URLEncoder.encode(address,"UTF-8")+"&"
                                                               +URLEncoder.encode("fixedTime","UTF-8")+"="+URLEncoder.encode(time,"UTF-8")+"&"
                                                               +URLEncoder.encode("groupType","UTF-8")+"="+URLEncoder.encode(type,"UTF-8")+"&"
                                                               +URLEncoder.encode("description","UTF-8")+"="+URLEncoder.encode(description,"UTF-8");

                                                       backgroundTask = new DatabaseBackgroundTask(MyGroupInfo.this);
                                                       backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                                       backgroundTask.execute(getResources().getString(R.string.editGroupInfo),DATA);
                                               } catch (UnsupportedEncodingException e) {
                                                       e.printStackTrace();
                                               }
                                       }else dialogClass.noInternetConnection();
                                }
                        }
                });

                //change fixed time
                gTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                hour = calendar.get(Calendar.HOUR_OF_DAY);
                                minute = calendar.get(Calendar.MINUTE);

                                timePickerDialog = new TimePickerDialog(MyGroupInfo.this,MyGroupInfo.this,hour,minute,
                                        DateFormat.is24HourFormat(MyGroupInfo.this));

                                timePickerDialog.show();
                        }
                });

                //change group type
                gType.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                editGroupType();
                        }
                });
        }

        //get all information about group from online and show on this page
        private void initializeGroupInfo()
        {
               if(internetIsOn.isOnline())
               {
                       try {
                               POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8");

                               backgroundTask = new DatabaseBackgroundTask(MyGroupInfo.this);
                               backgroundTask.setOnResultListener(onAsyncTaskInterface);
                               backgroundTask.execute(getResources().getString(R.string.groupInfo),POST_DATA);

                       }catch (UnsupportedEncodingException e) {
                               e.printStackTrace();
                       }
               }else dialogClass.noInternetConnection();
        }

        //process json data to string
        private void groupInformation(String userInfo)
        {
                if(userInfo!=null)
                {
                        try {
                                jsonObject = new JSONObject(userInfo);
                                jsonArray = jsonObject.optJSONArray("groupInfo");

                                int count = 0;
                                while(count<jsonArray.length())
                                {
                                        JSONObject jObject = jsonArray.getJSONObject(count);
                                        name = jObject.getString("gName");
                                        id = jObject.getString("groupId");
                                        description = jObject.getString("gDescription");
                                        type = jObject.getString("gType");
                                        address = jObject.getString("gAddress");
                                        time = jObject.getString("gTime");
                                        date = jObject.getString("gDate");
                                        member = jObject.getString("gMem");
                                        admin = jObject.getString("gAdmin");
                                        count++;
                                }

                                gName.setText(name);
                                groupId.setText(id);
                                gDescription.setText(description);
                                gType.setText(type);
                                gTime.setText(time);
                                gDate.setText("Create at "+date);
                                gMember.setText(member);
                                gAdmin.setText(admin);
                                gAddress.setText(address);

                        } catch (JSONException e) {
                                e.printStackTrace();
                        }



                }else Log.d(TAG,"Json object error");
        }

        //edit your group type
        private void editGroupType()
        {

                CharSequence[] values = {"Public group","Close group","Secret group"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int item) {

                                switch(item)
                                {
                                        case 0:
                                                gType.setText("public");
                                                break;
                                        case 1:
                                                gType.setText("close");
                                                break;
                                        case 2:
                                                gType.setText("secret");
                                                break;
                                }
                                alertDialog.dismiss();
                        }
                });
                alertDialog = builder.create();
                alertDialog.show();

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

                switch (item.getItemId())
                {
                        case R.id.edit:
                                if(userType.equals("admin"))
                                {
                                        gName.setEnabled(true);
                                        gDescription.setEnabled(true);
                                        gAddress.setEnabled(true);
                                        gTime.setEnabled(true);
                                        gType.setEnabled(true);

                                        bEdit.setEnabled(true);
                                }else dialogClass.error("Only admin can edit group info.You are not admin.");
                                break;
                }

                return super.onOptionsItemSelected(item);
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hourFinal = hourOfDay;
                minuteFinal = minute;


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
                        gTime.setText(sHour+" : 0"+String.valueOf(minuteFinal)+format);
                else
                        gTime.setText(sHour+" : "+String.valueOf(minuteFinal)+format);
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {

                MenuInflater menuInflater = getMenuInflater();
                menuInflater.inflate(R.menu.edit,menu);

                return super.onCreateOptionsMenu(menu);
        }


        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String result) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                        switch (result)
                                        {
                                                case "no result"://get group info,if no result found
                                                        dialogClass.error("No result found.Please retry.");
                                                        break;

                                                case "not member"://get group info,if not a member
                                                        dialogClass.notMember();
                                                        break;

                                                case "failed"://update info failed
                                                        dialogClass.error("Group information update failed.Please retry after sometimes");
                                                        break;

                                                case "success"://update info success
                                                        startActivity(new Intent(MyGroupInfo.this,MyGroupInfo.class));
                                                        Toast.makeText(MyGroupInfo.this,"Update successfully",Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        break;

                                                default:
                                                        groupInformation(result);//json data group info
                                                        break;
                                        }
                                }
                        });
                }
        };
}
