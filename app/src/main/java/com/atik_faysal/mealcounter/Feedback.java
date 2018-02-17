package com.atik_faysal.mealcounter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.backend.InfoBackgroundTask;
import com.atik_faysal.backend.InfoBackgroundTask.OnAsyncTaskInterface;
import com.atik_faysal.backend.SharedPreferenceData;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * Created by USER on 1/29/2018.
 */

public class Feedback extends AppCompatActivity
{
        private EditText eFeedback;
        private Button bFeedback;
        private TextView txtName;
        private Toolbar toolbar;

        private AlertDialogClass dialogClass;
        private NeedSomeMethod someMethod;
        private InfoBackgroundTask checkBackgroundTask;
        private SharedPreferenceData sharedPreferenceData;
        private CheckInternetIsOn internetIsOn;


        private String currentUser;
        private static final String FILE_URL = "http://192.168.56.1/feedback.php";
        private static String POST_DATA;
        private static final String PREF_NAME = "currentInfo";

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.feedback);
                initComponent();
                onButtonClick();
                setToolbar();
        }

        private void initComponent()
        {
                eFeedback = findViewById(R.id.eFeedback);
                txtName = findViewById(R.id.txtName);
                bFeedback = findViewById(R.id.bFeedback);
                toolbar = findViewById(R.id.toolbar1);
                setSupportActionBar(toolbar);

                eFeedback.setOnTouchListener(new View.OnTouchListener() {
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
                sharedPreferenceData = new SharedPreferenceData(this);
                internetIsOn = new CheckInternetIsOn(this);
                currentUser = sharedPreferenceData.getCurrentUserName(PREF_NAME);

                txtName.setText(currentUser);
                if(eFeedback.getText().toString().isEmpty())bFeedback.setEnabled(false);

                eFeedback.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                                if(eFeedback.getText().toString().length()<20)bFeedback.setEnabled(false);
                                else bFeedback.setEnabled(true);
                        }
                });
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

        private void onButtonClick()
        {
                bFeedback.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                if(internetIsOn.isOnline())
                                {
                                        try {
                                                POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8")+"&"
                                                        +URLEncoder.encode("feedback","UTF-8")+"="+URLEncoder.encode(eFeedback.getText().toString(),"UTF-8")+"&"
                                                        +URLEncoder.encode("date","UTF-8")+"="+URLEncoder.encode(someMethod.getDate(),"UTF-8");
                                        } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                        }

                                        checkBackgroundTask = new InfoBackgroundTask(Feedback.this);
                                        checkBackgroundTask.setOnResultListener(onAsyncTaskInterface);
                                        checkBackgroundTask.execute(FILE_URL,POST_DATA);
                                }else dialogClass.noInternetConnection();

                        }
                });
        }

        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        switch (message)
                                        {
                                                case "offline":
                                                        dialogClass.noInternetConnection();
                                                        break;
                                                case "failed":
                                                        Toast.makeText(Feedback.this,"Sorry ! please try again latter.",Toast.LENGTH_SHORT).show();
                                                        break;
                                                default:
                                                        Toast.makeText(Feedback.this,message,Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        break;
                                        }
                                }
                        });
                }
        };

}
