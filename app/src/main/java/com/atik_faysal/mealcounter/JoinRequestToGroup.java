package com.atik_faysal.mealcounter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.GetDataFromServer;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by USER on 2/9/2018.
 */

public class JoinRequestToGroup extends AppCompatActivity
{
        private TextView groupId,gAdmin,gMember,gTime,gDate,gType;
        private EditText gName,gAddress,gDescription;
        private Button bEdit;
        private String currentUser;
        private String group;
        private String name,id,address,description,type,member,time,date,admin,status;

        private DatabaseBackgroundTask backgroundTask;
        private AlertDialogClass dialogClass;
        private CheckInternetIsOn internetIsOn;
        private NeedSomeMethod someMethod;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.group_info);
                initComponent();
                //setToolbar();
        }

        //initialize all user information related variable by getText from textView or editText
        private void initComponent()
        {
                groupId = findViewById(R.id.txtName);
                gAdmin = findViewById(R.id.txtTaka);
                gAddress = findViewById(R.id.gAddress);
                gMember = findViewById(R.id.gMember);
                gTime = findViewById(R.id.gTime);
                gDate = findViewById(R.id.gDate);
                gName = findViewById(R.id.groupName);
                gType = findViewById(R.id.txtPhoneNumber);
                gDescription = findViewById(R.id.gDescription);
                bEdit = findViewById(R.id.buEdit);

                SwipeRefreshLayout refreshLayout = findViewById(R.id.layout1);
                refreshLayout.setColorSchemeResources(R.color.color2,R.color.red,R.color.color6);

                //disable edit button
                ImageView imageEdit = findViewById(R.id.imgEdit);
                imageEdit.setImageBitmap(null);
                imageEdit.setEnabled(false);

                //back button
                ImageView imgBack = findViewById(R.id.imgBack);
                imgBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                finish();
                        }
                });

                //disable
                gName.setEnabled(false);
                gAddress.setEnabled(false);
                gDescription.setEnabled(false);

                dialogClass = new AlertDialogClass(this);
                someMethod = new NeedSomeMethod(this);
                internetIsOn = new CheckInternetIsOn(this);
                SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(this);

                currentUser = sharedPreferenceData.getCurrentUserName();
                someMethod.reloadPage(refreshLayout,JoinRequestToGroup.class);
                if(getIntent().hasExtra("group"))
                        group = getIntent().getExtras().getString("group");
                else group = "null";

                if(internetIsOn.isOnline())
                        initializeGroupInfo();
                else dialogClass.noInternetConnection();

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
                            backgroundTask.execute(getResources().getString(R.string.requestAction),value);
                       }else dialogClass.noInternetConnection();
                  }
             });
        }

        //get all group information from database
        private void initializeGroupInfo()
        {
                if(internetIsOn.isOnline())
                {
                        Map<String,String> map = new HashMap<>();
                        map.put("group",group);
                        map.put("userName",currentUser);
                        GetDataFromServer fromServer = new GetDataFromServer(this,taskInterface,getResources().getString(R.string.requestGroupInfo),map);
                        fromServer.sendJsonRequest();
                }else dialogClass.noInternetConnection();

                /*try {
                        String DATA = URLEncoder.encode("group", "UTF-8") + "=" + URLEncoder.encode(group, "UTF-8") + "&"
                             + URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(currentUser, "UTF-8");

                        backgroundTask = new DatabaseBackgroundTask(JoinRequestToGroup.this);
                        backgroundTask.setOnResultListener(taskInterface);
                        backgroundTask.execute(getResources().getString(R.string.requestGroupInfo), DATA);

                }catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                }*/
        }

        //process json data and show in page
        @SuppressLint("SetTextI18n")
        private void groupInformation(String groupInfo)
        {
                if(groupInfo!=null)
                {
                        try {
                                String reqGroup=null;
                                JSONObject jsonObject = new JSONObject(groupInfo);
                                JSONArray jsonArray = jsonObject.optJSONArray("groupInfo");

                                int count = 0;
                                while(count< jsonArray.length())
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
                                        reqGroup = jObject.getString("newGroup");
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

                                assert reqGroup != null;
                                if(!reqGroup.equals(id)&&!reqGroup.equals("Null"))
                                {
                                        dialogClass.alreadyMember("You already send a request,please cancel previous request and retry.");
                                        return;
                                }

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
                                                onButtonClick("");
                                                bEdit.setText("Already member");
                                                bEdit.setEnabled(false);
                                                break;
                                        case "send":
                                                bEdit.setText("Join now");
                                                try {
                                                        String postData = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8")+"&"
                                                                +URLEncoder.encode("action","UTF-8")+"="+URLEncoder.encode("send","UTF-8")+"&"
                                                                +URLEncoder.encode("group","UTF-8")+"="+URLEncoder.encode(group,"UTF-8")+"&"
                                                                +URLEncoder.encode("date","UTF-8")+"="+URLEncoder.encode(someMethod.getDateWithTime(),"UTF-8");
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
                                                        dialogClass.alreadyMember("You are already member.So you can not see another group information.If you want,Please leave from previous group and retry.");
                                                        break;
                                                default:
                                                        Toast.makeText(JoinRequestToGroup.this,"result :"+result,Toast.LENGTH_LONG).show();
                                                        groupInformation(result);
                                                        break;
                                        }
                                }
                        });
                }
        };
}
