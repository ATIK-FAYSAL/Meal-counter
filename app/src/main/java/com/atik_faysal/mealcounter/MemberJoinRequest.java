package com.atik_faysal.mealcounter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.atik_faysal.backend.InfoBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.model.MemberModel;
import com.atik_faysal.backend.InfoBackgroundTask.OnAsyncTaskInterface;
import com.atik_faysal.adapter.AcceptRequestAdapter;

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
        private ListView listView;
        private TextView textView;
        private Toolbar toolbar;

        private SwipeRefreshLayout refreshLayout;
        private List<MemberModel>memberModelList = new ArrayList<>();
        private JSONObject jsonObject;
        private JSONArray jsonArray;
        private AcceptRequestAdapter adapter;

        private static final String FILE_URL = "http://192.168.56.1/allJoinRequests.php";
        private static String POST_DATA;
        private String currentUser;
        private static final String USER_INFO = "currentInfo";

        private CheckInternetIsOn internetIsOn;
        private AlertDialogClass dialogClass;
        private InfoBackgroundTask backgroundTask;
        private SharedPreferenceData sharedPreferenceData;
        private NeedSomeMethod someMethod;
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.member_list);
                initComponent();
        }

        private void initComponent()
        {
                listView = findViewById(R.id.memberList);
                textView = findViewById(R.id.txtPerson);
                refreshLayout = findViewById(R.id.refreshLayout);
                refreshLayout.setColorSchemeResources(R.color.color2,R.color.red,R.color.color6);
                toolbar = findViewById(R.id.toolbar2);
                setSupportActionBar(toolbar);

                internetIsOn = new CheckInternetIsOn(this);
                dialogClass = new AlertDialogClass(this);
                sharedPreferenceData = new SharedPreferenceData(this);
                someMethod = new NeedSomeMethod(this);

                currentUser = sharedPreferenceData.getCurrentUserName(USER_INFO);

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
                                backgroundTask = new InfoBackgroundTask(this);
                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                backgroundTask.execute(FILE_URL,POST_DATA);
                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }
                }else dialogClass.noInternetConnection();
        }

        private void processJsonData(String jsonData)
        {

                String name,userName,phone,status,date,id,group;

                try {
                        jsonObject = new JSONObject(jsonData);
                        jsonArray = jsonObject.optJSONArray("joinRequests");

                        int count=0;

                        while (count<jsonArray.length())
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

                        adapter = new AcceptRequestAdapter(this,memberModelList);
                        listView.setAdapter(adapter);
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
