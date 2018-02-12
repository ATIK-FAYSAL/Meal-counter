package com.atik_faysal.mealcounter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.backend.InfoBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.model.AdapterMemberList;
import com.atik_faysal.model.MemberModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 2/4/2018.
 */

public class AdminPanel extends AppCompatActivity
{
        private ListView listView;
        private TextView txtPerson;
        private Toolbar toolbar;

        private List<MemberModel> memberList = new ArrayList<>();
        private JSONArray jsonArray;
        private JSONObject jsonObject;

        private InfoBackgroundTask backgroundTask;
        private AlertDialogClass dialogClass;
        private CheckInternetIsOn internetIsOn;
        private AdapterMemberList adapter;
        private SharedPreferenceData sharedPreferenceData;

        private static final String FILE_URL = "http://192.168.56.1/json_mem_info.php";
        private static String POST_DATA;
        private static final String USER_INFO = "currentInfo";

        private String currentUser;
        private String name,userName,phone,taka,type,date;
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.member_list);
                initComponent();
        }

        private void initComponent()
        {
                listView = findViewById(R.id.memberList);
                txtPerson = findViewById(R.id.txtPerson);
                toolbar = findViewById(R.id.toolbar2);
                setSupportActionBar(toolbar);
                setToolbar();

                internetIsOn = new CheckInternetIsOn(this);
                dialogClass = new AlertDialogClass(this);
                sharedPreferenceData = new SharedPreferenceData(this);

                currentUser = sharedPreferenceData.getCurrentUserName(USER_INFO);
                if(internetIsOn.isOnline())
                {
                        if(currentUser!=null)
                        {
                                try {
                                        POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                }
                                backgroundTask = new InfoBackgroundTask(this);
                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                backgroundTask.execute(FILE_URL,POST_DATA);
                        }else Toast.makeText(this,"under construction",Toast.LENGTH_SHORT).show();
                }
        }

        private void setToolbar()
        {
                toolbar.setTitleTextColor(getResources().getColor(R.color.offWhite));
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


        InfoBackgroundTask.OnAsyncTaskInterface onAsyncTaskInterface = new InfoBackgroundTask.OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String userInfo) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        switch (userInfo)
                                        {
                                                case "no result":
                                                        break;

                                                case "not member":
                                                        dialogClass.notMember();
                                                        break;

                                                default:
                                                        addMemberInListView(userInfo);
                                                        break;
                                        }
                                }
                        });
                }
        };


        private void addMemberInListView(String jsonData)
        {
                if(jsonData!=null)
                {
                        try {
                                jsonObject = new JSONObject(jsonData);
                                jsonArray = jsonObject.optJSONArray("memInfo");

                                int count=0;

                                while (count<jsonArray.length())
                                {
                                        JSONObject jObject = jsonArray.getJSONObject(count);

                                        name = jObject.getString("name");
                                        userName = jObject.getString("userName");
                                        phone = jObject.getString("phone");
                                        type = jObject.getString("type");
                                        date = jObject.getString("date");
                                        taka = jObject.getString("taka");

                                        memberList.add(new MemberModel(name,userName,phone,"",taka,type,date));
                                        count++;
                                }

                                if(memberList.size()==1)txtPerson.setText(String.valueOf(memberList.size())+"  person");
                                else txtPerson.setText(String.valueOf(memberList.size())+"  persons");

                                adapter = new AdapterMemberList(this,"adminClass",memberList);
                                listView.setAdapter(adapter);
                        } catch (JSONException e) {
                                e.printStackTrace();
                        }
                }
        }
}
