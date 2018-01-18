package com.atik_faysal.mealcounter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class ThirdPage extends AppCompatActivity
{
        private Button bFinish;
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.third_page);
                initComponent();
                startActivity();
        }

        private void initComponent()
        {
                bFinish = findViewById(R.id.bFinish);
        }

        private void startActivity()
        {
                bFinish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                startActivity(new Intent(ThirdPage.this,LogInActivity.class));
                        }
                });
        }
}
