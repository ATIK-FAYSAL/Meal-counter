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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.model.MemberModel;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.adapter.RequestsAdapter;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 2/11/2018.
 */

public class MemberJoinRequest extends AppCompatActivity
{
        private RecyclerView recyclerView;
        private TextView textView;
        private Toolbar toolbar;
        private RelativeLayout emptyView;
        private List<MemberModel>memberModelList = new ArrayList<>();

        //private static final String FILE_URL = "http://192.168.56.1/allJoinRequests.php";
        private static String POST_DATA;
        private String currentUser;

        private LinearLayoutManager layoutManager;

        private CheckInternetIsOn internetIsOn;
        private AlertDialogClass dialogClass;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.member_list);
                initComponent();
        }

        @SuppressLint("SetTextI18n")
        private void initComponent()
        {
                recyclerView = findViewById(R.id.memberList);
                layoutManager = new LinearLayoutManager(this);
                textView = findViewById(R.id.txtPerson);
                emptyView = findViewById(R.id.empty_view);
                AdView adView = findViewById(R.id.adView);
                SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
                refreshLayout.setColorSchemeResources(R.color.color2,R.color.red,R.color.color6);
                TextView textView = findViewById(R.id.textView16);
                textView.setText("Requests");
                toolbar = findViewById(R.id.toolbar2);
                setSupportActionBar(toolbar);

                internetIsOn = new CheckInternetIsOn(this);
                dialogClass = new AlertDialogClass(this);
                SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(this);
                NeedSomeMethod someMethod = new NeedSomeMethod(this);

                currentUser = sharedPreferenceData.getCurrentUserName();
                someMethod.setAdmob(adView);

                //calling method
                setToolbar();
                initializeAllRequest();
                someMethod.reloadPage(refreshLayout,MemberJoinRequest.class);
        }

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

        private void initializeAllRequest()
        {
                if(internetIsOn.isOnline())
                {
                        try {
                                POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8");
                                DatabaseBackgroundTask backgroundTask = new DatabaseBackgroundTask(this);
                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                backgroundTask.execute(getResources().getString(R.string.allJoinRequest),POST_DATA);
                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }
                }else dialogClass.noInternetConnection();
        }

        private void processJsonData(String jsonData)
        {

                String name,userName,phone,status,date,id,group;

                try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray jsonArray = jsonObject.optJSONArray("joinRequests");

                        int count=0;

                        while (count< jsonArray.length())
                        {
                                JSONObject jObject = jsonArray.getJSONObject(count);

                                id = jObject.getString("id");
                                userName = jObject.getString("userName");
                                status = jObject.getString("status");
                                date = jObject.getString("date");
                                name = jObject.getString("name");
                                phone = jObject.getString("phone");
                                group = jObject.getString("group");

                                memberModelList.add(new MemberModel(name,userName,phone,group,status,id,date));
                                count++;
                        }

                        if(memberModelList.isEmpty())
                        {
                             recyclerView.setVisibility(View.INVISIBLE);
                             emptyView.setVisibility(View.VISIBLE);
                        }

                        RequestsAdapter adapter = new RequestsAdapter(this, memberModelList);
                        recyclerView.setAdapter(adapter);
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        textView.setText(String.valueOf(memberModelList.size()));

                } catch (JSONException e) {
                        e.printStackTrace();
                }
        }

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
