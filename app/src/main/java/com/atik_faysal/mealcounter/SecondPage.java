package com.atik_faysal.mealcounter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by USER on 1/14/2018.
 */

public class SecondPage extends AppCompatActivity
{
        private Button bContinue;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.second_page);
                initComponent();
                startActivity();
        }

        private void initComponent()
        {
                bContinue = findViewById(R.id.bContinue);
        }

        private void startActivity()
        {
                bContinue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                startActivity(new Intent(SecondPage.this,ThirdPage.class));
                        }
                });
        }

}
