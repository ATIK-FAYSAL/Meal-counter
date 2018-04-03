package com.atik_faysal.mealcounter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.model.MemberModel;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.adapter.AdapterMemberList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 2/1/2018.
 */

public class AllMemberList extends AppCompatActivity
{
        private ListView listView;
        private TextView txtPerson;
        private Toolbar toolbar;
        private SwipeRefreshLayout refreshLayout;

        private List<MemberModel>memberList = new ArrayList<>();
        private JSONArray jsonArray;
        private JSONObject jsonObject;

        private DatabaseBackgroundTask backgroundTask;
        private AlertDialogClass dialogClass;
        private CheckInternetIsOn internetIsOn;
        private AdapterMemberList adapter;
        private SharedPreferenceData sharedPreferenceData;
        private NeedSomeMethod someMethod;

        //private static final String FILE_URL = "http://192.168.56.1/json_mem_info.php";
        private static String POST_DATA;
        private static final String USER_INFO = "currentInfo";

        private String currentUser;
        private String name,userName,phone,taka,type,date;
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
                refreshLayout = findViewById(R.id.refreshLayout);
                refreshLayout.setColorSchemeResources(R.color.color2,R.color.red,R.color.color6);
                setSupportActionBar(toolbar);

                internetIsOn = new CheckInternetIsOn(this);
                dialogClass = new AlertDialogClass(this);
                sharedPreferenceData = new SharedPreferenceData(this);
                someMethod = new NeedSomeMethod(this);

                //calling method
                someMethod.reloadPage(refreshLayout,AllMemberList.class);
                setToolbar();

                currentUser = sharedPreferenceData.getCurrentUserName();
                if(internetIsOn.isOnline())
                {
                        if(currentUser!=null)
                        {
                                try {
                                        POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                }
                                backgroundTask = new DatabaseBackgroundTask(this);
                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                backgroundTask.execute(getResources().getString(R.string.memberInfo),POST_DATA);
                        }else Toast.makeText(this,"under construction",Toast.LENGTH_SHORT).show();
                }
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

        //process json data,get all member from database and show in listview
        private void addMemberInListView(String jsonData)
        {
                if(jsonData!=null)
                {
                        try {
                                jsonObject = new JSONObject(jsonData);
                                jsonArray = jsonObject.optJSONArray("memInfo");

                                int count=0;

                                while (count<jsonArray.length())
                                {
                                        JSONObject jObject = jsonArray.getJSONObject(count);

                                        name = jObject.getString("name");
                                        userName = jObject.getString("userName");
                                        phone = jObject.getString("phone");
                                        type = jObject.getString("type");
                                        date = jObject.getString("date");
                                        taka = jObject.getString("taka");

                                        memberList.add(new MemberModel(name,userName,phone,"",taka,type,date));
                                        count++;
                                }

                                if(memberList.size()==1)txtPerson.setText(String.valueOf(memberList.size())+"  person");
                                else txtPerson.setText(String.valueOf(memberList.size())+"  persons");

                                adapter = new AdapterMemberList(this,memberList);
                                listView.setAdapter(adapter);
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
