package com.atik_faysal.mealcounter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.atik_faysal.backend.InfoBackgroundTask;
import com.atik_faysal.backend.InfoBackgroundTask.OnAsyncTaskInterface;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.model.NoticeAdapter;
import com.atik_faysal.model.NoticeModel;

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


        private final static String FILE_URL = "http://192.168.56.1/notice.php";
        private static String POST_DATA;
        private final static String USER_INFO = "currentInfo";
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
        private InfoBackgroundTask backgroundTask;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.notice);
                initComponent();
                onButtonClick();
                setToolbar();
        }


        private void initComponent()
        {
                txtTitle  = findViewById(R.id.txtTitle);
                txtTitle.requestFocus();
                txtNotice = findViewById(R.id.txtNotice);
                bPublish = findViewById(R.id.bPublish);
                listView = findViewById(R.id.list);
                toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

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

                currentUser = sharedPreferenceData.getCurrentUserName(USER_INFO);
                readyToConnect();
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

        private void onButtonClick()
        {
                bPublish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                              if(internetIsOn.isOnline())
                              {
                                      if(checkNotice())
                                      {
                                              title = txtTitle.getText().toString();
                                              notice = txtNotice.getText().toString();
                                              try {
                                                      POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8")+"&"
                                                                 +URLEncoder.encode("title","UTF-8")+"="+URLEncoder.encode(title,"UTF-8")+"&"
                                                                 +URLEncoder.encode("notice","UTF-8")+"="+URLEncoder.encode(notice,"UTF-8")+"&"
                                                                 +URLEncoder.encode("date","UTF-8")+"="+URLEncoder.encode(someMethod.getDate(),"UTF-8");

                                                      backgroundTask = new InfoBackgroundTask(NoticeBoard.this);
                                                      backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                                      backgroundTask.execute(FILE_URL,POST_DATA);

                                              } catch (UnsupportedEncodingException e) {
                                                      e.printStackTrace();
                                              }
                                      }
                              }else dialogClass.noInternetConnection();
                        }
                });
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
                                                                Toast.makeText(NoticeBoard.this,"Notice not published.please try again.",Toast.LENGTH_SHORT).show();
                                                                break;
                                                }
                                        }
                                }
                        });
                }
        };


        private void readyToConnect()
        {
                try {
                        POST = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8");

                        backgroundTask = new InfoBackgroundTask(this);
                        backgroundTask.setOnResultListener(asyncTaskInterface);
                        backgroundTask.execute(FILE,POST);

                } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                }
        }

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


        OnAsyncTaskInterface asyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String result) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        if(result!=null)
                                                initializeNoticeInListView(result);
                                        else Toast.makeText(NoticeBoard.this,"Error",Toast.LENGTH_SHORT).show();
                                }
                        });
                }
        };

}
