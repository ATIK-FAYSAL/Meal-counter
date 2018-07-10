package com.atik_faysal.mealcounter;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.atik_faysal.backend.PostData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.backend.SharedPreferenceData;
import com.google.android.gms.ads.AdView;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by USER on 1/29/2018.
 */

public class Feedback extends AppCompatActivity
{
        private EditText eFeedback;
        private Button bFeedback;
        private Toolbar toolbar;
        private TextView txtName;

        private AlertDialogClass dialogClass;
        private NeedSomeMethod someMethod;
        private CheckInternetIsOn internetIsOn;


        private String currentUser;
        //private static final String FILE_URL = "http://192.168.56.1/feedback.php";

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.feedback);
                initComponent();
        }

        //initialize all user information related variable by getText from textView or editText
        @SuppressLint("ClickableViewAccessibility")
        private void initComponent()
        {
                eFeedback = findViewById(R.id.eFeedback);
                txtName = findViewById(R.id.txtName);
                bFeedback = findViewById(R.id.bFeedback);
                bFeedback.setBackgroundDrawable(getDrawable(R.drawable.disable_button));
                toolbar = findViewById(R.id.toolbar1);
                AdView adView = findViewById(R.id.adView);
                setSupportActionBar(toolbar);

                //add scrollview in editText
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
                SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(this);
                internetIsOn = new CheckInternetIsOn(this);
                currentUser = sharedPreferenceData.getCurrentUserName();

                txtName.setText(currentUser);
                if(eFeedback.getText().toString().isEmpty())bFeedback.setEnabled(false);

                someMethod.setAdmob(adView);

                //on text changed listener and take action
                eFeedback.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable s) {
                                if(eFeedback.getText().toString().length()<20)
                                {
                                        bFeedback.setEnabled(false);
                                        bFeedback.setBackgroundDrawable(getDrawable(R.drawable.disable_button));
                                }
                                else
                                {
                                        bFeedback.setEnabled(true);
                                        bFeedback.setBackgroundDrawable(getDrawable(R.drawable.button1));
                                }
                        }
                });

                //calling method
                onButtonClick();
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

        //button click
        private void onButtonClick()
        {
                bFeedback.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                if(internetIsOn.isOnline())
                                {

                                        Map<String,String> map = new HashMap<>();
                                        map.put("userName",currentUser);
                                        map.put("feedback",eFeedback.getText().toString());
                                        map.put("date",someMethod.getDateWithTime());
                                        PostData postData = new PostData(Feedback.this,onAsyncTaskInterface);
                                        postData.InsertData(getResources().getString(R.string.feedback),map);
                                }else dialogClass.noInternetConnection();

                        }
                });

                txtName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                Intent page = new Intent(Feedback.this,MemberDetails.class);
                                page.putExtra("userName",currentUser);
                                startActivity(page);
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
                                                case "success":
                                                        someMethod.progress("Working on it....","Thank you for your cooperation.");
                                                        break;
                                                case "offline":
                                                        dialogClass.noInternetConnection();
                                                        break;
                                                default:
                                                        dialogClass.error("Execution failed! please try again.");
                                                        break;
                                        }
                                }
                        });
                }
        };

}
