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

        //component variable declaration
        private Button bStart;
        private RelativeLayout layout1,layout2;
        private Animation animation1,animation2;

        //object declaration
        SharedPreferenceData sharedPreferenceData;

        private ImageView circleImageView;

        private static final String INPUT_TASK_COMPLETE = "inputTask";
        AlertDialogClass dialogClass;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                Fabric.with(this, new Crashlytics());
                setContentView(R.layout.first_page);
                initComponent();
                //startActivity();
                threadStart();
                //alertDialog();
        }


        @Override
        protected void onStart() {
                super.onStart();
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
                dialogClass = new AlertDialogClass(this);

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


       private void alertDialog()
       {
               getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
               iOSDialogBuilder builder = new iOSDialogBuilder(FirstPage.this);

                       builder.setTitle("Error")
                       .setSubtitle("saia")
                       .setBoldPositiveLabel(true)
                       .setCancelable(false)
                       .setPositiveListener("ok",new iOSDialogClickListener() {
                               @Override
                               public void onClick(iOSDialog dialog) {
                                       Toast.makeText(FirstPage.this,"Clicked!",Toast.LENGTH_LONG).show();
                                       dialog.dismiss();

                               }
                       })
                       .setNegativeListener("cancel", new iOSDialogClickListener() {
                               @Override
                               public void onClick(iOSDialog dialog) {
                                       dialog.dismiss();
                               }
                       })
                       .build().show();
       }
}
