package com.atik_faysal.mealcounter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.atik_faysal.adapter.AdminAdapter;
import com.atik_faysal.adapter.BalanceApprovalAdapter;
import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.model.CostModel;
import com.atik_faysal.model.MemberModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ApproveBalance extends AppCompatActivity
{

        private List<CostModel> costModelList;
        private ListView listView;


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
                listView = findViewById(R.id.list);
                TextView txtDate = findViewById(R.id.txtDate);
                TextView txtSession = findViewById(R.id.txtSession);

                costModelList = new ArrayList<>();

                SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(this);
                DatabaseBackgroundTask backgroundTask = new DatabaseBackgroundTask(this);
                AlertDialogClass dialogClass = new AlertDialogClass(this);
                NeedSomeMethod someMethod = new NeedSomeMethod(this);
                CheckInternetIsOn internetIsOn = new CheckInternetIsOn(this);

                SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh1);
                refreshLayout.setColorSchemeResources(R.color.color2,R.color.red,R.color.color6);
                someMethod.reloadPage(refreshLayout,ApproveBalance.class);

                txtDate.setText(someMethod.getDate());
                txtSession.setText("#"+sharedPreferenceData.getmyCurrentSession());
                if(internetIsOn.isOnline())
                {
                        String file = getResources().getString(R.string.approveBalance);
                        try {
                                String data = URLEncoder.encode("group","UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getMyGroupName(),"UTF-8")+"&"
                                        +URLEncoder.encode("check","UTF-8")+"="+URLEncoder.encode("all","UTF-8");
                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                backgroundTask.execute(file,data);
                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }
                }else dialogClass.noInternetConnection();

        }

        private void processJsonData(String jsonData)
        {
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

                        BalanceApprovalAdapter adapter = new BalanceApprovalAdapter(this, costModelList);
                        listView.setAdapter(adapter);
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
