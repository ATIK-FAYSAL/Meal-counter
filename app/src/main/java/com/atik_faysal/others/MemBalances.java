package com.atik_faysal.others;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.atik_faysal.adapter.MemBalanceAdapter;
import com.atik_faysal.backend.GetDataFromServer;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.MemBalanceModel;
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

public class MemBalances extends AppCompatActivity
{
        private ListView listView;
        private RelativeLayout emptyView;
        private TextView textView;
        private ProgressBar progressBar;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.mem_balance);
                initComponent();
                setToolbar();
        }

        @SuppressLint("SetTextI18n")
        private void initComponent()
        {
                TextView txtSession = findViewById(R.id.txtSession);
                listView = findViewById(R.id.list);
                AdView adView = findViewById(R.id.adView);
                emptyView = findViewById(R.id.empty_view);
                textView = findViewById(R.id.txtNoResult);
                textView.setVisibility(View.INVISIBLE);
                progressBar = findViewById(R.id.progressBar);


                SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(this);
                CheckInternetIsOn internetIsOn = new CheckInternetIsOn(this);
                AlertDialogClass dialogClass = new AlertDialogClass(this);
                NeedSomeMethod someMethod = new NeedSomeMethod(this);
                someMethod.setAdmob(adView);

                txtSession.setText("#"+sharedPreferenceData.getmyCurrentSession());

                if(internetIsOn.isOnline())
                {
                        Map<String,String> map = new HashMap<>();
                        map.put("group",sharedPreferenceData.getMyGroupName());
                        map.put("action","all");
                        GetDataFromServer dataFromServer = new GetDataFromServer(this,onAsyncTaskInterface,getResources().getString(R.string.memBalances),map);
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

        private void processJsonData(String jsonData)
        {
                String name,taka,id;
                final List<MemBalanceModel> modelList = new ArrayList<>();
                try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray jsonArray = jsonObject.optJSONArray("balances");

                        int count = 0;
                        while (count < jsonArray.length()) {
                                JSONObject jObject = jsonArray.getJSONObject(count);
                                name = jObject.getString("name");
                                taka = jObject.getString("taka");
                                id = jObject.getString("id");
                                modelList.add(new MemBalanceModel(id,name,taka));
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
                                        if(modelList.isEmpty())
                                        {
                                                textView.setVisibility(View.VISIBLE);
                                                listView.setEmptyView(emptyView);
                                        }
                                        else
                                        {
                                                emptyView.setVisibility(View.INVISIBLE);
                                                MemBalanceAdapter adapter = new MemBalanceAdapter(MemBalances.this, modelList);
                                                listView.setAdapter(adapter);
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

        private OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        if(message!=null)
                                                processJsonData(message);

                                }
                        });
                }
        };


}
