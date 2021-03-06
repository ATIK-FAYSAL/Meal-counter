package com.atik_faysal.mealcounter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.atik_faysal.backend.GetDataFromServer;
import com.atik_faysal.backend.PostData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.model.NoticeModel;
import com.atik_faysal.adapter.NoticeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by USER on 2/8/2018.
 */

public class NoticeBoard extends AppCompatActivity
{
        private EditText txtNotice,txtTitle;
        private Button bPublish;
        private Toolbar toolbar;
        private RecyclerView recyclerView;
        private LinearLayoutManager layoutManager;

        private static String currentUser;
        private String notice,title;

        private AlertDialogClass dialogClass;
        private NeedSomeMethod someMethod;
        private CheckInternetIsOn internetIsOn;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.notice);
                initComponent();
                getAllNotice();
        }

        //initialize all user information related variable by getText from textView or editText
        @SuppressLint("ClickableViewAccessibility")
        private void initComponent()
        {
                txtTitle  = findViewById(R.id.txtTitle);
                txtNotice = findViewById(R.id.txtNotice);
                bPublish = findViewById(R.id.bPublish);
                recyclerView = findViewById(R.id.list);
                layoutManager = new LinearLayoutManager(this);
                toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                //set scrollview in notice editText
                txtNotice.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                                if(v.getId() == R.id.txtNotice){
                                        v.getParent().requestDisallowInterceptTouchEvent(true);
                                        switch (event.getAction() & MotionEvent.ACTION_MASK){
                                                case MotionEvent.ACTION_UP:
                                                        v.getParent().requestDisallowInterceptTouchEvent(false);
                                                        break;
                                        }
                                }
                                return false;
                        }
                });


                dialogClass = new AlertDialogClass(this);
                someMethod = new NeedSomeMethod(this);
                internetIsOn = new CheckInternetIsOn(this);
                SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(this);

                //calling method
                onButtonClick();
                setToolbar();
                currentUser = sharedPreferenceData.getCurrentUserName();
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

        //check notice and title for that they follow the input condition
        private boolean checkNotice()
        {
                boolean flag = true;

                if(txtNotice.getText().toString().isEmpty())
                {
                        flag = false;
                        txtNotice.setError("Please write something");
                }
                if(txtTitle.getText().toString().isEmpty())
                {
                        flag = false;
                        txtTitle.setError("Please write a title");
                        txtTitle.requestFocus();
                }

                if(txtTitle.getText().toString().length()>30||txtTitle.getText().toString().length()<5)
                {
                        flag = false;
                        txtTitle.setError("Title must be in 5-30 characters");
                }

                if(txtNotice.getText().toString().length()>300||txtNotice.getText().toString().length()<20)
                {
                        flag = false;
                        txtNotice.setError("Notice must be in 20-300 characters");
                }

                return flag;
        }

        //button click
        private void onButtonClick()
        {
                bPublish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                if(checkNotice())
                                {
                                        title = txtTitle.getText().toString();
                                        notice = txtNotice.getText().toString();

                                        if(internetIsOn.isOnline())
                                        {
                                                Map<String,String> map = new HashMap<>();
                                                map.put("userName",currentUser);
                                                map.put("title",title);
                                                map.put("notice",notice);
                                                map.put("date",someMethod.getDateWithTime());
                                                PostData postData = new PostData(NoticeBoard.this,onAsyncTaskInterface);
                                                postData.InsertData(getResources().getString(R.string.notice),map);
                                        }else dialogClass.noInternetConnection();
                                }
                        }
                });
        }

        //ready to connect online
        private void getAllNotice()
        {

                if(internetIsOn.isOnline())
                {
                        Map<String,String> map = new HashMap<>();
                        map.put("userName",currentUser);
                        GetDataFromServer dataFromServer = new GetDataFromServer(this,asyncTaskInterface,getResources().getString(R.string.allNotice),map);
                        dataFromServer.sendJsonRequest();
                }else dialogClass.noInternetConnection();

        }

        //get all notice and set into list view
        private void initializeNoticeInListView(String jsonData)
        {
                List<NoticeModel> noticeModels = new ArrayList<>();
                try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray jsonArray = jsonObject.optJSONArray("notice");

                        int count=0;
                        while (count< jsonArray.length())
                        {
                                JSONObject jObject= jsonArray.getJSONObject(count);

                                String userName = jObject.getString("userName");
                                String date = jObject.getString("date");
                                String id = jObject.getString("id");
                                String getTitle = jObject.getString("title");
                                String getNotice = jObject.getString("notice");

                                noticeModels.add(new NoticeModel(userName, id, getTitle, getNotice, date));
                                count++;
                        }

                        NoticeAdapter adapter = new NoticeAdapter(this, noticeModels);
                        recyclerView.setAdapter(adapter);
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());

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
                                        {
                                                switch (result)
                                                {
                                                        case "success":
                                                                getAllNotice();
                                                                someMethod.progress("Working on it...","One notice published");
                                                                break;

                                                        case "not member":
                                                                dialogClass.alreadyMember("Please first join a group.");
                                                                break;

                                                        default:
                                                                dialogClass.error("Notice does not published.please try again.");
                                                                break;
                                                }
                                        }
                                }
                        });
                }
        };


        OnAsyncTaskInterface asyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String result) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        if(result!=null)
                                                initializeNoticeInListView(result);
                                }
                        });
                }
        };

}
