package com.atik_faysal.mealcounter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.atik_faysal.backend.GetSearchableGroup;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.model.SearchableModel;
import com.facebook.accountkit.ui.LoginType;

import java.util.ArrayList;

import interfaces.SearchInterfaces;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;
import static android.content.ContentValues.TAG;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class HomePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

        //component variable
        private Toolbar toolbar;
        private DrawerLayout drawer;
        private ActionBarDrawerToggle toggle;

        private SharedPreferenceData sharedPreferenceData;
        private CheckInternetIsOn internetIsOn;
        private AlertDialogClass dialogClass;


        private final static String USER_LOGIN = "userLogIn";


        private ArrayList<SearchableModel>groupList ;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.home_page);
                initComponent();
                closeApp();
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
                internetIsOn = new CheckInternetIsOn(this);
                dialogClass = new AlertDialogClass(this);
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
                MenuInflater menuInflater = getMenuInflater();
                menuInflater.inflate(R.menu.search, menu);
                return true;
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
                int res_id;
                res_id = item.getItemId();
                if(res_id==R.id.search)
                {
                        if(internetIsOn.isOnline())
                        {
                                groupList = new ArrayList<>();
                                GetSearchableGroup searchableGroup = new GetSearchableGroup(HomePageActivity.this);
                                searchableGroup.setOnResultListener(searchInterfaces);
                                searchableGroup.execute();

                                new SimpleSearchDialogCompat(this, "Search", "enter name", null, groupList, new SearchResultListener<Searchable>() {
                                        @Override
                                        public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {


                                                baseSearchDialogCompat.dismiss();
                                        }
                                }).show();
                        }else dialogClass.noInternetConnection();
                }
                return true;
        }

        SearchInterfaces searchInterfaces = new SearchInterfaces() {
                @Override
                public void onResultSuccess(final ArrayList<String> list) {
                        runOnUiThread(new Runnable() {
                                public void run() {
                                        for(int i =0;i<list.size();i++)
                                                groupList.add(new SearchableModel(list.get(i)));

                                }
                        });
                }
        };


        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                switch (id)
                {
                        case R.id.makeGroup:
                                if(internetIsOn.isOnline())
                                        startActivity(new Intent(HomePageActivity.this,EditYourProfile.class));
                                else dialogClass.noInternetConnection();
                                break;

                        case R.id.editProfile:
                                if(internetIsOn.isOnline())
                                        startActivity(new Intent(HomePageActivity.this,EditYourProfile.class));
                                else dialogClass.noInternetConnection();
                                break;

                        case R.id.acceptRequest:
                                break;

                        case R.id.makeAdmin:
                                break;

                        case R.id.member:
                                break;

                        case R.id.setAlarm:
                                break;

                        case R.id.setting:
                                break;

                        case R.id.feedback:
                                startActivity(new Intent(HomePageActivity.this,Feedback.class));
                                break;

                        case R.id.aboutUs:
                                break;

                        case R.id.logout:
                                userLogOut();
                                break;

                }

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

        private void closeApp()
        {
                if(getIntent().getBooleanExtra("flag",false))finish();
        }
}
