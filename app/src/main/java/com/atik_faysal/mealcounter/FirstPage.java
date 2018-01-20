package com.atik_faysal.mealcounter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.atik_faysal.backend.SharedPreferenceData;

public class FirstPage extends AppCompatActivity {

        //component variable declaration
        private Button bStart;
        private RelativeLayout layout1,layout2;
        private Animation animation1,animation2;

        //object declaration
        SharedPreferenceData sharedPreferenceData;

        private static final String INPUT_TASK_COMPLETE = "inputTask";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.first_page);
                initComponent();
                startActivity();
        }


        @Override
        protected void onStart() {
                super.onStart();
                if(sharedPreferenceData.returnInputTaskResult(INPUT_TASK_COMPLETE))startActivity(new Intent(FirstPage.this,LogInActivity.class));
        }

        private void initComponent()
        {
                //component initialize
                bStart = findViewById(R.id.bStart);
                layout1 = findViewById(R.id.relative1);
                layout2 = findViewById(R.id.relative2);
                animation1 = AnimationUtils.loadAnimation(this,R.anim.animation1);
                animation2 = AnimationUtils.loadAnimation(this,R.anim.animation2);
                layout2.setAnimation(animation1);
                layout1.setAnimation(animation2);

                //object initialize
                sharedPreferenceData = new SharedPreferenceData(this);
        }

        private void startActivity()
        {
                bStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                startActivity(new Intent(FirstPage.this,SecondPage.class));
                        }
                });
        }
}
