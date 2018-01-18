package com.atik_faysal.mealcounter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirstPage extends AppCompatActivity {

        private Button bStart;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.first_page);
                initComponent();
                startActivity();
        }


        private void initComponent()
        {
                bStart = findViewById(R.id.bStart);
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
