package com.atik_faysal.mealcounter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.atik_faysal.backend.SaveGroupInformation;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by USER on 1/22/2018.
 * initComponent-->Void.    initialize all component and object,also call some method.
 */

public class MakeMyGroup extends AppCompatActivity
{
        protected EditText groupName,groupId,groupAddress,groupDescription;
        protected Button bCreate;


        private String gName,gId,gAddress,gDescription;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.make_group_layout);
                initComponent();
                onButtonClick();
        }


        protected void initComponent()
        {
                groupName = findViewById(R.id.gName);
                groupId = findViewById(R.id.gId);
                groupAddress = findViewById(R.id.gAddress);
                groupDescription = findViewById(R.id.gDescription);
                bCreate = findViewById(R.id.bCreate);
        }

        protected String getDate()
        {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MMM.dd hh:mm aaa");
                String date = dateFormat.format(calendar.getTime());
                return date;
        }


        private void getGroupInformation()
        {
                gName = groupName.getText().toString();
                gId = groupId.getText().toString();
                gAddress = groupAddress.getText().toString();
                gDescription = groupDescription.getText().toString();
        }

        protected void onButtonClick()
        {
                bCreate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                getGroupInformation();
                                new SaveGroupInformation(MakeMyGroup.this).execute(gName,gId,gAddress,gDescription,getDate());
                                //new SaveGroupInformation(MakeMyGroup.this).execute("toma","111","dhaka","nothing",getDate());
                        }
                });
        }
}
