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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.GetDataFromServer;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.model.MemberModel;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.adapter.RequestsAdapter;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by USER on 2/11/2018.
 */

public class MemberJoinRequest extends AppCompatActivity
{
        private RecyclerView recyclerView;
        private TextView textView;
        private Toolbar toolbar;
        private RelativeLayout emptyView;

        private String currentUser;

        private LinearLayoutManager layoutManager;

        private CheckInternetIsOn internetIsOn;
        private AlertDialogClass dialogClass;
        private TextView txtNoResult;
        private ProgressBar progressBar;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.member_list);
                initComponent();
        }

        @SuppressLint("SetTextI18n")
        private void initComponent()
        {
                recyclerView = findViewById(R.id.memberList);
                layoutManager = new LinearLayoutManager(this);
                textView = findViewById(R.id.txtPerson);
                emptyView = findViewById(R.id.empty_view);
                progressBar = findViewById(R.id.progressBar);
                txtNoResult = findViewById(R.id.txtNoResult);
                txtNoResult.setVisibility(View.INVISIBLE);
                AdView adView = findViewById(R.id.adView);
                SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
                refreshLayout.setColorSchemeResources(R.color.color2,R.color.red,R.color.color6);
                TextView textView = findViewById(R.id.textView16);
                textView.setText("Requests");
                toolbar = findViewById(R.id.toolbar2);
                setSupportActionBar(toolbar);

                internetIsOn = new CheckInternetIsOn(this);
                dialogClass = new AlertDialogClass(this);
                SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(this);
                NeedSomeMethod someMethod = new NeedSomeMethod(this);

                currentUser = sharedPreferenceData.getCurrentUserName();
                someMethod.setAdmob(adView);

                //calling method
                setToolbar();
                initializeAllRequest();
                someMethod.reloadPage(refreshLayout,MemberJoinRequest.class);
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

        private void initializeAllRequest()
        {
                if(internetIsOn.isOnline())
                {
                        Map<String,String>map = new HashMap<>();
                        map.put("userName",currentUser);
                        GetDataFromServer dataFromServer = new GetDataFromServer(this,onAsyncTaskInterface,getResources().getString(R.string.allJoinRequest),map);
                        dataFromServer.sendJsonRequest();
                        /*try {
                                POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8");
                                DatabaseBackgroundTask backgroundTask = new DatabaseBackgroundTask(this);
                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                backgroundTask.execute(getResources().getString(R.string.allJoinRequest),POST_DATA);
                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }*/
                }else dialogClass.noInternetConnection();
        }

        private void processJsonData(final String jsonData)
        {
                final List<MemberModel>memberModelList = new ArrayList<>();
                String name,userName,phone,status,date,id,group;

                try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray jsonArray = jsonObject.optJSONArray("joinRequests");

                        int count=0;

                        while (count< jsonArray.length())
                        {
                                JSONObject jObject = jsonArray.getJSONObject(count);

                                id = jObject.getString("id");
                                userName = jObject.getString("userName");
                                status = jObject.getString("status");
                                date = jObject.getString("date");
                                name = jObject.getString("name");
                                phone = jObject.getString("phone");
                                group = jObject.getString("group");

                                memberModelList.add(new MemberModel(name.trim(),userName.trim(),phone.trim(),group.trim(),status.trim(),id.trim(),date.trim()));
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
                                        if(memberModelList.isEmpty())
                                        {
                                                emptyView.setVisibility(View.VISIBLE);
                                                txtNoResult.setVisibility(View.VISIBLE);
                                                recyclerView.setVisibility(View.INVISIBLE);
                                        }
                                        else
                                        {
                                                emptyView.setVisibility(View.INVISIBLE);
                                                recyclerView.setVisibility(View.VISIBLE);
                                                RequestsAdapter adapter = new RequestsAdapter(MemberJoinRequest.this, memberModelList);
                                                recyclerView.setAdapter(adapter);
                                                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                                recyclerView.setLayoutManager(layoutManager);
                                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                                textView.setText(String.valueOf(memberModelList.size()));
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

        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String result) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                                processJsonData(result);
                                }
                        });
                }
        };
}
