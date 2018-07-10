package com.atik_faysal.superClasses;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.adapter.CostAdapter;
import com.atik_faysal.adapter.MemBalanceAdapter;
import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.GetDataFromServer;
import com.atik_faysal.backend.GetImportantData;
import com.atik_faysal.backend.PostData;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.InfoInterfaces;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.CostModel;
import com.atik_faysal.others.MemBalances;

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

@SuppressLint("Registered")
public class ShoppingCost extends AppCompatActivity
{
        protected SharedPreferenceData sharedPreferenceData;
        protected AlertDialogClass dialogClass;
        protected CheckInternetIsOn internetIsOn;
        protected NeedSomeMethod someMethod;
        protected GetImportantData importantData;
        protected DatabaseBackgroundTask backgroundTask;

        private ListView listView;
        private TextView txtName,txtDate,txtTaka;
        private RelativeLayout emptyView;
        private TextView textView;
        private ProgressBar progressBar;


        protected void initComponent()
        {
                sharedPreferenceData = new SharedPreferenceData(this);
                dialogClass = new AlertDialogClass(this);
                internetIsOn = new CheckInternetIsOn(this);
                importantData = new GetImportantData(this);
                someMethod = new NeedSomeMethod(this);

                String currentDate;
                txtDate = findViewById(R.id.txtDate);
                txtName = findViewById(R.id.txtName);
                txtTaka = findViewById(R.id.txtTaka);
                listView = findViewById(R.id.costList);
                emptyView = findViewById(R.id.empty_view);
                textView = findViewById(R.id.txtNoResult);
                textView.setVisibility(View.INVISIBLE);
                progressBar = findViewById(R.id.progressBar);
                currentDate = someMethod.getDate();
                txtDate.setText(currentDate);

                if(internetIsOn.isOnline())
                {
                        Map<String,String> map = new HashMap<>();
                        map.put("userName",sharedPreferenceData.getCurrentUserName());
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

        //if everything is ok then today's only_show_cost will be added by clicking add button
        protected void onButtonClick(final OnAsyncTaskInterface onAsyncTaskInterface,final String action)
        {
                Button bAdd = findViewById(R.id.bAdd);
                bAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                if(internetIsOn.isOnline())
                                {
                                        String taka = txtTaka.getText().toString();
                                        String name = txtName.getText().toString();
                                        if(taka.isEmpty())
                                        {
                                                txtTaka.setError("Invalid amount");
                                                return;
                                        }
                                        if(name.equals("Member's name"))
                                        {
                                                Toast.makeText(ShoppingCost.this,"Please select a member.",Toast.LENGTH_SHORT).show();
                                                return;
                                        }
                                        Map<String,String>map = new HashMap<>();
                                        map.put("group",sharedPreferenceData.getMyGroupName());
                                        map.put("name",txtName.getText().toString());
                                        map.put("cost",taka);
                                        map.put("date",txtDate.getText().toString());
                                        map.put("action",action);
                                        PostData postData = new PostData(ShoppingCost.this,onAsyncTaskInterface);
                                        postData.InsertData(getResources().getString(R.string.shoppingCostNotify),map);

                                }else dialogClass.noInternetConnection();
                        }
                });
        }

        //process shopping only_show_cost json data
        private void processJsonData(String result)
        {
                final List<CostModel> costList = new ArrayList<>();
                int count=0;
                String name,taka,date,id;
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

                                costList.add(new CostModel(id,name,taka,date,""));
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
                                                listView.setEmptyView(emptyView);
                                        }
                                        else
                                        {
                                                emptyView.setVisibility(View.INVISIBLE);
                                                CostAdapter adapter = new CostAdapter(ShoppingCost.this, costList);
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
