package com.atik_faysal.mealcounter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import com.atik_faysal.backend.GetDataFromServer;
import com.atik_faysal.backend.PostData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.backend.SharedPreferenceData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by USER on 2/5/2018.
 */

public class MyGroupInfo extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener
{
     private TextView groupId,gAdmin,gMember,gTime,gDate,gType;
     private EditText gName,gAddress,gDescription;
     private Button bEdit;


     private String currentUser,userType;
     private String name,id,address,description,type,member,time,date,admin;

     private AlertDialogClass dialogClass;
     private CheckInternetIsOn internetIsOn;
     private  SharedPreferenceData sharedPreferenceData;
     private NeedSomeMethod someMethod;
     private Calendar calendar;
     private TimePickerDialog timePickerDialog;

     private int hour;
     private int minute;

     AlertDialog alertDialog;

     @Override
     protected void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.group_info);
          initComponent();
     }

     //initialize all user information related variable by getText from textView or editText
     private void initComponent()
     {
          groupId = findViewById(R.id.txtName);
          gAdmin = findViewById(R.id.txtTaka);
          gAddress = findViewById(R.id.gAddress);
          gMember = findViewById(R.id.gMember);
          gTime = findViewById(R.id.gTime);
          gDate = findViewById(R.id.gDate);
          gName = findViewById(R.id.groupName);
          gType = findViewById(R.id.txtPhoneNumber);
          gDescription = findViewById(R.id.gDescription);
          SwipeRefreshLayout refreshLayout = findViewById(R.id.layout1);
          refreshLayout.setColorSchemeResources(R.color.color2,R.color.red,R.color.color6);
          bEdit = findViewById(R.id.buEdit);
          //editable false
          bEdit.setEnabled(false);
          bEdit.setBackgroundDrawable(getDrawable(R.drawable.disable_button));
          gName.setEnabled(false);
          gAddress.setEnabled(false);
          gDescription.setEnabled(false);

          //object initialize
          dialogClass = new AlertDialogClass(this);
          someMethod = new NeedSomeMethod(this);
          internetIsOn = new CheckInternetIsOn(this);
          sharedPreferenceData = new SharedPreferenceData(this);
          SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(this);
          calendar = Calendar.getInstance();

          //get current user info
          currentUser = sharedPreferenceData.getCurrentUserName();
          userType = sharedPreferenceData.getUserType();

          //calling method
          //setToolbar();
          onButtonClickListener();
          initializeGroupInfo();
          someMethod.reloadPage(refreshLayout,MyGroupInfo.class);
     }

     //group info to check that they follow the input condition
     private boolean checkGroupInfo(String name,String address,String desc)
     {
          boolean flag = true;

          if(gName.getText().toString().length()<3||gName.getText().toString().length()>20)
               return  false;
          if(gDescription.getText().toString().length()<20||gDescription.getText().toString().length()>200)
               return false;
          if(gAddress.getText().toString().length()<10||gAddress.getText().toString().length()>30)
               return false;

          if(name.isEmpty())
          {
               flag = false;
               gName.setError("Invalid name");
          }
          if(address.isEmpty())
          {
               flag = false;
               gAddress.setError("Invalid address");
          }
          if (desc.isEmpty())
               description = "null";
          else description = gDescription.getText().toString();

          return flag;
     }

     //set validation for text
     private void addTextChangeListener()
     {
          final Drawable icon = getResources().getDrawable(R.drawable.icon_done);
          icon.setBounds(0,0,icon.getIntrinsicWidth(),icon.getIntrinsicHeight());

          gName.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
               @Override
               public void onTextChanged(CharSequence s, int start, int before, int count) {}

               @Override
               public void afterTextChanged(Editable s) {
                    if(gName.getText().toString().length()<3||gName.getText().toString().length()>20)
                         gName.setError("Invalid");
                    else
                         gName.setError("Valid",icon);

               }
          });

          gDescription.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

               @Override
               public void onTextChanged(CharSequence s, int start, int before, int count) {}

               @Override
               public void afterTextChanged(Editable s) {
                    if(gDescription.getText().toString().length()<20||gDescription.getText().toString().length()>200)
                         gDescription.setError("Invalid");
                    else gDescription.setError("Valid",icon);
               }
          });

          gAddress.addTextChangedListener(new TextWatcher()  {
               @Override
               public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

               @Override
               public void onTextChanged(CharSequence s, int start, int before, int count) {}

               @Override
               public void afterTextChanged(Editable s) {
                    if(gAddress.getText().toString().length()<10||gAddress.getText().toString().length()>30)
                         gAddress.setError("Invalid");
                    else gAddress.setError("Valid",icon);
               }
          });
     }

     //on button click
     private void onButtonClickListener()
     {
          ImageView imgBack = findViewById(R.id.imgBack);
          imgBack.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    finish();
               }
          });

          ImageView imgEdit = findViewById(R.id.imgEdit);

          if(!sharedPreferenceData.getUserType().equals("admin"))
          {
               imgEdit.setImageBitmap(null);
               imgEdit.setEnabled(false);
          }

          imgEdit.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    if(userType.equals("admin"))
                    {
                         gName.setEnabled(true);
                         gName.setFocusable(true);
                         gDescription.setEnabled(true);
                         gAddress.setEnabled(true);
                         gTime.setEnabled(true);
                         gType.setEnabled(true);
                         bEdit.setEnabled(true);
                         bEdit.setBackgroundDrawable(getDrawable(R.drawable.button1));
                         gName.setFocusableInTouchMode(true);
                         gAddress.setFocusableInTouchMode(true);
                         gDescription.setFocusableInTouchMode(true);
                         addTextChangeListener();//group information validation
                    }else dialogClass.error("Only admin can icon_edit_blue group info.You are not admin.");
               }
          });

          //connect to online and ready to show gorup info
          bEdit.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                    String name,address,time,type,desc;

                    name = gName.getText().toString();
                    address = gAddress.getText().toString();
                    time = gTime.getText().toString();
                    type = gType.getText().toString();
                    desc = gDescription.getText().toString();

                    if(checkGroupInfo(name,address,desc))
                    {
                         if(internetIsOn.isOnline())
                         {
                              Map<String,String> map = new HashMap<>();
                              map.put("groupID",groupId.getText().toString());
                              map.put("name",name);
                              map.put("address",address);
                              map.put("fixedTime",time);
                              map.put("groupType",type);
                              map.put("description",description);
                              PostData postData = new PostData(MyGroupInfo.this,onAsyncTaskInterface);
                              postData.InsertData(getResources().getString(R.string.editGroupInfo),map);
                              /*try {
                                   DATA = URLEncoder.encode("groupID","UTF-8")+"="+URLEncoder.encode(groupId.getText().toString(),"UTF-8")+"&"
                                        +URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"
                                        +URLEncoder.encode("address","UTF-8")+"="+URLEncoder.encode(address,"UTF-8")+"&"
                                        +URLEncoder.encode("fixedTime","UTF-8")+"="+URLEncoder.encode(time,"UTF-8")+"&"
                                        +URLEncoder.encode("groupType","UTF-8")+"="+URLEncoder.encode(type,"UTF-8")+"&"
                                        +URLEncoder.encode("description","UTF-8")+"="+URLEncoder.encode(description,"UTF-8");

                                   backgroundTask = new DatabaseBackgroundTask(MyGroupInfo.this);
                                   backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                   backgroundTask.execute(getResources().getString(R.string.editGroupInfo),DATA);
                              } catch (UnsupportedEncodingException e) {
                                   e.printStackTrace();
                              }*/
                         }else dialogClass.noInternetConnection();
                    }
               }
          });

          //change fixed time
          gTime.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    minute = calendar.get(Calendar.MINUTE);

                    timePickerDialog = new TimePickerDialog(MyGroupInfo.this,MyGroupInfo.this,hour,minute,
                         DateFormat.is24HourFormat(MyGroupInfo.this));

                    timePickerDialog.show();
               }
          });

          //change group type
          gType.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    editGroupType();
               }
          });
     }

     //get all information about group from online and show on this page
     private void initializeGroupInfo()
     {
          if(internetIsOn.isOnline())
          {
               Map<String,String>map = new HashMap<>();
               map.put("userName",currentUser);
               GetDataFromServer dataFromServer = new GetDataFromServer(this,taskInterface,getResources().getString(R.string.groupInfo),map);
               dataFromServer.sendJsonRequest();
               /*try {
                    POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8");

                    backgroundTask = new DatabaseBackgroundTask(MyGroupInfo.this);
                    backgroundTask.setOnResultListener(onAsyncTaskInterface);
                    backgroundTask.execute(getResources().getString(R.string.groupInfo),POST_DATA);

               }catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
               }*/
          }else dialogClass.noInternetConnection();
     }

     //process json data to string
     @SuppressLint("SetTextI18n")
     private void groupInformation(String userInfo)
     {
          try {
               JSONObject jsonObject = new JSONObject(userInfo);
               JSONArray jsonArray = jsonObject.optJSONArray("groupInfo");

               int count = 0;
               while(count< jsonArray.length())
               {
                    JSONObject jObject = jsonArray.getJSONObject(count);
                    name = jObject.getString("gName");
                    id = jObject.getString("groupId");
                    description = jObject.getString("gDescription");
                    type = jObject.getString("gType");
                    address = jObject.getString("gAddress");
                    time = jObject.getString("gTime");
                    date = jObject.getString("gDate");
                    member = jObject.getString("gMem");
                    admin = jObject.getString("gAdmin");
                    count++;
               }

               gName.setText(name);
               groupId.setText(id);
               gDescription.setText(description);
               gType.setText(type);
               gTime.setText(time);
               gDate.setText("Create at "+date);
               gMember.setText(member);
               gAdmin.setText(admin);
               gAddress.setText(address);

          } catch (JSONException e) {
               e.printStackTrace();
          }
     }

     //edit your group type
     private void editGroupType()
     {
          CharSequence[] values = {"Public group","Close group","Secret group"};
          AlertDialog.Builder builder = new AlertDialog.Builder(this);

          builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

               @SuppressLint("SetTextI18n")
               public void onClick(DialogInterface dialog, int item) {

                    switch(item)
                    {
                         case 0:
                              gType.setText("public");
                              break;
                         case 1:
                              gType.setText("close");
                              break;
                         case 2:
                              gType.setText("secret");
                              break;
                    }
                    alertDialog.dismiss();
               }
          });
          alertDialog = builder.create();
          alertDialog.show();

     }

     @SuppressLint("SetTextI18n")
     @Override
     public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
          int hourFinal = hourOfDay;


          String format;
          String sHour;
          if(hourFinal >12)
          {
               hourFinal -=12;
               format = " PM";
          }else if(hourFinal ==0)
          {
               hourFinal = 12;
               format = " AM";
          }else if(hourFinal ==12)
               format = " PM";
          else
               format = " AM";

          sHour = String.valueOf(hourFinal);

          if(hourFinal <10)
               sHour = "0"+String.valueOf(hourFinal);

          if(minute<10)
               gTime.setText(sHour+" : 0"+String.valueOf(minute)+format);
          else
               gTime.setText(sHour+" : "+String.valueOf(minute)+format);
     }


     OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
          @Override
          public void onResultSuccess(final String result) {
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         switch (result)
                         {
                              case "success"://update info success
                                   someMethod.progress("Updating information...","Group information updated successfully.");
                                   initializeGroupInfo();//initialize new information about group
                                   bEdit.setEnabled(false);//disable button
                                   bEdit.setBackgroundDrawable(getDrawable(R.drawable.disable_button));//change disable button background
                                   //disable all component
                                   gName.setEnabled(false);
                                   gAddress.setEnabled(false);
                                   gDescription.setEnabled(false);
                                   gName.setFocusable(false);
                                   gAddress.setFocusable(false);
                                   gDescription.setFocusable(false);
                                   break;
                              default:
                                   dialogClass.error("Group information update failed.Please retry after sometimes");
                                   bEdit.setEnabled(false);//disable button
                                   bEdit.setBackgroundDrawable(getDrawable(R.drawable.disable_button));//change background for disable button
                                   break;
                         }
                    }
               });
          }
     };

     //get json data from server
     OnAsyncTaskInterface taskInterface = new OnAsyncTaskInterface() {
          @Override
          public void onResultSuccess(final String message) {
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         if(message!=null)
                              groupInformation(message);//send json data for processing
                    }
               });
          }
     };
}
