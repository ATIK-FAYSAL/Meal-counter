package com.atik_faysal.mealcounter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.Toolbar;

import com.atik_faysal.backend.*;

/**
 * Created by USER on 1/14/2018.
 */

public class SecondPage extends AppCompatActivity
{


        //String variable
        private String mInputType = "null";

        //Component variable declaration
        Intent intent;
        private Button bContinue;


        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.second_page);
                initComponent();//initialize all class and component
                startActivity();//start another activity
        }

        private void initComponent()
        {
                //component initialize
                bContinue = findViewById(R.id.bContinue);

        }

        private void startActivity()
        {
                bContinue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                if(mInputType.equals("null")) Toast.makeText(SecondPage.this,"Please choose an option",Toast.LENGTH_SHORT).show();
                                else
                                {
                                        intent = new Intent(SecondPage.this,ThirdPage.class);
                                        intent.putExtra("mInputType",mInputType);
                                        startActivity(intent);
                                }
                        }
                });
        }

        public void mealInputType(View v)
        {
                boolean checked = ((RadioButton)v).isChecked();
                if(v.getId()==R.id.rByManager)
                {
                        if(checked)mInputType="manager";
                }
                else if(v.getId()==R.id.rByMyself)
                {
                        if(checked)mInputType="myself";
                }
        }
}
