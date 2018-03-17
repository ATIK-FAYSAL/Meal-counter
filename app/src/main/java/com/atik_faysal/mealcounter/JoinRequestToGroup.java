package com.atik_faysal.mealcounter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static android.content.ContentValues.TAG;

/**
 * Created by USER on 2/9/2018.
 */

public class JoinRequestToGroup extends AppCompatActivity
{
        private TextView groupId,gAdmin,gMember,gTime,gDate,gType;
        private EditText gName,gAddress,gDescription;
        private Button bEdit;
        private Toolbar toolbar;

        private String currentUser;
        private static final String USER_INFO = "currentInfo";
        private String group;
        private final static String FILE = "http://192.168.56.1/RequestGroupInfo.php";
        private final static String FILE_URL = "http://192.168.56.1/joinRequestAction.php";
        private static String DATA;
        private String name,id,address,description,type,member,time,date,admin,status;

        private JSONObject jsonObject;
        private JSONArray jsonArray;

        private DatabaseBackgroundTask backgroundTask;
        private AlertDialogClass dialogClass;
        private CheckInternetIsOn internetIsOn;
        private NeedSomeMethod someMethod;
        private SharedPreferenceData sharedPreferenceData;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.group_info);
                initComponent();
                setToolbar();
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
                bEdit = findViewById(R.id.buEdit);
                toolbar = findViewById(R.id.toolbar1);
                setSupportActionBar(toolbar);


                //disable
                gName.setEnabled(false);
                gAddress.setEnabled(false);
                gDescription.setEnabled(false);

                dialogClass = new AlertDialogClass(this);
                someMethod = new NeedSomeMethod(this);
                internetIsOn = new CheckInternetIsOn(this);
                sharedPreferenceData = new SharedPreferenceData(this);

                currentUser = sharedPreferenceData.getCurrentUserName();
                if(getIntent().hasExtra("group"))
                        group = getIntent().getExtras().getString("group");
                else group = "null";

                if(internetIsOn.isOnline())
                        initializeGroupInfo();
                else dialogClass.noInternetConnection();

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

        //button click
        private void onButtonClick(final String value)
        {
                bEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                               if(internetIsOn.isOnline())
                               {
                                       backgroundTask = new DatabaseBackgroundTask(JoinRequestToGroup.this);
                                       backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                       backgroundTask.execute(FILE_URL,value);
                               }else dialogClass.noInternetConnection();
                        }
                });
        }

        //get all group information from database
        private void initializeGroupInfo()
        {
                try {
                        DATA = URLEncoder.encode("group","UTF-8")+"="+URLEncoder.encode(group,"UTF-8")+"&"
                                +URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8");

                        backgroundTask = new DatabaseBackgroundTask(JoinRequestToGroup.this);
                        backgroundTask.setOnResultListener(taskInterface);
                        backgroundTask.execute(FILE,DATA);

                }catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                }
        }

        //process json data and show in page
        private void groupInformation(String groupInfo)
        {
                if(groupInfo!=null)
                {
                        try {
                                jsonObject = new JSONObject(groupInfo);
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
                                        status = jObject.getString("status");
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

                                switch (status)
                                {
                                        case "pending":
                                                bEdit.setText("Cancel Request");
                                                try {
                                                        String postData = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8")+"&"
                                                                +URLEncoder.encode("action","UTF-8")+"="+URLEncoder.encode("cancel","UTF-8");
                                                       onButtonClick(postData);
                                                } catch (UnsupportedEncodingException e) {
                                                        e.printStackTrace();
                                                }
                                                break;
                                        case "complete":
                                                bEdit.setText("Already member");
                                                bEdit.setEnabled(false);
                                                break;
                                        case "send":
                                                bEdit.setText("Join now");
                                                try {
                                                        String postData = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8")+"&"
                                                                +URLEncoder.encode("action","UTF-8")+"="+URLEncoder.encode("send","UTF-8")+"&"
                                                                +URLEncoder.encode("group","UTF-8")+"="+URLEncoder.encode(group,"UTF-8")+"&"
                                                                +URLEncoder.encode("date","UTF-8")+"="+URLEncoder.encode(someMethod.getDate(),"UTF-8");
                                                        onButtonClick(postData);
                                                } catch (UnsupportedEncodingException e) {
                                                        e.printStackTrace();
                                                }
                                                break;
                                }

                        } catch (JSONException e) {
                                e.printStackTrace();
                        }



                }else Log.d(TAG,"Json object error");
        }

        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String result) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        switch (result)
                                        {
                                                case "success":
                                                        initializeGroupInfo();
                                                        dialogClass.success("Request send success.Please wait for your admin response");
                                                        break;
                                                case "success1":
                                                        initializeGroupInfo();
                                                        break;
                                                case "failed":
                                                        initializeGroupInfo();
                                                        dialogClass.error("Execution failed.Please retry after sometimes");
                                                        break;
                                        }
                                }
                        });
                }
        };

        OnAsyncTaskInterface taskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String result) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        switch (result)
                                        {
                                                case "member":
                                                        dialogClass.alreadyMember("You are already member.So you can not see another group information.If you want,");
                                                        break;
                                                default:
                                                        groupInformation(result);
                                                        break;
                                        }
                                }
                        });
                }
        };
}
