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
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.model.MemberModel;
import com.atik_faysal.adapter.AdminAdapter;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 2/4/2018.
 */

public class AdminPanel extends AppCompatActivity
{
        private RecyclerView listView;
        private TextView txtPerson;
        private Toolbar toolbar;
        private AlertDialogClass dialogClass;
        private LinearLayoutManager layoutManager;


        //private static final String FILE_URL = "http://192.168.56.1/json_mem_info.php";
        private static String POST_DATA;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.member_list);
                initComponent();
        }

        //initialize all user information related variable by getText from textView or editText
        private void initComponent()
        {
                listView = findViewById(R.id.memberList);
                txtPerson = findViewById(R.id.txtPerson);
                toolbar = findViewById(R.id.toolbar2);
                layoutManager = new LinearLayoutManager(this);
                SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
                refreshLayout.setColorSchemeResources(R.color.color2,R.color.red,R.color.color6);
                setSupportActionBar(toolbar);
                setToolbar();

                CheckInternetIsOn internetIsOn = new CheckInternetIsOn(this);
                dialogClass = new AlertDialogClass(this);
                SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(this);
                NeedSomeMethod someMethod = new NeedSomeMethod(this);

                //calling method
                someMethod.reloadPage(refreshLayout,AdminPanel.class);

                String currentUser = sharedPreferenceData.getCurrentUserName();
                if(internetIsOn.isOnline())
                {
                        if(currentUser !=null)
                        {
                                try {
                                        POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8");
                                        DatabaseBackgroundTask backgroundTask = new DatabaseBackgroundTask(this);
                                        backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                        backgroundTask.execute(getResources().getString(R.string.memberInfo),POST_DATA);
                                } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                }
                        }else Toast.makeText(this,"under construction",Toast.LENGTH_SHORT).show();
                }else dialogClass.noInternetConnection();
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

        //get all member and set into list view
        @SuppressLint("SetTextI18n")
        private void addMemberInListView(String jsonData)
        {
                List<MemberModel> memberList = new ArrayList<>();
                if(jsonData!=null)
                {
                        try {
                                JSONObject jsonObject = new JSONObject(jsonData);
                                JSONArray jsonArray = jsonObject.optJSONArray("memInfo");

                                int count=0;

                                while (count< jsonArray.length())
                                {
                                        JSONObject jObject = jsonArray.getJSONObject(count);

                                        String name = jObject.getString("name");
                                        String userName = jObject.getString("userName");
                                        String phone = jObject.getString("phone");
                                        String type = jObject.getString("type");
                                        String date = jObject.getString("date");
                                        String taka = jObject.getString("taka");

                                        memberList.add(new MemberModel(name, userName, phone,"", taka, type, date));
                                        count++;
                                }

                                if(memberList.size()==1)txtPerson.setText(String.valueOf(memberList.size())+"  person");
                                else txtPerson.setText(String.valueOf(memberList.size())+"  persons");

                                AdminAdapter adapter = new AdminAdapter(this, memberList);
                                listView.setAdapter(adapter);
                                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                listView.setLayoutManager(layoutManager);
                                listView.setItemAnimator(new DefaultItemAnimator());
                        } catch (JSONException e) {
                                e.printStackTrace();
                        }
                }
        }

        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String userInfo) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        switch (userInfo)
                                        {
                                                case "no result":
                                                        break;

                                                case "not member":
                                                        dialogClass.notMember();
                                                        break;

                                                default:
                                                        addMemberInListView(userInfo);
                                                        break;
                                        }
                                }
                        });
                }
        };

}
