package com.atik_faysal.mealcounter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.atik_faysal.backend.GetDataFromServer;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.model.MemberModel;
import com.atik_faysal.adapter.AdminAdapter;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by USER on 2/4/2018.
 */

public class AdminPanel extends AppCompatActivity
{
        private RecyclerView listView;
        private RelativeLayout empty_view;
        private TextView txtPerson;
        private Toolbar toolbar;
        private AlertDialogClass dialogClass;
        private LinearLayoutManager layoutManager;
        private TextView textView;
        private ProgressBar progressBar;


        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.member_list);
                initComponent();
        }

        //initialize all user information related variable by getText from textView or editText
        private void initComponent()
        {
                listView = findViewById(R.id.memberList);
                txtPerson = findViewById(R.id.txtPerson);
                toolbar = findViewById(R.id.toolbar2);
                layoutManager = new LinearLayoutManager(this);
                empty_view = findViewById(R.id.empty_view);
                progressBar = findViewById(R.id.progressBar);
                textView = findViewById(R.id.txtNoResult);
                textView.setVisibility(View.INVISIBLE);
                AdView adView = findViewById(R.id.adView);
                SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
                refreshLayout.setColorSchemeResources(R.color.color2,R.color.red,R.color.color6);
                setSupportActionBar(toolbar);
                setToolbar();

                CheckInternetIsOn internetIsOn = new CheckInternetIsOn(this);
                dialogClass = new AlertDialogClass(this);
                SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(this);
                NeedSomeMethod someMethod = new NeedSomeMethod(this);
                someMethod.setAdmob(adView);

                //calling method
                someMethod.reloadPage(refreshLayout,AdminPanel.class);

                String currentUser = sharedPreferenceData.getCurrentUserName();
                if(internetIsOn.isOnline())
                {
                        Map<String,String> map = new HashMap<>();
                        map.put("userName",currentUser);
                        GetDataFromServer dataFromServer = new GetDataFromServer(this,onAsyncTaskInterface,getResources().getString(R.string.memberInfo),map);
                        dataFromServer.sendJsonRequest();
                        /*if(currentUser !=null)
                        {
                                try {
                                        POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8");
                                        DatabaseBackgroundTask backgroundTask = new DatabaseBackgroundTask(this);
                                        backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                        backgroundTask.execute(getResources().getString(R.string.memberInfo),POST_DATA);
                                } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                }
                        }else Toast.makeText(this,"under construction",Toast.LENGTH_SHORT).show();*/
                }else dialogClass.noInternetConnection();
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

        //get all member and set into list view
        @SuppressLint("SetTextI18n")
        private void addMemberInListView(String jsonData)
        {
                final List<MemberModel> memberList = new ArrayList<>();
                if(jsonData!=null)
                {
                        try {
                                JSONObject jsonObject = new JSONObject(jsonData);
                                JSONArray jsonArray = jsonObject.optJSONArray("memInfo");

                                int count=0;

                                while (count< jsonArray.length())
                                {
                                        JSONObject jObject = jsonArray.getJSONObject(count);

                                        String name = jObject.getString("name");
                                        String userName = jObject.getString("userName");
                                        String phone = jObject.getString("phone");
                                        String type = jObject.getString("type");
                                        String date = jObject.getString("date");
                                        String taka = jObject.getString("taka");

                                        memberList.add(new MemberModel(name, userName, phone,"", taka, type, date));
                                        count++;
                                }

                        } catch (JSONException e) {
                                e.printStackTrace();
                        }finally {
                                //add progress bar ...
                                final Timer timer = new Timer();
                                final Handler handler = new Handler();
                                final  Runnable runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                                if(memberList.isEmpty())
                                                {
                                                        empty_view.setVisibility(View.VISIBLE);
                                                        textView.setVisibility(View.VISIBLE);
                                                        listView.setVisibility(View.INVISIBLE);
                                                }
                                                else
                                                {
                                                        empty_view.setVisibility(View.INVISIBLE);
                                                        listView.setVisibility(View.VISIBLE);
                                                        AdminAdapter adapter = new AdminAdapter(AdminPanel.this, memberList);
                                                        listView.setAdapter(adapter);
                                                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                                        listView.setLayoutManager(layoutManager);
                                                        listView.setItemAnimator(new DefaultItemAnimator());
                                                        if(memberList.size()==1)txtPerson.setText(String.valueOf(memberList.size())+"  person");
                                                        else txtPerson.setText(String.valueOf(memberList.size())+"  persons");
                                                }
                                                progressBar.setVisibility(View.GONE);
                                                timer.cancel();
                                        }
                                };
                                timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                                handler.post(runnable);
                                        }
                                },2800);
                        }
                }
        }

        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
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
                                                        dialogClass.alreadyMember("Please first join a group.");
                                                        break;

                                                default:
                                                        addMemberInListView(userInfo);
                                                        break;
                                        }
                                }
                        });
                }
        };

}
