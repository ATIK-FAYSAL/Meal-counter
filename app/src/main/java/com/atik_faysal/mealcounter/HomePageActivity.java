package com.atik_faysal.mealcounter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.DownLoadImageTask;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.backend.GetImportantData;
import com.atik_faysal.backend.RegisterDeviceToken;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.model.SearchableModel;
import com.atik_faysal.others.AboutUs;
import com.atik_faysal.others.CreateSession;
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

        //component array object
        private CardView[] cardViews = new CardView[8];
        private int[] cardViewId = {R.id.cardView1,R.id.cardView2,R.id.cardView3,R.id.cardView4,R.id.cardView5,R.id.cardView6,R.id.cardView7,R.id.cardView8};
        private ImageView[] imageViews = new ImageView[8];
        private int[] imageViewId = {R.id.image1,R.id.image2,R.id.image3,R.id.image4,R.id.image5,R.id.image6,R.id.image7,R.id.image8};
        private ImageView userImage;
        private FloatingActionButton fab;

        private SharedPreferenceData sharedPreferenceData;
        private CheckInternetIsOn internetIsOn;
        private AlertDialogClass dialogClass;
        private NeedSomeMethod someMethod;
        private NoResultFound noResultFound;


        private final static String USER_LOGIN = "userLogIn";
        private String currentUser;
        private String userType;
        //private final static String FILE = "http://192.168.56.1/getGroupName.php";

        private ArrayList<SearchableModel>groupList;

        private TextView txtTotalCost,txtTotalMeal,txtTodayMeal,txtMyMeal;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.home_page);
                initComponent();
        }

        //on strat method ,here get some info from shared pref and take appropriate action
        @Override
        protected void onStart() {
                super.onStart();

                //String fUrl = "http://192.168.56.1/checkMemType.php";
                String postData;
                if(internetIsOn.isOnline())
                {
                        try {
                                postData = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8");
                                DatabaseBackgroundTask backgroundTask = new DatabaseBackgroundTask(this);
                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                backgroundTask.execute(getResources().getString(R.string.memberType),postData);

                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }

                        myImage();//download image and set image in imageview
                }else dialogClass.noInternetConnection();
        }

        //backpressed method,default method
        @Override
        public void onBackPressed() {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                iOSDialogBuilder builder = new iOSDialogBuilder(HomePageActivity.this);

                builder.setTitle("App termination")
                        .setSubtitle("Do you want to close app?")
                        .setBoldPositiveLabel(true)
                        .setCancelable(false)
                        .setPositiveListener("Close App",new iOSDialogClickListener() {
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

        //default method,set menu option
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
                                noResultFound.checkValueIsExist(currentUser,MemberJoinRequest.class,"request");
                                break;

                        case R.id.makeAdmin:
                                startActivity(new Intent(HomePageActivity.this,AdminPanel.class));
                                break;

                        case R.id.member:
                                if(internetIsOn.isOnline())
                                        noResultFound.checkValueIsExist(currentUser,AllMemberList.class,"member");
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
                                startActivity(new Intent(this, AboutUs.class));
                                break;

                        case R.id.logout:
                                userLogOut();
                                break;

                }

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
        }


        //get image from shared pref,if image is already downloaded,else it will download user image and save into shared pref
        private void myImage()
        {
                if(sharedPreferenceData.myImageIsSave())
                {
                        Bitmap bitmap= sharedPreferenceData.getMyImage();
                        userImage.setImageBitmap(bitmap);
                }else
                {
                        DownLoadImageTask imageTask = new DownLoadImageTask(this);
                        imageTask.execute(currentUser);
                }

        }

        //initialize all component and class object,also call some method
        private void initComponent()
        {

                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.addDrawerListener(toggle);
                toggle.syncState();

                NavigationView navigationView =  findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(this);
                SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
                refreshLayout.setColorSchemeResources(R.color.color2,R.color.red,R.color.color6);
                View view = navigationView.inflateHeaderView(R.layout.nav_header_home_page);
                TextView txtUserName = view.findViewById(R.id.txtName);
                userImage = view.findViewById(R.id.userImage);
                txtTotalCost = findViewById(R.id.txtTaka);
                txtTotalMeal = findViewById(R.id.txtMeal);
                txtTodayMeal = findViewById(R.id.txtTmeal);
                txtMyMeal = findViewById(R.id.txtMmeal);
                TextView txtDate = findViewById(R.id.txtDate);
                fab = findViewById(R.id.fab);

                initObject();//initialize cardView and imageView
                //other initialize
                sharedPreferenceData = new SharedPreferenceData(this);
                internetIsOn = new CheckInternetIsOn(this);
                dialogClass = new AlertDialogClass(this);
                someMethod = new NeedSomeMethod(this);
                noResultFound = new NoResultFound(this);
                GetImportantData importantData = new GetImportantData(this);

                FirebaseMessaging.getInstance().subscribeToTopic("test");
                String token = FirebaseInstanceId.getInstance().getToken();

                currentUser = sharedPreferenceData.getCurrentUserName();
                userType = sharedPreferenceData.getUserType();
                String date = someMethod.getDateWithTime();
                txtUserName.setText(currentUser);
                txtDate.setText(someMethod.getDate());

                //calling method
                RegisterDeviceToken.registerToken(token,currentUser, date);
                someMethod.reloadPage(refreshLayout,HomePageActivity.class);
                someMethod.userCurrentStatus(currentUser,"active");
                someMethod.myGroupName(currentUser);
                importantData.myGroupType(currentUser);
                importantData.getCurrentSession(currentUser);
                closeApp();


               if(internetIsOn.isOnline())
               {
                       try {
                               String POST_DATA = URLEncoder.encode("userName", "UTF-8")+"="+URLEncoder.encode("", "UTF-8");
                               groupList = new ArrayList<>();
                               DatabaseBackgroundTask backgroundTask = new DatabaseBackgroundTask(HomePageActivity.this);
                               backgroundTask.setOnResultListener(taskInterface);
                               backgroundTask.execute(getResources().getString(R.string.allGroupName), POST_DATA);
                       } catch (UnsupportedEncodingException e) {
                               e.printStackTrace();
                       }


                       try {
                               //String url = "http://192.168.56.1/homePageInfo.php";
                               String POST_DATA = URLEncoder.encode("userName", "UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getCurrentUserName(), "UTF-8")+"&"
                                       +URLEncoder.encode("group", "UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getMyGroupName(), "UTF-8")+"&"
                                       +URLEncoder.encode("date", "UTF-8")+"="+URLEncoder.encode(someMethod.getDate(), "UTF-8");
                               DatabaseBackgroundTask backgroundTask = new DatabaseBackgroundTask(HomePageActivity.this);
                               backgroundTask.setOnResultListener(anInterface);
                               backgroundTask.execute(getResources().getString(R.string.homePageInfo), POST_DATA);
                       } catch (UnsupportedEncodingException e) {
                               e.printStackTrace();
                       }

               }else dialogClass.noInternetConnection();

        }

        //initialize card view and image view object
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

        // on button click method
        public void onButtonClick(View view)
        {
                initObject();
                int id = view.getId();

                if(id==cardViewId[0]||id==imageViewId[0])
                {
                        if(sharedPreferenceData.getUserType().equals("nope"))
                                dialogClass.notMember();
                        else
                        {
                                if(sharedPreferenceData.getmyCurrentSession().equals("nope"))
                                        dialogClass.error("No session available,please contact with admin.");
                                else startActivity(new Intent(HomePageActivity.this,MealClass.class));
                        }
                        //new MealClass().setTabLayout();
                }else if(id==cardViewId[1]||id==imageViewId[1])
                {
                        if(sharedPreferenceData.getUserType().equals("nope"))
                                dialogClass.notMember();
                        else
                        {
                                if (sharedPreferenceData.getmyCurrentSession().equals("nope"))
                                        dialogClass.error("No session available,please contact with admin.");
                                else
                                        noResultFound.checkValueIsExist(sharedPreferenceData.getCurrentUserName(),ApproveBalance.class,"approval");
                        }
                               // startActivity(new Intent(HomePageActivity.this,ApproveBalance.class));
                }else if(id==cardViewId[2]||id==imageViewId[2])
                {
                        if(sharedPreferenceData.getUserType().equals("nope"))
                                dialogClass.notMember();
                        else
                        {
                                if (sharedPreferenceData.getmyCurrentSession().equals("nope"))
                                        dialogClass.error("No session available,please contact with admin.");
                                else
                                {
                                        if(sharedPreferenceData.getMyGroupType().equals("public"))
                                                startActivity(new Intent(HomePageActivity.this,CostForPublicGroup.class));
                                        else if((sharedPreferenceData.getMyGroupType().equals("secret")||sharedPreferenceData.getMyGroupType().equals("close"))
                                                &&sharedPreferenceData.getUserType().equals("admin"))
                                                startActivity(new Intent(HomePageActivity.this,CostOfSecretCloseGroup.class));
                                        else if((sharedPreferenceData.getMyGroupType().equals("secret")||sharedPreferenceData.getMyGroupType().equals("close"))
                                                &&sharedPreferenceData.getUserType().equals("member"))
                                                startActivity(new Intent(HomePageActivity.this,CostForSecretCloseMem.class));
                                }
                        }
                }else if(id==cardViewId[3]||id==imageViewId[3])
                {
                        if(sharedPreferenceData.getUserType().equals("nope"))
                                dialogClass.notMember();
                        else startActivity(new Intent(HomePageActivity.this,SetTabLayout.class));
                        //startActivity(new Intent(HomePageActivity.this,MakeShoppingList.class));
                }else if(id==cardViewId[4]||id==imageViewId[4])
                {
                        if(sharedPreferenceData.getUserType().equals("nope"))
                                dialogClass.notMember();
                        else Toast.makeText(this,"click on button 5",Toast.LENGTH_SHORT).show();
                }else if(id==cardViewId[5]||id==imageViewId[5])
                {
                        if(sharedPreferenceData.getUserType().equals("nope"))
                                dialogClass.notMember();
                        else startActivity(new Intent(HomePageActivity.this,NoticeBoard.class));

                }else if(id==cardViewId[6]||id==imageViewId[6])
                {
                        if(sharedPreferenceData.getUserType().equals("nope"))
                                dialogClass.notMember();
                        else
                        {
                                if (sharedPreferenceData.getmyCurrentSession().equals("nope"))
                                        dialogClass.error("No session available,please contact with admin.");
                                else
                                        startActivity(new Intent(HomePageActivity.this,MonthReport.class));
                        }
                }else if(id==cardViewId[7]||id==imageViewId[7])
                {
                        if(sharedPreferenceData.getUserType().equals("nope"))
                                dialogClass.notMember();
                        else Toast.makeText(this,"click on button 8",Toast.LENGTH_SHORT).show();
                }else if(id==cardViewId[8]||id==imageViewId[8])
                {
                        if(sharedPreferenceData.getUserType().equals("nope"))
                                dialogClass.notMember();
                        else Toast.makeText(this,"click on button 9",Toast.LENGTH_SHORT).show();
                }


                fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                if(sharedPreferenceData.getUserType().equals("admin"))
                                {
                                        if(!sharedPreferenceData.getmyCurrentSession().equals("nope"))
                                                dialogClass.error("You already in a session,please finish current session and retry.");
                                        else startActivity(new Intent(HomePageActivity.this, CreateSession.class));
                                }
                                else dialogClass.error("Only admin can create new session");
                        }
                });

        }

        //get json data from server and convert to string,also set in component
        private void processJsonData(String jsonData)
        {
                try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray jsonArray = jsonObject.optJSONArray("groupName");

                        int count=0;

                        while (count< jsonArray.length())
                        {
                                JSONObject jObject = jsonArray.getJSONObject(count);
                                groupList.add(new SearchableModel(jObject.getString("groupName")));
                                count++;
                        }

                } catch (JSONException e) {
                        e.printStackTrace();
                }
        }

        //log out method
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

        //app terminated method
        private void closeApp()
        {
                if(getIntent().getBooleanExtra("flag",false))finish();
        }

        //get group information from server
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

        //get member information from server
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

        //get all home page information
        OnAsyncTaskInterface anInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        String totalMeal=null,totalTaka = null,myMeal=null,todayMeal=null;
                                        if(message!=null)
                                        {
                                                try {
                                                        int count=0;
                                                        JSONObject jsonObject = new JSONObject(message);
                                                        JSONArray jsonArray = jsonObject.optJSONArray("jsonData");
                                                        while (count<jsonArray.length())
                                                        {
                                                                JSONObject jObject = jsonArray.getJSONObject(count);
                                                                totalTaka = jObject.getString("cost");
                                                                totalMeal = jObject.getString("meals");
                                                                todayMeal = jObject.getString("tmeal");
                                                                myMeal = jObject.getString("mmeal");
                                                                count++;
                                                        }

                                                        txtTotalCost.setText(totalTaka);
                                                        txtTotalMeal.setText(totalMeal);
                                                        txtTodayMeal.setText(todayMeal);
                                                        txtMyMeal.setText(myMeal);

                                                } catch (JSONException e) {
                                                        Toast.makeText(HomePageActivity.this,"Error : "+e.toString(),Toast.LENGTH_LONG).show();
                                                }

                                        }
                                }
                        });
                }
        };
}
