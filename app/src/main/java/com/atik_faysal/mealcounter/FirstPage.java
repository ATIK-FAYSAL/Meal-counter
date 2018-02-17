package com.atik_faysal.mealcounter;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.atik_faysal.backend.SharedPreferenceData;
import com.crashlytics.android.Crashlytics;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;

public class FirstPage extends AppCompatActivity {

        //component variable declaration
        private Button bStart;
        private RelativeLayout layout1,layout2;
        private Animation animation1,animation2;

        //object declaration
        SharedPreferenceData sharedPreferenceData;

        private ImageView circleImageView;

        private static final String INPUT_TASK_COMPLETE = "inputTask";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                Fabric.with(this, new Crashlytics());
                setContentView(R.layout.first_page);
                initComponent();
                //startActivity();
                threadStart();
        }


        @Override
        protected void onStart() {
                super.onStart();
                if(sharedPreferenceData.returnInputTaskResult(INPUT_TASK_COMPLETE))startActivity(new Intent(FirstPage.this,LogInActivity.class));
        }

        private void initComponent()
        {
                //component initialize
                layout1 = findViewById(R.id.relative1);


                //circleImageView = findViewById(R.id.imageView);
                //TransitionDrawable drawable =(TransitionDrawable) circleImageView.getDrawable();
                //drawable.startTransition(2000);
                animation2 = AnimationUtils.loadAnimation(this,R.anim.animation2);
                layout1.setAnimation(animation2);

                //object initialize
                sharedPreferenceData = new SharedPreferenceData(this);
        }

        protected void threadStart()
        {
                Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                                try
                                {
                                        Thread.sleep(2000);
                                        startActivity(new Intent(FirstPage.this,LogInActivity.class));
                                        finish();
                                }catch (InterruptedException e)
                                {
                                        e.printStackTrace();
                                }
                        }
                });

                thread.start();
        }

       /* private void startActivity()
        {
                bStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                                startActivity(new Intent(FirstPage.this,LogInActivity.class));
                        }
                });
        }*/
}
