package com.atik_faysal.mealcounter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.atik_faysal.backend.InfoBackgroundTask;
import com.atik_faysal.backend.InfoBackgroundTask.OnAsyncTaskInterface;
import com.atik_faysal.backend.SharedPreferenceData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static android.content.ContentValues.TAG;

/**
 * Created by USER on 2/5/2018.
 */

public class MyGroupInfo extends AppCompatActivity
{

        private TextView groupId,gAdmin,gMember,gTime,gDate,gType;
        private EditText gName,gAddress,gDescription;
        private Button bEdit;
        private Toolbar toolbar;


        private static final String FILE_URL = "http://192.168.56.1/groupInfo.php";
        private static String POST_DATA ;
        private String currentUser;
        private final static String USER_INFO = "currentInfo";
        private String name,id,address,description,type,member,time,date,admin;

        private InfoBackgroundTask backgroundTask;
        private AlertDialogClass dialogClass;
        private CheckInternetIsOn internetIsOn;
        private NeedSomeMethod someMethod;
        private SharedPreferenceData sharedPreferenceData;

        private JSONObject jsonObject;
        private JSONArray jsonArray;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.group_info);
                initComponent();
                setToolbar();
                onButtonClickListener();
                initializeGroupInfo();
        }


        private void initComponent()
        {
                groupId = findViewById(R.id.groupId);
                gAdmin = findViewById(R.id.gAdmin);
                gAddress = findViewById(R.id.gAddress);
                gMember = findViewById(R.id.gMember);
                gTime = findViewById(R.id.gTime);
                gDate = findViewById(R.id.gDate);
                gName = findViewById(R.id.groupName);
                gType = findViewById(R.id.gType);
                gDescription = findViewById(R.id.gDescription);

                bEdit = findViewById(R.id.buEdit);

                toolbar = findViewById(R.id.toolbar1);
                setSupportActionBar(toolbar);

                bEdit.setEnabled(false);
                gName.setEnabled(false);
                gAddress.setEnabled(false);
                gDescription.setEnabled(false);

                dialogClass = new AlertDialogClass(this);
                someMethod = new NeedSomeMethod(this);
                internetIsOn = new CheckInternetIsOn(this);
                sharedPreferenceData = new SharedPreferenceData(this);


                currentUser = sharedPreferenceData.getCurrentUserName(USER_INFO);
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


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {

                MenuInflater menuInflater = getMenuInflater();
                menuInflater.inflate(R.menu.edit,menu);

                return super.onCreateOptionsMenu(menu);
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

                switch (item.getItemId())
                {
                        case R.id.edit:
                                gName.setEnabled(true);
                                gDescription.setEnabled(true);
                                gAddress.setEnabled(true);
                                gTime.setEnabled(true);
                                gType.setEnabled(true);

                                bEdit.setEnabled(true);
                                break;
                }

                return super.onOptionsItemSelected(item);
        }

        private void onButtonClickListener()
        {
                bEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                });
        }

        private void initializeGroupInfo()
        {
                try {
                        POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8");

                        backgroundTask = new InfoBackgroundTask(MyGroupInfo.this);
                        backgroundTask.setOnResultListener(onAsyncTaskInterface);
                        backgroundTask.execute(FILE_URL,POST_DATA);

                }catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                }
        }

        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String result) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                        switch (result)
                                        {
                                                case "no result":
                                                        dialogClass.error("No result found.Please retry.");
                                                        break;

                                                case "not member":
                                                        dialogClass.notMember();
                                                        break;

                                                default:
                                                        groupInformation(result);
                                                        break;
                                        }
                                }
                        });
                }
        };


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
                                gDate.setText(date);
                                gMember.setText(member);
                                gAdmin.setText(admin);
                                gAddress.setText(address);

                        } catch (JSONException e) {
                                e.printStackTrace();
                        }



                }else Log.d(TAG,"Json object error");
        }

}
