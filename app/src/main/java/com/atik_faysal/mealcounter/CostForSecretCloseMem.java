package com.atik_faysal.mealcounter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.atik_faysal.adapter.CostAdapter;
import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.GetImportantData;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.InfoInterfaces;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.CostOfSecretCloseGroup;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.CostModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CostForSecretCloseMem extends AppCompatActivity
{
        private SharedPreferenceData sharedPreferenceData;
        private AlertDialogClass dialogClass;
        private CheckInternetIsOn internetIsOn;
        private NeedSomeMethod someMethod;
        private GetImportantData importantData;

        private ListView listView;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.only_show_cost);
                initComponent();
                setToolbar();
        }

        private void initComponent()
        {
                SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh1);
                refreshLayout.setColorSchemeResources(R.color.color2, R.color.red, R.color.color6);

                someMethod = new NeedSomeMethod(this);
                sharedPreferenceData = new SharedPreferenceData(this);
                dialogClass = new AlertDialogClass(this);
                internetIsOn = new CheckInternetIsOn(this);
                someMethod = new NeedSomeMethod(this);
                importantData = new GetImportantData(this);
                someMethod.reloadPage(refreshLayout, CostOfSecretCloseGroup.class);

                String currentDate;
                TextView txtDate = findViewById(R.id.txtDate);
                listView = findViewById(R.id.list);
                currentDate = someMethod.getDate();
                txtDate.setText(currentDate);

                if(internetIsOn.isOnline())
                {
                        try {
                                String DATA = URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(sharedPreferenceData.getCurrentUserName(), "UTF-8");
                                importantData.getAllShoppingCost(getResources().getString(R.string.shoppingCost), DATA,infoInterfaces);
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

        //process shopping only_show_cost json data
        private void processJsonData(String result)
        {
                List<CostModel> costList = new ArrayList<>();
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
                        CostAdapter adapter = new CostAdapter(this, costList);
                        listView.setAdapter(adapter);
                } catch (JSONException e) {
                        e.printStackTrace();
                }
        }

        //get all shopping list
        InfoInterfaces infoInterfaces = new InfoInterfaces() {
                @Override
                public void getInfo(final String result) {
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
