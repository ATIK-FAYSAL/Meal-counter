package com.atik_faysal.others;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.adapter.EachMemReportAdapter;
import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.GetDataFromServer;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.interfaces.ProcessJsonData;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.CostModel;
import com.atik_faysal.model.MealModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EachMemReport extends AppCompatActivity
{

        private List<MealModel>mealModels;
        private ListView listView;
        private EachMemReportAdapter adapter;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.report_list);
                initComponent();
                setToolbar();
        }


        @SuppressLint("SetTextI18n")
        private void initComponent()
        {
                TextView txtTotalTaka = findViewById(R.id.totalTaka);
                TextView txtTotalCost = findViewById(R.id.totalCost);
                TextView txtTotalMeal = findViewById(R.id.totalMeal);
                TextView txtMealRate = findViewById(R.id.mealRate);
                TextView txtStatus = findViewById(R.id.remaining);
                TextView txtMonth = findViewById(R.id.txtMonth);
                listView = findViewById(R.id.list);

                CheckInternetIsOn internetIsOn = new CheckInternetIsOn(this);
                DatabaseBackgroundTask backgroundTask = new DatabaseBackgroundTask(this);
                AlertDialogClass dialogClass = new AlertDialogClass(this);

                try {
                        Map<String,String> infoMap = (Map<String, String>) getIntent().getSerializableExtra("map");
                        txtMealRate.setText(infoMap.get("mealRate")+" per meal");
                        txtMonth.setText("#"+infoMap.get("month"));
                        txtTotalTaka.setText(infoMap.get("taka"));
                        txtTotalCost.setText(infoMap.get("cost"));
                        txtTotalMeal.setText(infoMap.get("meal"));
                        txtStatus.setText(infoMap.get("status"));

                        if(internetIsOn.isOnline())
                        {
                                Map<String,String> map = new HashMap<>();
                                map.put("name",infoMap.get("name"));
                                GetDataFromServer dataFromServer = new GetDataFromServer(this,onAsyncTaskInterface,getResources().getString(R.string.eachMemReport),map);
                                dataFromServer.sendJsonRequest();
                                /*try {
                                        String data = URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(infoMap.get("name"),"UTF-8");
                                        backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                        backgroundTask.execute(getResources().getString(R.string.eachMemReport),data);
                                } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                }*/
                        }else
                                dialogClass.noInternetConnection();

                }catch (ArrayIndexOutOfBoundsException ex)
                {
                        ex.printStackTrace();
                }

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

        ProcessJsonData processJsonData = new ProcessJsonData() {
                @Override
                public void processData(final String data) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        mealModels = new ArrayList<>();
                                        try {
                                                String name,breakfast,dinner,lunch,total,date;
                                                JSONObject jsonObject = new JSONObject(data);
                                                JSONArray jsonArray = jsonObject.getJSONArray("meals");
                                                int count=0;
                                                while(count<jsonArray.length())
                                                {
                                                        JSONObject jObject = jsonArray.getJSONObject(count);
                                                        name = jObject.getString("user");
                                                        date = jObject.getString("date");
                                                        breakfast = jObject.getString("breakfast");
                                                        dinner = jObject.getString("dinner");
                                                        lunch = jObject.getString("lunch");
                                                        total = jObject.getString("total");
                                                        mealModels.add(new MealModel(date,name,breakfast,dinner,lunch,total));
                                                        count++;
                                                }
                                                adapter = new EachMemReportAdapter(EachMemReport.this,mealModels);
                                                listView.setAdapter(adapter);
                                        } catch (JSONException e) {
                                                e.printStackTrace();
                                        }
                                }
                        });
                }
        };


        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        if(message!=null)
                                                processJsonData.processData(message);
                                }
                        });
                }
        };
}

