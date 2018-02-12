package com.atik_faysal.mealcounter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.backend.InfoBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.atik_faysal.backend.InfoBackgroundTask.OnAsyncTaskInterface;

import static android.content.ContentValues.TAG;

/**
 * Created by USER on 2/2/2018.
 */

public class MemberDetails extends AppCompatActivity
{
        private AlertDialogClass dialogClass;
        private CheckInternetIsOn internetIsOn;
        private InfoBackgroundTask infoBackgroundTask;
        private SharedPreferenceData sharedPreferenceData;
        private NeedSomeMethod someMethod;

        //component variable
        private Button bRemove;
        private Toolbar toolbar;
        private TextView txtTaka,txtGroup,txtUserName,txtDate;
        private EditText eName,eEmail,eAddress,eFaWord,ePhone;
        private JSONArray jsonArray;
        private JSONObject jsonObject;

        private String name,userName,phone,email,address,fWord,taka,group,date;
        private String user;
        private String currentUser;
        private final static String FILE_URL = "http://192.168.56.1/json_read_member_info.php";
        private static String POST_DATA;
        private final static String URL = "http://192.168.56.1/remove_member.php";
        private static String DATA ;
        private final static String USER_INFO = "currentInfo";


        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.profile);
                initComponent();
                setToolbar();
                onButtonClick();
        }

        @SuppressLint("SetTextI18n")
        private void initComponent()
        {
                toolbar = findViewById(R.id.toolbar1);
                setSupportActionBar(toolbar);
                txtTaka = findViewById(R.id.txtTaka);
                txtUserName = findViewById(R.id.groupId);
                txtGroup = findViewById(R.id.txtGroup);
                txtDate = findViewById(R.id.gDate);

                eName = findViewById(R.id.groupName);
                eEmail = findViewById(R.id.gAddress);
                eAddress = findViewById(R.id.gTime);
                eFaWord = findViewById(R.id.gDescription);
                ePhone = findViewById(R.id.gType);

                bRemove = findViewById(R.id.bEdit);
                bRemove.setText("Remove");

                sharedPreferenceData = new SharedPreferenceData(this);
                internetIsOn = new CheckInternetIsOn(this);
                someMethod = new NeedSomeMethod(this);
                dialogClass = new AlertDialogClass(this);

                user = getIntent().getExtras().getString("userName");
                currentUser = sharedPreferenceData.getCurrentUserName(USER_INFO);

                try {
                        if(user!=null)
                                POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(user,"UTF-8");
                        else Toast.makeText(this,"under construction",Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                }
                infoBackgroundTask = new InfoBackgroundTask(this);
                infoBackgroundTask.setOnResultListener(onAsyncTaskInterface);
                infoBackgroundTask.execute(FILE_URL,POST_DATA);

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


        private void onButtonClick()
        {
                bRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                if(internetIsOn.isOnline())
                                {
                                        if(sharedPreferenceData.getUserType().equals("admin"))
                                        {

                                                if(currentUser.equals(user))
                                                        dialogClass.error("You can not remove your own membership.");
                                                else
                                                        removeMember(user);
                                        }
                                        else dialogClass.error("Only admin can remove member.You are not an admin.");
                                }else dialogClass.noInternetConnection();
                        }
                });
        }

        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String userInfo) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        switch (userInfo)
                                        {
                                                case "failed":
                                                        Toast.makeText(MemberDetails.this,"Error occurred to information update",Toast.LENGTH_SHORT).show();
                                                        break;
                                                case "updated":
                                                        updateUserInfo();
                                                        break;
                                                default:
                                                        initializeUserInfo(userInfo);
                                                        break;
                                        }
                                }
                        });
                }
        };


        private void initializeUserInfo(String userInfo)
        {
                if(userInfo!=null)
                {
                        try {
                                jsonObject = new JSONObject(userInfo);
                                jsonArray = jsonObject.optJSONArray("information");

                                int count = 0;
                                while(count<jsonArray.length())
                                {
                                        JSONObject jObject = jsonArray.getJSONObject(count);
                                        name = jObject.getString("name");
                                        userName = jObject.getString("userName");
                                        email = jObject.getString("email");
                                        phone = jObject.getString("phone");
                                        address = jObject.getString("address");
                                        taka = jObject.getString("taka");
                                        fWord = jObject.getString("fWord");
                                        group = jObject.getString("group");
                                        date = jObject.getString("date");
                                        count++;
                                }
                        } catch (JSONException e) {
                                e.printStackTrace();
                        }


                        eName.setText("  "+name);
                        txtUserName.setText("  "+userName);
                        ePhone.setText("  "+phone);
                        eAddress.setText("  "+address);
                        eFaWord.setText("  "+fWord);
                        txtDate.setText("  "+"Join  "+date);
                        eEmail.setText("  "+email);
                        txtTaka.setText(taka);
                        txtGroup.setText(group);
                }else Log.d(TAG,"Json object error");
        }

        private void updateUserInfo()
        {
                try {
                        if(user!=null)
                                POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(user,"UTF-8");
                        else Toast.makeText(MemberDetails.this,"under construction",Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                }
                infoBackgroundTask = new InfoBackgroundTask(MemberDetails.this);
                infoBackgroundTask.setOnResultListener(onAsyncTaskInterface);
                infoBackgroundTask.execute(FILE_URL,POST_DATA);
                Toast.makeText(MemberDetails.this,"Information updated successfully.",Toast.LENGTH_SHORT).show();
                finish();
        }

        OnAsyncTaskInterface asyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String value) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        switch (value)
                                        {
                                                case "error":
                                                        dialogClass.error("Failed to execute operation.Please retry after sometimes");
                                                        break;
                                                case "successful":
                                                        someMethod.closeActivity(MemberDetails.this,HomePageActivity.class);
                                                        break;
                                        }
                                }
                        });
                }
        };

        private void removeMember(String user)
        {
                try {
                        DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(user,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                }

                InfoBackgroundTask backgroundTask = new InfoBackgroundTask(MemberDetails.this);
                backgroundTask.setOnResultListener(asyncTaskInterface);
                backgroundTask.execute(URL,DATA);
        }

}
