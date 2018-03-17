package com.atik_faysal.mealcounter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.backend.SharedPreferenceData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static android.content.ContentValues.TAG;


/**
 * Created by USER on 1/28/2018.
 */

public class EditYourProfile extends AppCompatActivity
{
        //class object declaration
        private AlertDialogClass dialogClass;
        private CheckInternetIsOn internetIsOn;
        private DatabaseBackgroundTask databaseBackgroundTask;
        private SharedPreferenceData sharedPreferenceData;
        private NeedSomeMethod someMethod;

        //component variable
        private SwipeRefreshLayout refreshLayout;
        private Button bEdit;
        private Toolbar toolbar;
        private TextView txtTaka,txtGroup,txtUserName,txtDate;
        private EditText eName,eEmail,eAddress,eFaWord,ePhone;
        private JSONArray jsonArray;
        private JSONObject jsonObject;

        private String name,userName,phone,email,address,fWord,taka,group,date;
        private String currentUser;
        private final static String FILE_URL = "http://192.168.56.1/json_read_member_info.php";
        private static String POST_DATA;
        private final static String FILE_URL1 = "http://192.168.56.1/update_user_info.php";
        private static String POST_DATA1;
        private static final String USER_INFO = "currentInfo";


        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.profile);
                initComponent();
        }

        //initialize all user information related variable by getText from textView or editText
        private void initComponent()
        {
                toolbar = findViewById(R.id.toolbar1);
                setSupportActionBar(toolbar);
                txtTaka = findViewById(R.id.txtTaka);
                txtUserName = findViewById(R.id.txtUserName);
                txtGroup = findViewById(R.id.txtGroup);
                txtDate = findViewById(R.id.gDate);

                eName = findViewById(R.id.groupName);
                eEmail = findViewById(R.id.gAddress);
                eAddress = findViewById(R.id.gTime);
                eFaWord = findViewById(R.id.gDescription);
                ePhone = findViewById(R.id.txtPhoneNumber);

                refreshLayout = findViewById(R.id.layout1);
                refreshLayout.setColorSchemeResources(R.color.color2,R.color.red,R.color.color6);
                bEdit = findViewById(R.id.bEdit);
                bEdit.setEnabled(false);

                sharedPreferenceData = new SharedPreferenceData(this);
                internetIsOn = new CheckInternetIsOn(this);
                dialogClass = new AlertDialogClass(this);
                someMethod = new NeedSomeMethod(this);

                //calling method
                someMethod.reloadPage(refreshLayout,EditYourProfile.class);
                setToolbar();
                onButtonClick();

                currentUser = sharedPreferenceData.getCurrentUserName();

               if(internetIsOn.isOnline())
               {
                       try {
                               if(currentUser!=null)
                                       POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8");
                               else Toast.makeText(this,"under construction",Toast.LENGTH_SHORT).show();

                               databaseBackgroundTask = new DatabaseBackgroundTask(this);
                               databaseBackgroundTask.setOnResultListener(onAsyncTaskInterface);
                               databaseBackgroundTask.execute(FILE_URL,POST_DATA);
                       } catch (UnsupportedEncodingException e) {
                               e.printStackTrace();
                       }
               }else dialogClass.noInternetConnection();

        }

        //set a toolbar,above the page
        private void setToolbar()
        {
                toolbar.setTitleTextColor(getResources().getColor(R.color.white));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                toolbar.setNavigationIcon(R.drawable.icon_back);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                finish();
                        }
                });
        }

        private void onButtonClick()
        {
                bEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                String getName,getEmail,getAddress,getFword;
                                getName = eName.getText().toString();
                                getEmail = eEmail.getText().toString();
                                getAddress = eAddress.getText().toString();
                                getFword = eFaWord.getText().toString();
                                if(internetIsOn.isOnline())
                                {
                                        if(checkUserInfo(getName,getEmail))
                                        {
                                                try
                                                {
                                                        POST_DATA1 = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8")+"&"
                                                                +URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(getName,"UTF-8")+"&"
                                                                +URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(getEmail,"UTF-8")+"&"
                                                                +URLEncoder.encode("address","UTF-8")+"="+URLEncoder.encode(getAddress,"UTF-8")+"&"
                                                                +URLEncoder.encode("fWord","UTF-8")+"="+URLEncoder.encode(getFword,"UTF-8");
                                                } catch (UnsupportedEncodingException e) {
                                                        e.printStackTrace();
                                                }
                                                DatabaseBackgroundTask checkBackgroundTask = new DatabaseBackgroundTask(EditYourProfile.this);
                                                checkBackgroundTask.setOnResultListener(onAsyncTaskInterface);
                                                checkBackgroundTask.execute(FILE_URL1,POST_DATA1);

                                        }
                                }else dialogClass.noInternetConnection();
                        }
                });
        }

        //check user info for that they follow the input condition
        private boolean checkUserInfo(String name,String email)
        {
                boolean flag = true;
                if(name.length()<3)
                {
                        eName.setError("Invalid name");
                        eName.requestFocus();
                        flag = false;
                }

                for(char c : name.toCharArray()){
                        if(Character.isDigit(c)){
                                eName.setError("Invalid Name");
                                flag = false;
                                eName.requestFocus();
                        }
                }

                if(eName.getText().toString().isEmpty())
                {
                        eName.setError("Invalid name");
                        eName.requestFocus();
                        flag = false;
                }

                if(!email.contains("@"))
                {
                        eEmail.setError("Invalid email");
                        eEmail.requestFocus();
                        flag = false;
                }

                if(eEmail.getText().toString().isEmpty())
                {
                        eEmail.setError("Invalid email");
                        eEmail.requestFocus();
                        flag = false;
                }

                if(eAddress.getText().toString().isEmpty())
                {
                        eAddress.setError("Invalid address");
                        eAddress.requestFocus();
                        flag = false;
                }

                if(eFaWord.getText().toString().isEmpty())
                {
                        eFaWord.setError("Invalid word");
                        eFaWord.requestFocus();
                        flag = false;
                }

                return flag;
        }

        //process json data to string
        private void processJsonData(String jsonData)
        {
                try {
                        jsonObject = new JSONObject(jsonData);
                        jsonArray = jsonObject.optJSONArray("information");

                        int count = 0;
                        while(count<jsonArray.length())
                        {
                                JSONObject jObject = jsonArray.getJSONObject(count);
                                name = jObject.getString("name");
                                userName = jObject.getString("userName");
                                email = jObject.getString("email");
                                phone = jObject.getString("phone");
                                address = jObject.getString("address");
                                taka = jObject.getString("taka");
                                fWord = jObject.getString("fWord");
                                group = jObject.getString("group");
                                date = jObject.getString("date");
                                count++;
                        }
                } catch (JSONException e) {
                        e.printStackTrace();
                }


                eName.setText("  "+name);
                txtUserName.setText("  "+userName);
                ePhone.setText("  "+phone);
                eAddress.setText("  "+address);
                eFaWord.setText("  "+fWord);
                txtDate.setText("  "+"Join  "+date);
                eEmail.setText("  "+email);
                txtTaka.setText(taka);
                txtGroup.setText(group);
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                MenuInflater menuInflater = getMenuInflater();
                menuInflater.inflate(R.menu.edit,menu);
                return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
                int id;
                id = item.getItemId();
                switch (id)
                {
                        case R.id.edit:
                                eName.setFocusableInTouchMode(true);
                                eEmail.setFocusableInTouchMode(true);
                                eFaWord.setFocusableInTouchMode(true);
                                eAddress.setFocusableInTouchMode(true);
                                bEdit.setEnabled(true);
                                break;
                }
                return true;
        }

        //interface,get all information about user from database
        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String userInfo) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                       switch (userInfo)
                                       {
                                               case "failed":
                                                       Toast.makeText(EditYourProfile.this,"Error occurred to information update",Toast.LENGTH_SHORT).show();
                                                       break;
                                               case "updated":
                                                       if(internetIsOn.isOnline())
                                                       {
                                                               try {
                                                                       if(currentUser!=null)
                                                                               POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8");
                                                                       else Toast.makeText(EditYourProfile.this,"under construction",Toast.LENGTH_SHORT).show();
                                                               } catch (UnsupportedEncodingException e) {
                                                                       e.printStackTrace();
                                                               }
                                                               databaseBackgroundTask = new DatabaseBackgroundTask(EditYourProfile.this);
                                                               databaseBackgroundTask.setOnResultListener(onAsyncTaskInterface);
                                                               databaseBackgroundTask.execute(FILE_URL,POST_DATA);
                                                               Toast.makeText(EditYourProfile.this,"Information updated successfully.",Toast.LENGTH_SHORT).show();
                                                       }else dialogClass.noInternetConnection();
                                                       break;
                                               default:
                                                       if(userInfo!=null)
                                                               processJsonData(userInfo);
                                                       else Log.d(TAG,"Json object error");
                                                       break;
                                       }
                                }
                        });
                }
        };


}
