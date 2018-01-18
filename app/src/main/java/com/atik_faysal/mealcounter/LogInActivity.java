package com.atik_faysal.mealcounter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 1/17/2018.
 */

public class LogInActivity extends AppCompatActivity
{

        private TextView txtSingUp,txtForgotPass;
        private Button bSignIn;
        private CheckBox checkBox;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.log_in_page);
                initComponent();
                actionComponent();
        }


        private void initComponent()
        {
                txtSingUp = findViewById(R.id.txtSignUp);
                txtForgotPass = findViewById(R.id.txtForgotPass);
                bSignIn = findViewById(R.id.bSignIn);
                checkBox = findViewById(R.id.cRemember);
        }


        private void actionComponent()
        {
                txtSingUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                startActivity(new Intent(LogInActivity.this,CreateNewAccount.class));
                        }
                });
                bSignIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                               startActivity(new Intent(LogInActivity.this,HomePageActivity.class));
                        }
                });
        }

}
