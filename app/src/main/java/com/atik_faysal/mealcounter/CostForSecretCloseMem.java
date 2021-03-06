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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.atik_faysal.adapter.CostAdapterNew;
import com.atik_faysal.backend.GetDataFromServer;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.model.CostModel;
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

public class CostForSecretCloseMem extends AppCompatActivity
{

        private RecyclerView listView;
        private RelativeLayout emptyView;
        private TextView textView;
        private ProgressBar progressBar;
        private LinearLayoutManager layoutManager;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.costs_layout);
                initComponent();
                setToolbar();
        }

        @SuppressLint("SetTextI18n")
        private void initComponent()
        {
                SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh1);
                emptyView = findViewById(R.id.empty_view);
                AdView adView = findViewById(R.id.adView);
                refreshLayout.setColorSchemeResources(R.color.color2, R.color.red, R.color.color6);
                TextView txtSession = findViewById(R.id.txtSession);
                textView = findViewById(R.id.txtNoResult);
                textView.setVisibility(View.INVISIBLE);
                progressBar = findViewById(R.id.progressBar);
                layoutManager = new LinearLayoutManager(this);

                NeedSomeMethod someMethod = new NeedSomeMethod(this);
                SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(this);
                AlertDialogClass dialogClass = new AlertDialogClass(this);
                CheckInternetIsOn internetIsOn = new CheckInternetIsOn(this);
                someMethod.reloadPage(refreshLayout, CostOfSecretCloseGroup.class);
                someMethod.setAdmob(adView);

                listView = findViewById(R.id.list);
                txtSession.setText("#"+ sharedPreferenceData.getmyCurrentSession());
                if(internetIsOn.isOnline())
                {
                        Map<String,String> map = new HashMap<>();
                        map.put("userName",sharedPreferenceData.getCurrentUserName());
                        map.put("action","all");
                        GetDataFromServer dataFromServer = new GetDataFromServer(this,onAsyncTaskInterface,getResources().getString(R.string.shoppingCost),map);
                        dataFromServer.sendJsonRequest();

                }else dialogClass.noInternetConnection();
        }


        //set a toolbar,above the page
        protected void setToolbar()
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

        //process shopping only_show_cost json data
        private void processJsonData(String result)
        {
                final List<CostModel> costList = new ArrayList<>();
                int count=0;
                String name,taka,date,id,status;
                try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray jsonArray = jsonObject.optJSONArray("costList");

                        while (count<jsonArray.length())
                        {
                                JSONObject jObject = jsonArray.getJSONObject(count);
                                id = jObject.getString("id");
                                name = jObject.getString("name");
                                taka = jObject.getString("taka");
                                date = jObject.getString("date");
                                status = jObject.getString("status");

                                costList.add(new CostModel(id,name,taka,date,status));
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
                                        if(costList.isEmpty())
                                        {
                                                textView.setVisibility(View.VISIBLE);
                                                listView.setVisibility(View.INVISIBLE);
                                                emptyView.setVisibility(View.VISIBLE);
                                        }
                                        else
                                        {
                                                emptyView.setVisibility(View.INVISIBLE);
                                                CostAdapterNew adapter = new CostAdapterNew(CostForSecretCloseMem.this, costList);
                                                listView.setAdapter(adapter);
                                                listView.setAdapter(adapter);
                                                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                                listView.setLayoutManager(layoutManager);
                                                listView.setItemAnimator(new DefaultItemAnimator());
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

        //get all shopping list
        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String result) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        if(result!=null)
                                                processJsonData(result);
                                }
                        });
                }
        };
}
