package com.atik_faysal.mealcounter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.atik_faysal.backend.SharedPreferenceData;


public class ThirdPage extends AppCompatActivity
{
        //component declaration
        private Button bFinish;
        private Intent intent;

        //String variable
        private String cInputType = "null";
        private String mInputType = "null";

        //object declaration
        SharedPreferenceData sharedPreferenceData;

        //static variable
        private static final String MEAL_INPUT_TYPE  = "mInputType";
        private static final String COST_INPUT_TYPE  = "cInputType";
        private static final String INPUT_TASK_COMPLETE = "inputTask";

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.third_page);
                initComponent();
                startActivity();
        }

        private void initComponent()
        {
                //component initialize
                bFinish = findViewById(R.id.bFinish);

                //object initialize
                sharedPreferenceData = new SharedPreferenceData(this);

                //get activity's extraValue
                try {
                        mInputType = getIntent().getExtras().getString("mInputType");
                }catch (NullPointerException e)
                {
                        e.printStackTrace();
                }
        }




        private void startActivity()
        {

                bFinish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                if(cInputType.equals("null")||mInputType.equals("null")) Toast.makeText(ThirdPage.this,"Please choose an option",Toast.LENGTH_SHORT).show();
                                else
                                {
                                        sharedPreferenceData.saveMealInputType(MEAL_INPUT_TYPE,mInputType);
                                        sharedPreferenceData.saveCostInputType(COST_INPUT_TYPE,cInputType);
                                        sharedPreferenceData.inputTaskComplete(INPUT_TASK_COMPLETE,true);
                                        intent = new Intent(ThirdPage.this,LogInActivity.class);
                                        startActivity(intent);
                                }
                        }
                });
        }

        public void costInputType(View v)
        {
                boolean checked = ((RadioButton)v).isChecked();
                if(v.getId()==R.id.rByManager)
                {
                        if(checked)cInputType="manager";
                }
                else if(v.getId()==R.id.rByMyself)
                {
                        if(checked)cInputType="myself";
                }
        }
}
