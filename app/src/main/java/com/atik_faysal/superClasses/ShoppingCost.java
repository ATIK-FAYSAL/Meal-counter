package com.atik_faysal.superClasses;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.adapter.CostAdapter;
import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.GetImportantData;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.InfoInterfaces;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
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
                currentDate = someMethod.getDate();
                txtDate.setText(currentDate);

                if(internetIsOn.isOnline())
                {
                        try {
                                String DATA = URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(sharedPreferenceData.getCurrentUserName(), "UTF-8") + "&"
                                        + URLEncoder.encode("month", "UTF-8") + "=" + URLEncoder.encode(someMethod.getMonth(), "UTF-8");
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
                                        try {
                                                String post = URLEncoder.encode("group","UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getMyGroupName(),"UTF-8")+"&"
                                                        +URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(txtName.getText().toString(),"UTF-8")+"&"
                                                        +URLEncoder.encode("cost","UTF-8")+"="+URLEncoder.encode(taka,"UTF-8")+"&"
                                                        +URLEncoder.encode("date","UTF-8")+"="+URLEncoder.encode(txtDate.getText().toString(),"UTF-8")+"&"
                                                        +URLEncoder.encode("month","UTF-8")+"="+URLEncoder.encode(someMethod.getMonth(),"UTF-8")+"&"
                                                        +URLEncoder.encode("action","UTF-8")+"="+URLEncoder.encode(action,"UTF-8");
                                                backgroundTask = new DatabaseBackgroundTask(ShoppingCost.this);
                                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                                backgroundTask.execute(getResources().getString(R.string.shoppingCostNotify),post);
                                        } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                        }
                                }else dialogClass.noInternetConnection();
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
