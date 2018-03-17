package com.atik_faysal.mealcounter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.atik_faysal.model.CostModel;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 2/28/2018.
 */

public class AddCost extends AppCompatActivity
{

        private SharedPreferenceData sharedPreferenceData;
        private AlertDialogClass dialogClass;
        private CheckInternetIsOn internetIsOn;
        private NeedSomeMethod someMethod;
        private GetImportantData importantData;
        private DatabaseBackgroundTask backgroundTask;

        private ListView listView;
        private TextView txtName,txtDate,txtTaka;
        private Button bAdd;
        private Spinner spinner;
        private Toolbar toolbar;

        private List<String>memName;

        private final static String FILE = "http://192.168.56.1/shoppingCost.php";
        private final static String COST_FILE = "http://192.168.56.1/shoppingCostNotify.php";
        private final static String MEM_NAME = "http://192.168.56.1/groupMemberName.php";
        private static String DATA;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                onActivityStart();
        }


        protected void onActivityStart()
        {

                sharedPreferenceData = new SharedPreferenceData(this);
                importantData = new GetImportantData(this);
                dialogClass = new AlertDialogClass(this);
                internetIsOn = new CheckInternetIsOn(this);
                someMethod = new NeedSomeMethod(this);
                SwipeRefreshLayout refreshLayout;



                if(!(sharedPreferenceData.getMyGroupType().equals("public"))&&(sharedPreferenceData.getUserType().equals("member")))
                {
                        setContentView(R.layout.cost);
                        toolbar = findViewById(R.id.toolbar);
                        setSupportActionBar(toolbar);
                        refreshLayout = findViewById(R.id.refresh);
                        refreshLayout.setColorSchemeResources(R.color.color2,R.color.red,R.color.color6);
                        someMethod.reloadPage(refreshLayout,AddCost.class);
                        setToolbar();
                        listView = findViewById(R.id.list);

                        if(internetIsOn.isOnline())
                        {
                                try {
                                        DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getCurrentUserName(),"UTF-8");
                                        importantData.getAllShoppingCost(FILE,DATA,infoInterfaces);
                                } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                }
                        }else dialogClass.noInternetConnection();
                        setToolbar();
                }else
                {
                        setContentView(R.layout.add_cost_public);
                        refreshLayout = findViewById(R.id.refresh1);
                        refreshLayout.setColorSchemeResources(R.color.color2,R.color.red,R.color.color6);
                        someMethod.reloadPage(refreshLayout,AddCost.class);
                        initComponent();
                        onButtonClick();
                        setToolbar();
                }
        }

        private void initComponent()
        {
                String currentDate;
                String[] value;

                if(internetIsOn.isOnline())
                {
                        try {
                                DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getCurrentUserName(),"UTF-8");
                                importantData.getAllShoppingCost(FILE,DATA,infoInterfaces);
                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }


                        try {
                                DATA =  URLEncoder.encode("group","UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getMyGroupName(),"UTF-8");
                                backgroundTask = new DatabaseBackgroundTask(this);
                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                backgroundTask.execute(MEM_NAME,DATA);
                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }
                }else dialogClass.noInternetConnection();

                txtName = findViewById(R.id.txtName);
                txtDate = findViewById(R.id.txtDate);
                txtTaka = findViewById(R.id.txtTaka);
                bAdd = findViewById(R.id.bAdd);
                listView = findViewById(R.id.costList);
                spinner = findViewById(R.id.spinner);
                toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                currentDate = someMethod.getDate();
                value = currentDate.split(" ");
                txtDate.setText(value[0]);
        }

        //set a toolbar,above the page
        private void setToolbar()
        {
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

        private void onButtonClick()
        {
                bAdd = findViewById(R.id.bAdd);
                internetIsOn = new CheckInternetIsOn(this);
                bAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                if(internetIsOn.isOnline())
                                {
                                        String taka = txtTaka.getText().toString();
                                        if(taka.isEmpty())
                                        {
                                                txtTaka.setError("Invalid amount");
                                                return;
                                        }
                                        try {
                                                String post = URLEncoder.encode("group","UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getMyGroupName(),"UTF-8")+"&"
                                                        +URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getCurrentUserName(),"UTF-8")+"&"
                                                        +URLEncoder.encode("cost","UTF-8")+"="+URLEncoder.encode(taka,"UTF-8")+"&"
                                                        +URLEncoder.encode("date","UTF-8")+"="+URLEncoder.encode(txtDate.getText().toString(),"UTF-8");
                                                backgroundTask = new DatabaseBackgroundTask(AddCost.this);
                                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                                backgroundTask.execute(COST_FILE,post);
                                        } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                        }
                                }
                        }
                });
        }

        private void addMemberNameSpinner(List<String>names)
        {
                ArrayAdapter<String>memAdapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,names);
                spinner.setAdapter(memAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String name = parent.getItemAtPosition(position).toString();
                                txtName.setText(name);
                                }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                });
        }

        //process shopping cost json data
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

                                costList.add(new CostModel(id,name,taka,date));
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

        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String result) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        switch (result)
                                        {
                                                case "success":
                                                        dialogClass.success("Today's cost add successfully.");
                                                        break;
                                                case "error":
                                                        dialogClass.error("Execution failed,please try again.");
                                                        break;
                                                default:
                                                        memName = new ArrayList<>();
                                                        int count=0;
                                                        String name;
                                                        try {
                                                                JSONObject jsonObject = new JSONObject(result);
                                                                JSONArray jsonArray = jsonObject.optJSONArray("userName");
                                                                while (count<jsonArray.length())
                                                                {
                                                                        JSONObject jObject = jsonArray.getJSONObject(count);
                                                                        name = jObject.getString("userName");
                                                                        memName.add(name);
                                                                        count++;
                                                                }
                                                                addMemberNameSpinner(memName);
                                                        } catch (JSONException e) {
                                                                e.printStackTrace();
                                                        }
                                                        break;
                                        }
                                }
                        });
                }
        };
}