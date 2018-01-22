package com.atik_faysal.mealcounter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.atik_faysal.backend.SharedPreferenceData;

public class HomePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

        //component variable
        private Toolbar toolbar;
        private DrawerLayout drawer;
        private ActionBarDrawerToggle toggle;


        private SharedPreferenceData sharedPreferenceData;


        private final static String USER_LOGIN = "userLogIn";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.home_page);
                initComponent();
                clossApp();
        }

        private void initComponent()
        {
                toolbar =  findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                drawer =  findViewById(R.id.drawer_layout);
                toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.addDrawerListener(toggle);
                toggle.syncState();

                NavigationView navigationView =  findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(this);

                sharedPreferenceData = new SharedPreferenceData(this);
        }

        @Override
        public void onBackPressed() {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Exit");
                builder.setMessage("Want to exit ?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("flag",true);
                                startActivity(intent);
                        }
                });
                builder.setNegativeButton("no",null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.home_page, menu);
                return true;
        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if(id==R.id.makeGroup)
                {
                        startActivity(new Intent(HomePageActivity.this,MakeMyGroup.class));
                }else if(id==R.id.editProfile)
                {

                }else if(id==R.id.acceptRequest)
                {

                }else if(id==R.id.makeAdmin)
                {

                }else if(id==R.id.member)
                {

                }else if(id==R.id.setAlarm)
                {

                }else if(id==R.id.setting)
                {

                }else if(id==R.id.feedback)
                {

                }else if(id==R.id.aboutUs)
                {

                }else if(id==R.id.logout)
                        userLogOut();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
        }


        protected void userLogOut()
        {
                final ProgressDialog progressDialog = ProgressDialog.show(HomePageActivity.this, "Please wait", "User Log out...", true);
                progressDialog.setCancelable(true);
                new Thread(new Runnable() {
                        @Override
                        public void run() {
                                try {
                                        Thread.sleep(2500);
                                } catch (Exception e) {
                                }
                                progressDialog.dismiss();
                        }
                }).start();
                progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                                sharedPreferenceData.ifUserLogIn(USER_LOGIN,false);
                                finish();
                        }
                });
        }

        private void clossApp()
        {
                if(getIntent().getBooleanExtra("flag",false))finish();
        }
}
