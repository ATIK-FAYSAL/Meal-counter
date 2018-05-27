package com.atik_faysal.mealcounter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atik_faysal.adapter.BalanceApprovalAdapter;
import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.GetDataFromServer;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.model.CostModel;
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

public class ApproveBalance extends AppCompatActivity
{

        private RecyclerView recyclerView;
        private LinearLayoutManager layoutManager;
        private RelativeLayout emptyView;


        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.only_show_cost);
                initComponent();
                setToolbar();
        }

        private void setToolbar()
        {
                Toolbar toolbar;
                toolbar = findViewById(R.id.toolbar);
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

        @SuppressLint("SetTextI18n")
        private void initComponent()
        {
                recyclerView = findViewById(R.id.list);
                TextView txtSession = findViewById(R.id.txtSession);
                AdView adView = findViewById(R.id.adView);
                emptyView = findViewById(R.id.empty_view);

                layoutManager = new LinearLayoutManager(this);

                SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(this);
                DatabaseBackgroundTask backgroundTask = new DatabaseBackgroundTask(this);
                AlertDialogClass dialogClass = new AlertDialogClass(this);
                NeedSomeMethod someMethod = new NeedSomeMethod(this);
                CheckInternetIsOn internetIsOn = new CheckInternetIsOn(this);

                SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh1);
                refreshLayout.setColorSchemeResources(R.color.color2,R.color.red,R.color.color6);
                someMethod.reloadPage(refreshLayout,ApproveBalance.class);
                someMethod.setAdmob(adView);

                txtSession.setText("#"+sharedPreferenceData.getmyCurrentSession());
                if(internetIsOn.isOnline())
                {
                        Map<String,String> map = new HashMap<>();
                        map.put("group",sharedPreferenceData.getMyGroupName());
                        map.put("check","all");
                        GetDataFromServer dataFromServer = new GetDataFromServer(this,onAsyncTaskInterface,getResources().getString(R.string.approveBalance),map);
                        dataFromServer.sendJsonRequest();
                        /*String file = getResources().getString(R.string.approveBalance);
                        try {
                                String data = URLEncoder.encode("group","UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getMyGroupName(),"UTF-8")+"&"
                                        +URLEncoder.encode("check","UTF-8")+"="+URLEncoder.encode("all","UTF-8");
                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                backgroundTask.execute(file,data);
                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }*/
                }else dialogClass.noInternetConnection();

        }

        private void processJsonData(String jsonData)
        {
                List<CostModel> costModelList = new ArrayList<>();
                try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray jsonArray = jsonObject.optJSONArray("balance");

                        String name,status,date,taka,id;
                        int count=0;

                        while (count<jsonArray.length())
                        {
                                JSONObject jObject = jsonArray.getJSONObject(count);

                                name = jObject.getString("name");
                                date = jObject.getString("date");
                                taka = jObject.getString("taka");
                                status = jObject.getString("status");
                                id = jObject.getString("id");

                                costModelList.add(new CostModel(id,name,taka,date,status));
                                count++;
                        }

                        if(costModelList.isEmpty())
                        {
                                emptyView.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.INVISIBLE);
                        }else
                        {
                                emptyView.setVisibility(View.INVISIBLE);
                                recyclerView.setVisibility(View.VISIBLE);
                        }

                        BalanceApprovalAdapter adapter = new BalanceApprovalAdapter(this, costModelList);
                        recyclerView.setAdapter(adapter);
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                } catch (JSONException e) {
                        e.printStackTrace();
                }
        }

        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
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
