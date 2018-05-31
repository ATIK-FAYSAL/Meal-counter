package com.atik_faysal.mealcounter;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class FirstPage extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                Fabric.with(this, new Crashlytics());
                setContentView(R.layout.first_page);
                TextView textView = findViewById(R.id.text);
                Typeface typeface = Typeface.createFromAsset(getResources().getAssets(),"fonts/Pacifico.ttf");
                textView.setTypeface(typeface);
                threadStart();
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
}
