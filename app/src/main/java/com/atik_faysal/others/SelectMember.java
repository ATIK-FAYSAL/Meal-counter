package com.atik_faysal.others;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.atik_faysal.adapter.SelectMemberAdapter;
import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.GetDataFromServer;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.ShoppingItemModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by USER on 2/24/2018.
 */

public class SelectMember extends AppCompatActivity
{
        private Toolbar toolbar;
        private Button bNotify;
        private RecyclerView recyclerView;
        private SelectMemberAdapter adapter;

        private List<ShoppingItemModel> memberName = new ArrayList<>();
        private SharedPreferenceData sharedPreferenceData;
        private CheckInternetIsOn internetIsOn;
        private AlertDialogClass dialogClass;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.members);
                iniComponent();
        }

        //initialize all variable
        private void iniComponent()
        {
                recyclerView = findViewById(R.id.recyclerView);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                bNotify = findViewById(R.id.bNotify);


                sharedPreferenceData = new SharedPreferenceData(this);
                internetIsOn = new CheckInternetIsOn(this);

                //calling method
                if(internetIsOn.isOnline())
                        getGroupMemberName();
                else
                        dialogClass.noInternetConnection();

                buttonClickListener();
                setToolbar();
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

        //on button click listener
        private void buttonClickListener()
        {
                bNotify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                if(internetIsOn.isOnline())
                                        selectedName();
                                else dialogClass.noInternetConnection();
                        }
                });
        }

        //get all member name of your group from database
        private void getGroupMemberName()
        {
                //String url = "http://192.168.56.1/groupMemberName.php";
                //String data;
                if(internetIsOn.isOnline())
                {
                        Map<String,String> map = new HashMap<>();
                        map.put("group",sharedPreferenceData.getMyGroupName());
                        GetDataFromServer dataFromServer = new GetDataFromServer(this,onAsyncTaskInterface,getResources().getString(R.string.groupMemberName),map);
                        dataFromServer.sendJsonRequest();
                        /*try {
                                data = URLEncoder.encode("group","UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getMyGroupName(),"UTF-8");
                                backgroundTask = new DatabaseBackgroundTask(SelectMember.this);
                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                backgroundTask.execute(getResources().getString(R.string.groupMemberName),data);
                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }*/
                }else
                        dialogClass.noInternetConnection();
        }

        //process json data to string and set in recycler view
        private void processJsonData(String json)
        {
                try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray jsonArray = jsonObject.optJSONArray("userName");
                        int count=0;
                        while (count< jsonArray.length())
                        {
                                JSONObject jObject = jsonArray.getJSONObject(count);
                                memberName.add(new ShoppingItemModel(jObject.getString("userName"),"",""));
                                count++;
                        }
                        adapter = new SelectMemberAdapter(this,memberName);
                        recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                        e.printStackTrace();
                }
        }

        //get all selected name to push notification
        private void selectedName()
        {
                //String url = "http://192.168.56.1/notifyMember.php";
                String data;

                List<String> selectedMemList = new ArrayList<>();
                String name;
                if(recyclerView.getChildCount()>0)
                {
                        for (int i=0;i<recyclerView.getChildCount();i++)
                        {
                                if(recyclerView.findViewHolderForLayoutPosition(i)instanceof SelectMemberAdapter.ViewHolder)
                                {
                                        SelectMemberAdapter.ViewHolder holder = (SelectMemberAdapter.ViewHolder) recyclerView.findViewHolderForLayoutPosition(i);
                                        if(holder.checkBox.isChecked())
                                        {
                                                name = holder.txtUserName.getText().toString();
                                                selectedMemList.add(name);
                                        }
                                }
                        }
                }

                if(!selectedMemList.isEmpty())
                {
                       if(internetIsOn.isOnline())
                       {
                               for(int i = 0; i< selectedMemList.size(); i++)
                               {

                                       Toast.makeText(this,"member : "+selectedMemList.get(i),Toast.LENGTH_LONG).show();
                                       Map<String,String> map = new HashMap<>();
                                       map.put("userName",selectedMemList.get(i));
                                       GetDataFromServer dataFromServer = new GetDataFromServer(this,
                                            onAsyncTaskInterface,getResources().getString(R.string.notifyMember),map);
                                       dataFromServer.sendJsonRequest();
                                       /*try {
                                               data = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(selectedMemList.get(i),"UTF-8");
                                               DatabaseBackgroundTask backgroundTask = new DatabaseBackgroundTask(this);
                                               backgroundTask.execute(getResources().getString(R.string.notifyMember),data);
                                       } catch (UnsupportedEncodingException e) {
                                               e.printStackTrace();
                                       }*/
                               }
                       }else dialogClass.noInternetConnection();
                }
        }

        //interface get name from database as json
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
