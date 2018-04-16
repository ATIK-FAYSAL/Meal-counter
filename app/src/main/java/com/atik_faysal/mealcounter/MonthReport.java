package com.atik_faysal.mealcounter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.adapter.ReportAdapter;
import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.model.CostModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.List;

public class MonthReport extends AppCompatActivity
{
        private TextView txtTotalTaka,txtTotalCost,txtMonth,txtTotalMeal,txtMealRate,txtRemaining;
        private ListView listView;

        private SharedPreferenceData sharedPreferenceData;
        private CheckInternetIsOn internetIsOn;
        private AlertDialogClass dialogClass;
        private NeedSomeMethod someMethod;
        private DatabaseBackgroundTask backgroundTask;

        private List<CostModel>modelList;
        private ReportAdapter adapter;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.report_list);
                initComponent();
                setToolbar();
        }


        //initialize all component
        @SuppressLint("SetTextI18n")
        private void initComponent()
        {
                //initialize all design component
                txtTotalTaka = findViewById(R.id.totalTaka);
                txtTotalCost = findViewById(R.id.totalCost);
                txtTotalMeal = findViewById(R.id.totalMeal);
                txtMealRate = findViewById(R.id.mealRate);
                txtRemaining = findViewById(R.id.remaining);
                txtMonth = findViewById(R.id.txtMonth);
                listView = findViewById(R.id.list);


                sharedPreferenceData = new SharedPreferenceData(this);
                internetIsOn = new CheckInternetIsOn(this);
                dialogClass = new AlertDialogClass(this);
                someMethod = new NeedSomeMethod(this);
                backgroundTask = new DatabaseBackgroundTask(this);

                modelList = new ArrayList<>();
                txtMonth.setText("#"+someMethod.getMonth());

                if(internetIsOn.isOnline())
                {
                        try {
                                String data = URLEncoder.encode("group","UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getMyGroupName(),"UTF-8")+"&"
                                        +URLEncoder.encode("month","UTF-8")+"="+URLEncoder.encode(someMethod.getMonth(),"UTF-8");
                                backgroundTask.setOnResultListener(asyncTaskInterface);
                                backgroundTask.execute(getResources().getString(R.string.report),data);

                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }
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

        @SuppressLint("SetTextI18n")
        private void processJsonData(String report)
        {
                try {
                        String monthlyTaka ,monthlyCost,monthlyMeal,remain,mealRate;

                        JSONObject mainObject = new JSONObject(report);
                        JSONArray reportArray = mainObject.optJSONArray("report");

                        JSONObject jsonIndex = reportArray.getJSONObject(0);
                        JSONArray detailArray = jsonIndex.getJSONArray("detail");
                        JSONObject detailObject = detailArray.getJSONObject(0);

                        monthlyTaka = detailObject.getString("monthlyTaka");
                        monthlyCost = detailObject.getString("monthlyCost");
                        monthlyMeal = detailObject.getString("monthlyMeal");
                        remain = detailObject.getString("remaining");
                        mealRate = detailObject.getString("mealRate");

                        String name,meal,taka,cost,status;

                        for(int i=1;i<reportArray.length();i++)
                        {
                                int count=0;
                                JSONObject index = reportArray.getJSONObject(i);
                                JSONArray infoArray = index.getJSONArray("info");
                                while(count<infoArray.length())
                                {
                                        JSONObject object = infoArray.getJSONObject(count);
                                        name = object.getString("name");
                                        meal = object.getString("meal");
                                        taka = object.getString("taka");
                                        cost = object.getString("cost");
                                        status = object.getString("status");
                                        modelList.add(new CostModel(name,meal,taka,cost,status));
                                        count++;
                                }
                        }

                        adapter = new ReportAdapter(this,modelList);
                        listView.setAdapter(adapter);

                        txtTotalTaka.setText(monthlyTaka);txtTotalMeal.setText(monthlyMeal);txtTotalCost.setText(monthlyCost);
                        txtRemaining.setText(remain);txtMealRate.setText(mealRate+" per meal");

                }catch (JSONException e)
                {
                        Log.d("Exception ",e.toString());
                }
        }

        private OnAsyncTaskInterface asyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        processJsonData(message);
                                }
                        });
                }
        };
}
