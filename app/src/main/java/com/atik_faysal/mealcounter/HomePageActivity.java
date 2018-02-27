package com.atik_faysal.mealcounter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.MenuInflater;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.backend.InfoBackgroundTask;
import com.atik_faysal.backend.InfoBackgroundTask.OnAsyncTaskInterface;
import com.atik_faysal.backend.RegisterDeviceToken;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.model.SearchableModel;
import com.atik_faysal.others.NoResultFound;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;

public class HomePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

        //component variable
        private Toolbar toolbar;
        private DrawerLayout drawer;
        private ActionBarDrawerToggle toggle;
        private View view;
        private TextView textView;
        private SwipeRefreshLayout refreshLayout;

        //component array object
        private CardView[] cardViews = new CardView[8];
        private int[] cardViewId = {R.id.cardView1,R.id.cardView2,R.id.cardView3,R.id.cardView4,R.id.cardView5,R.id.cardView6,R.id.cardView7,R.id.cardView8};
        private ImageView[] imageViews = new ImageView[8];
        private int[] imageViewId = {R.id.image1,R.id.image2,R.id.image3,R.id.image4,R.id.image5,R.id.image6,R.id.image7,R.id.image8};


        private SharedPreferenceData sharedPreferenceData;
        private CheckInternetIsOn internetIsOn;
        private AlertDialogClass dialogClass;
        private InfoBackgroundTask backgroundTask;
        private NeedSomeMethod someMethod;
        private NoResultFound noResultFound;


        private final static String USER_LOGIN = "userLogIn";
        private final static String USER_INFO = "currentInfo";
        private String currentUser,userType,date;
        private final static String FILE = "http://192.168.56.1/getGroupName.php";
        private static String POST_DATA ;

        private ArrayList<SearchableModel>groupList;
        private JSONObject jsonObject;
        private JSONArray jsonArray;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.home_page);
                initComponent();
        }

        @Override
        protected void onStart() {
                super.onStart();

                String fUrl = "http://192.168.56.1/checkMemType.php";
                String postData;
                if(internetIsOn.isOnline())
                {
                        try {
                                postData = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8");
                                backgroundTask = new InfoBackgroundTask(this);
                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                backgroundTask.execute(fUrl,postData);

                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }
                }else dialogClass.noInternetConnection();
        }

        @Override
        public void onBackPressed() {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                iOSDialogBuilder builder = new iOSDialogBuilder(HomePageActivity.this);

                builder.setTitle("App termination")
                        .setSubtitle("Do you want to close app?")
                        .setBoldPositiveLabel(true)
                        .setCancelable(false)
                        .setPositiveListener("ok",new iOSDialogClickListener() {
                                @Override
                                public void onClick(iOSDialog dialog) {
                                        Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.putExtra("flag",true);
                                        startActivity(intent);
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
                                new SimpleSearchDialogCompat(this, "Search", "enter name", null, groupList, new SearchResultListener<Searchable>() {
                                        @Override
                                        public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {

                                                Intent intent = new Intent(HomePageActivity.this,JoinRequestToGroup.class);
                                                intent.putExtra("group",searchable.getTitle());
                                                startActivity(intent);
                                                baseSearchDialogCompat.dismiss();
                                        }
                                }).show();
                        }else dialogClass.noInternetConnection();
                }
                return true;
        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                userType = sharedPreferenceData.getUserType();

                switch (id)
                {
                        case R.id.makeGroup:
                                if(internetIsOn.isOnline())
                                        startActivity(new Intent(HomePageActivity.this,MakeMyGroup.class));
                                else dialogClass.noInternetConnection();
                                break;

                        case R.id.myGroup:
                                if(internetIsOn.isOnline())
                                        startActivity(new Intent(HomePageActivity.this,MyGroupInfo.class));
                                else dialogClass.noInternetConnection();
                                break;

                        case R.id.editProfile:
                                if(internetIsOn.isOnline())
                                        startActivity(new Intent(HomePageActivity.this,EditYourProfile.class));
                                else dialogClass.noInternetConnection();
                                break;

                        case R.id.acceptRequest:
                                if(userType.equals("admin"))
                                {
                                        //startActivity(new Intent(HomePageActivity.this,MemberJoinRequest.class));
                                        noResultFound.checkJoinRequest(currentUser,MemberJoinRequest.class,"request");
                                }else dialogClass.error("Only admin can accept request.You are not an admin.");
                                break;

                        case R.id.makeAdmin:
                                if(userType.equals("admin"))
                                {
                                        startActivity(new Intent(HomePageActivity.this,AdminPanel.class));
                                }else dialogClass.error("Only admin can make another admin.You are not an admin.");
                                break;

                        case R.id.member:
                                if(internetIsOn.isOnline())
                                        noResultFound.checkJoinRequest(currentUser,AllMemberList.class,"member");
                                        //startActivity(new Intent(HomePageActivity.this,AllMemberList.class));
                                else dialogClass.noInternetConnection();
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

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
        }

        OnAsyncTaskInterface taskInterface = new OnAsyncTaskInterface() {

                @Override
                public void onResultSuccess(final String result) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        if(result!=null)
                                                processJsonData(result);
                                }
                        });
                }
        };

        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        if(message.equals("no result"))
                                        {
                                                someMethod.closeActivity(HomePageActivity.this,LogInActivity.class);
                                                sharedPreferenceData.ifUserLogIn(USER_LOGIN,false);
                                        }
                                        else
                                        {
                                                if(!userType.equals(message))
                                                {
                                                        sharedPreferenceData.userType(message);
                                                        userType = message;
                                                }
                                        }
                                }
                        });
                }
        };

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
                refreshLayout = findViewById(R.id.refreshLayout);
                refreshLayout.setColorSchemeResources(R.color.color2,R.color.red,R.color.color6);
                view = navigationView.inflateHeaderView(R.layout.nav_header_home_page);
                textView = view.findViewById(R.id.txtUserName);

                initObject();//initialize cardView and imageView
                //other initialize
                sharedPreferenceData = new SharedPreferenceData(this);
                internetIsOn = new CheckInternetIsOn(this);
                dialogClass = new AlertDialogClass(this);
                someMethod = new NeedSomeMethod(this);
                noResultFound = new NoResultFound(this);

                FirebaseMessaging.getInstance().subscribeToTopic("test");
                String token = FirebaseInstanceId.getInstance().getToken();

                currentUser = sharedPreferenceData.getCurrentUserName(USER_INFO);
                userType = sharedPreferenceData.getUserType();
                date = someMethod.getDate();
                textView.setText(currentUser);

                //calling method
                RegisterDeviceToken.registerToken(token,currentUser,date);
                someMethod.reloadPage(refreshLayout,HomePageActivity.class);
                someMethod.userCurrentStatus(currentUser,"active");
                someMethod.myGroupName(currentUser);
                closeApp();


                try {
                        POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode("","UTF-8");
                        groupList = new ArrayList<>();
                        InfoBackgroundTask backgroundTask = new InfoBackgroundTask(HomePageActivity.this);
                        backgroundTask.setOnResultListener(taskInterface);
                        backgroundTask.execute(FILE,POST_DATA);
                } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                }
        }

        private void initObject()
        {
                int i=0,j=0;

                while (i< cardViews.length)
                {
                        cardViews[i] = findViewById(cardViewId[j]);
                        i++;
                        j++;
                }

                int p=0,q=0;

                while (p<imageViews.length)
                {
                        imageViews[p] = findViewById(imageViewId[q]);
                        p++;
                        q++;
                }
        }

        public void onButtonClick(View view)
        {
                initObject();
                int id = view.getId();

                if(id==cardViewId[0]||id==imageViewId[0])
                {
                        if(sharedPreferenceData.getMyGroupName().equals(String.valueOf(R.string.memberType)))
                                dialogClass.notMember();
                        else Toast.makeText(this,"click on button 1",Toast.LENGTH_SHORT).show();
                }else if(id==cardViewId[1]||id==imageViewId[1])
                {
                        if(sharedPreferenceData.getMyGroupName().equals(String.valueOf(R.string.memberType)))
                                dialogClass.notMember();
                        else Toast.makeText(this,"click on button 2",Toast.LENGTH_SHORT).show();
                }else if(id==cardViewId[2]||id==imageViewId[2])
                {
                        if(sharedPreferenceData.getMyGroupName().equals(String.valueOf(R.string.memberType)))
                                dialogClass.notMember();
                        else Toast.makeText(this,"click on button 3",Toast.LENGTH_SHORT).show();
                }else if(id==cardViewId[3]||id==imageViewId[3])
                {
                        if(sharedPreferenceData.getMyGroupName().equals("nope"))
                                dialogClass.notMember();
                        else startActivity(new Intent(HomePageActivity.this,SetTabLayout.class));
                        //startActivity(new Intent(HomePageActivity.this,MakeShoppingList.class));
                }else if(id==cardViewId[4]||id==imageViewId[4])
                {
                        if(sharedPreferenceData.getMyGroupName().equals(String.valueOf(R.string.memberType)))
                                dialogClass.notMember();
                        else Toast.makeText(this,"click on button 5",Toast.LENGTH_SHORT).show();
                }else if(id==cardViewId[5]||id==imageViewId[5])
                {
                        if(sharedPreferenceData.getMyGroupName().equals(String.valueOf(R.string.memberType)))
                                dialogClass.notMember();
                        else startActivity(new Intent(HomePageActivity.this,NoticeBoard.class));

                }else if(id==cardViewId[6]||id==imageViewId[6])
                {
                        if(sharedPreferenceData.getMyGroupName().equals(String.valueOf(R.string.memberType)))
                                dialogClass.notMember();
                        else Toast.makeText(this,"click on button 7",Toast.LENGTH_SHORT).show();
                }else if(id==cardViewId[7]||id==imageViewId[7])
                {
                        if(sharedPreferenceData.getMyGroupName().equals(String.valueOf(R.string.memberType)))
                                dialogClass.notMember();
                        else Toast.makeText(this,"click on button 8",Toast.LENGTH_SHORT).show();
                }else if(id==cardViewId[8]||id==imageViewId[8])
                {
                        if(sharedPreferenceData.getMyGroupName().equals(String.valueOf(R.string.memberType)))
                                dialogClass.notMember();
                        else Toast.makeText(this,"click on button 9",Toast.LENGTH_SHORT).show();
                }

        }

        private void processJsonData(String jsonData)
        {
                try {
                        jsonObject = new JSONObject(jsonData);
                        jsonArray = jsonObject.optJSONArray("groupName");

                        int count=0;

                        while (count<jsonArray.length())
                        {
                                JSONObject jObject = jsonArray.getJSONObject(count);
                                groupList.add(new SearchableModel(jObject.getString("groupName")));
                                count++;
                        }

                } catch (JSONException e) {
                        e.printStackTrace();
                }
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
                                        someMethod.userCurrentStatus(currentUser,"inactive");
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
