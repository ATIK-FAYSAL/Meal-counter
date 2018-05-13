package com.atik_faysal.others;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.adapter.MealAdapter;
import com.atik_faysal.adapter.MemBalanceAdapter;
import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.MealModel;
import com.atik_faysal.model.MemBalanceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MemBalances extends AppCompatActivity
{
        private ListView listView;

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


                SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(this);
                CheckInternetIsOn internetIsOn = new CheckInternetIsOn(this);
                AlertDialogClass dialogClass = new AlertDialogClass(this);
                DatabaseBackgroundTask backgroundTask = new DatabaseBackgroundTask(this);

                txtSession.setText("#"+sharedPreferenceData.getmyCurrentSession());

                if(internetIsOn.isOnline())
                {
                        try {
                                String data = URLEncoder.encode("group","UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getMyGroupName(),"UTF-8")+"&"
                                        +URLEncoder.encode("action","UTF-8")+"="+URLEncoder.encode("all","UTF-8");
                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                backgroundTask.execute(getResources().getString(R.string.memBalances),data);
                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }
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
                List<MemBalanceModel> modelList = new ArrayList<>();
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

                        MemBalanceAdapter adapter = new MemBalanceAdapter(this, modelList);
                        listView.setAdapter(adapter);


                } catch (JSONException e) {
                        e.printStackTrace();
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
