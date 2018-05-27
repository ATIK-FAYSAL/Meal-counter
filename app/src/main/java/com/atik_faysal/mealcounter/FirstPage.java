package com.atik_faysal.mealcounter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.atik_faysal.backend.SharedPreferenceData;
import com.crashlytics.android.Crashlytics;

import de.hdodenhof.circleimageview.CircleImageView;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import io.fabric.sdk.android.Fabric;

public class FirstPage extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                Fabric.with(this, new Crashlytics());
                setContentView(R.layout.first_page);
                threadStart();
                //alertDialog();
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
