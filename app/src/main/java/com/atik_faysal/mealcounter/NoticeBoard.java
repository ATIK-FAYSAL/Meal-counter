package com.atik_faysal.mealcounter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.model.NoticeModel;
import com.atik_faysal.adapter.NoticeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 2/8/2018.
 */

public class NoticeBoard extends AppCompatActivity
{
        private EditText txtNotice,txtTitle;
        private Button bPublish;
        private ListView listView;
        private Toolbar toolbar;
        private SwipeRefreshLayout refreshLayout;

        private final static String FILE_URL = "http://192.168.56.1/notice.php";
        private static String POST_DATA;
        private final static String FILE = "http://192.168.56.1/getAllNotice.php";
        private static String POST;
        private static String currentUser;
        private String notice,title;
        private String userName,date,id,getNotice,getTitle;

        private List<NoticeModel>noticeModels = new ArrayList<>();
        private NoticeAdapter adapter;
        private JSONArray jsonArray;
        private JSONObject jsonObject;

        private SharedPreferenceData sharedPreferenceData;
        private AlertDialogClass dialogClass;
        private NeedSomeMethod someMethod;
        private CheckInternetIsOn internetIsOn;
        private DatabaseBackgroundTask backgroundTask;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.notice);
                initComponent();
        }

        //initialize all user information related variable by getText from textView or editText
        private void initComponent()
        {
                txtTitle  = findViewById(R.id.txtTitle);
                txtNotice = findViewById(R.id.txtNotice);
                bPublish = findViewById(R.id.bPublish);
                listView = findViewById(R.id.list);
                refreshLayout = findViewById(R.id.refreshLayout);
                refreshLayout.setColorSchemeResources(R.color.color2,R.color.red,R.color.color6);
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
                sharedPreferenceData = new SharedPreferenceData(this);

                //calling method
                onButtonClick();
                setToolbar();
                someMethod.reloadPage(refreshLayout,NoticeBoard.class);

                currentUser = sharedPreferenceData.getCurrentUserName();
                readyToConnect();
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
                                                try {
                                                        POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8")+"&"
                                                                +URLEncoder.encode("title","UTF-8")+"="+URLEncoder.encode(title,"UTF-8")+"&"
                                                                +URLEncoder.encode("notice","UTF-8")+"="+URLEncoder.encode(notice,"UTF-8")+"&"
                                                                +URLEncoder.encode("date","UTF-8")+"="+URLEncoder.encode(someMethod.getDateWithTime(),"UTF-8");

                                                        backgroundTask = new DatabaseBackgroundTask(NoticeBoard.this);
                                                        backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                                        backgroundTask.execute(FILE_URL,POST_DATA);

                                                } catch (UnsupportedEncodingException e) {
                                                        e.printStackTrace();
                                                }
                                        }else dialogClass.noInternetConnection();
                                }
                        }
                });
        }

        //ready to connect online
        private void readyToConnect()
        {
                try {
                       if(internetIsOn.isOnline())
                       {
                               POST = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8");
                               backgroundTask = new DatabaseBackgroundTask(this);
                               backgroundTask.setOnResultListener(asyncTaskInterface);
                               backgroundTask.execute(FILE,POST);
                       }else dialogClass.noInternetConnection();

                } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                }
        }

        //get all notice and set into list view
        private void initializeNoticeInListView(String jsonData)
        {
                try {
                        jsonObject = new JSONObject(jsonData);
                        jsonArray = jsonObject.optJSONArray("notice");

                        int count=0;
                        while (count<jsonArray.length())
                        {
                                JSONObject jObject= jsonArray.getJSONObject(count);

                                userName = jObject.getString("userName");
                                date = jObject.getString("date");
                                id = jObject.getString("id");
                                getTitle = jObject.getString("title");
                                getNotice = jObject.getString("notice");

                                noticeModels.add(new NoticeModel(userName,id,getTitle,getNotice,date));

                                count++;
                        }

                        adapter = new NoticeAdapter(this,noticeModels);
                        listView.setAdapter(adapter);

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
                                                                readyToConnect();
                                                                Toast.makeText(NoticeBoard.this,"Notice published",Toast.LENGTH_SHORT).show();
                                                                break;

                                                        case "not member":
                                                                dialogClass.notMember();
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
