package com.atik_faysal.mealcounter;

import android.annotation.SuppressLint;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.backend.ApiClient;
import com.atik_faysal.backend.GetDataFromServer;
import com.atik_faysal.backend.PostData;
import com.atik_faysal.interfaces.ApiClientInterface;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnUploadImageResult;
import com.atik_faysal.model.ImageModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by USER on 1/28/2018.
 */

public class EditYourProfile extends AppCompatActivity
{

     //class object declaration
     private AlertDialogClass dialogClass;
     private CheckInternetIsOn internetIsOn;
     private SharedPreferenceData sharedPreferenceData;
     private NeedSomeMethod someMethod;

     private static final int PICK_IMAGE_REQUEST = 1;
     private Bitmap bitmap;
     private final static long IMAGE_SIZE = 1600;//in kb
     private AlertDialog alertDialog;

     private Button bEdit;
     private TextView txtTaka,txtGroup,txtUserName,txtDate,txtUploadPhoto,ePhone;
     private EditText eName,eEmail,eAddress,eFaWord;
     public static CircleImageView imageView;

     private String name,userName,phone,email,address,fWord,taka,group,date;
     private String currentUser;

     @Override
     protected void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.profile);
          initComponent();
     }

     //initialize all user information related variable by getText from textView or editText
     private void initComponent()
     {
          //initialize all component
          bEdit = findViewById(R.id.bEdit);
          eName = findViewById(R.id.txtName);
          eEmail = findViewById(R.id.txtEmail);
          eAddress = findViewById(R.id.gAddress);
          eFaWord = findViewById(R.id.fWord);
          ePhone = findViewById(R.id.txtPhoneNumber);
          txtTaka = findViewById(R.id.txtTaka);
          txtUserName = findViewById(R.id.txtUserName);
          txtGroup = findViewById(R.id.txtGroup);
          txtDate = findViewById(R.id.gDate);
          imageView = findViewById(R.id.image);
          txtUploadPhoto = findViewById(R.id.txtUploadPhoto);
          imageView.setEnabled(false);
          txtUploadPhoto.setEnabled(false);

          //disable edittext
          eName.setEnabled(false);
          eEmail.setEnabled(false);
          eFaWord.setEnabled(false);
          eAddress.setEnabled(false);
          bEdit.setEnabled(false);
          bEdit.setBackgroundDrawable(getDrawable(R.drawable.disable_button));

          SwipeRefreshLayout refreshLayout = findViewById(R.id.layout1);
          refreshLayout.setColorSchemeResources(R.color.color2,R.color.red,R.color.color6);

          sharedPreferenceData = new SharedPreferenceData(this);
          internetIsOn = new CheckInternetIsOn(this);
          dialogClass = new AlertDialogClass(this);
          someMethod = new NeedSomeMethod(this);

          //calling method
          someMethod.reloadPage(refreshLayout,EditYourProfile.class);
          //setToolbar();
          onButtonClick();

          currentUser = sharedPreferenceData.getCurrentUserName();
          if(sharedPreferenceData.myImageIsSave())
               imageView.setImageBitmap(sharedPreferenceData.getMyImage());

          if(internetIsOn.isOnline())
          {
               Map<String,String>map = new HashMap<>();
               map.put("userName",currentUser);
               GetDataFromServer dataFromServer = new GetDataFromServer(this,taskInterface,getResources().getString(R.string.getMemberInfo),map);
               dataFromServer.sendJsonRequest();
                       /*try {
                               POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8");
                               databaseBackgroundTask = new DatabaseBackgroundTask(this);
                               databaseBackgroundTask.setOnResultListener(taskInterface);
                               databaseBackgroundTask.execute(getResources().getString(R.string.getMemberInfo),POST_DATA);
                       } catch (UnsupportedEncodingException e) {
                               e.printStackTrace();
                       }*/
          }else dialogClass.noInternetConnection();

     }

     //onText change listener,action will be change on text change
     private void onTextChangeListener()
     {
          final Drawable icon = getResources().getDrawable(R.drawable.icon_done);
          icon.setBounds(0,0,icon.getIntrinsicWidth(),icon.getIntrinsicHeight());

          eName.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
               @Override
               public void onTextChanged(CharSequence s, int start, int before, int count) {}

               @Override
               public void afterTextChanged(Editable s) {
                    boolean flag = true;
                    if(eName.getText().toString().length()<3||eName.getText().toString().length()>20)
                         eName.setError("Invalid");
                    else
                    {
                         for(int i=0;i<eName.length();i++)
                         {
                              if(((eName.getText().toString().charAt(i)>='a')&&(eName.getText().toString().charAt(i)<='z'))||
                                   ((eName.getText().toString().charAt(i)>='A')&&(eName.getText().toString().charAt(i)<='Z'))||
                                   eName.getText().toString().charAt(i)==' '||eName.getText().toString().charAt(i)==':'||eName.getText().toString().charAt(i)=='.'||eName.getText().toString().charAt(i)=='_')
                                   eName.setError("Valid",icon);
                              else flag = false;
                         }
                         if(!flag)eName.setError("Invalid");
                    }
               }
          });


          eEmail.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

               @Override
               public void onTextChanged(CharSequence s, int start, int before, int count) {}

               @Override
               public void afterTextChanged(Editable s) {
                    boolean flag = true;
                    if(!eEmail.getText().toString().contains("@"))
                         flag = false;
                    else
                    {
                         String[] email = eEmail.getText().toString().split("@");
                         if(email[0].length()<3||email[0].length()>30)
                              flag = false;
                    }

                    if(flag)
                         eEmail.setError("Valid",icon);
                    else
                         eEmail.setError("invalid");
               }
          });


          eAddress.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

               @Override
               public void onTextChanged(CharSequence s, int start, int before, int count) {}

               @Override
               public void afterTextChanged(Editable s) {
                    if(eAddress.getText().toString().length()<10||eAddress.getText().toString().length()>30)
                         eAddress.setError("invalid");
                    else eAddress.setError("Valid",icon);
               }
          });


          eFaWord.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

               @Override
               public void onTextChanged(CharSequence s, int start, int before, int count) {}

               @Override
               public void afterTextChanged(Editable s) {
                    if(eFaWord.getText().toString().length()<4||eFaWord.getText().toString().length()>15)
                         eFaWord.setError("invalid");
                    else
                         eFaWord.setError("Valid",icon);
               }
          });
     }

     private void onButtonClick()
     {
          ImageView imgBack,imgEdit;
          imgBack = findViewById(R.id.imgBack);
          imgEdit = findViewById(R.id.imgEdit);

          imgBack.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    finish();
               }
          });

          imgEdit.setOnClickListener(new View.OnClickListener() {
               @SuppressLint("SetTextI18n")
               @Override
               public void onClick(View view) {

                    eName.setEnabled(true);
                    eEmail.setEnabled(true);
                    eFaWord.setEnabled(true);
                    eAddress.setEnabled(true);
                    eName.setFocusableInTouchMode(true);
                    eEmail.setFocusableInTouchMode(true);
                    eFaWord.setFocusableInTouchMode(true);
                    eAddress.setFocusableInTouchMode(true);
                    bEdit.setEnabled(true);
                    bEdit.setBackgroundDrawable(getDrawable(R.drawable.button1));
                    txtUploadPhoto.setEnabled(true);
                    txtUploadPhoto.setText("Choose photo");
                    onTextChangeListener();

                    Drawable image = EditYourProfile.this.getResources().getDrawable(R.drawable.icon_photo);
                    image.setBounds(0,0,30,30);
                    txtUploadPhoto.setCompoundDrawables(image,null,null,null);
                    imageView.setEnabled(true);
               }
          });


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
                              Map<String,String> map = new HashMap<>();
                              map.put("userName",currentUser);
                              map.put("name",getName);
                              map.put("email",getEmail);
                              map.put("address",getAddress);
                              map.put("fWord",getFword);
                              PostData postData = new PostData(EditYourProfile.this,onAsyncTaskInterface);
                              postData.InsertData(getResources().getString(R.string.editProfile),map);
                                                /*try
                                                {
                                                        POST_DATA1 = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8")+"&"
                                                                +URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(getName,"UTF-8")+"&"
                                                                +URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(getEmail,"UTF-8")+"&"
                                                                +URLEncoder.encode("address","UTF-8")+"="+URLEncoder.encode(getAddress,"UTF-8")+"&"
                                                                +URLEncoder.encode("fWord","UTF-8")+"="+URLEncoder.encode(getFword,"UTF-8");
                                                        //POST_DATA1 = URLEncoder.encode("image","UTF-8")+"="+URLEncoder.encode(convertImageToString(bitmap),"UTF-8");
                                                } catch (UnsupportedEncodingException e) {
                                                        e.printStackTrace();
                                                }
                                                DatabaseBackgroundTask checkBackgroundTask = new DatabaseBackgroundTask(EditYourProfile.this);
                                                checkBackgroundTask.setOnResultListener(onAsyncTaskInterface);
                                                checkBackgroundTask.execute(getResources().getString(R.string.editProfile),POST_DATA1);*/

                         }
                    }else dialogClass.noInternetConnection();
               }
          });

          txtUploadPhoto.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view)
               {
                    openGallery();
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
     @SuppressLint("SetTextI18n")
     private void processJsonData(String jsonData)
     {
          try {
               JSONObject jsonObject = new JSONObject(jsonData);
               JSONArray jsonArray = jsonObject.optJSONArray("information");

               int count = 0;
               while(count< jsonArray.length())
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

          eName.setText(name);
          txtUserName.setText(userName);
          ePhone.setText(phone);
          eAddress.setText(address);
          eFaWord.setText(fWord);
          txtDate.setText("Join "+date);
          eEmail.setText(email);
          txtTaka.setText(taka);
          txtGroup.setText(group);
     }

     //open phone gallery and choose a photo
     public void openGallery()
     {
          Intent intent = new Intent();
          intent.setType("image/*");
          intent.setAction(Intent.ACTION_GET_CONTENT);
          startActivityForResult(intent, PICK_IMAGE_REQUEST);
     }

     //convert bitmap to string
     public String convertImageToString(Bitmap bmp){
          ByteArrayOutputStream stream = new ByteArrayOutputStream();
          bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
          byte[] imageBytes = stream.toByteArray();
          return Base64.encodeToString(imageBytes, Base64.DEFAULT);
     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
          super.onActivityResult(requestCode, resultCode, data);

          if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
               Uri imageUri = data.getData();
               try {
                    File file = new File(getRealPathFromURI(imageUri));
                    long length = file.length()/1024;//in kb

                    if(length>IMAGE_SIZE)
                         dialogClass.error("Large image size,please select image less than 1.5 MB");
                    else
                    {
                         bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                         uploadImage(bitmap);
                    }
               } catch (IOException e) {
                    e.printStackTrace();
               }
          }
     }

     //get image real path from image uri
     private String getRealPathFromURI(Uri contentUri) {
          String[] imageData = { MediaStore.Images.Media.DATA };
          CursorLoader loader = new CursorLoader(this, contentUri, imageData, null, null, null);
          Cursor cursor = loader.loadInBackground();
          int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
          cursor.moveToFirst();
          String result = cursor.getString(column_index);
          cursor.close();
          return result;
     }

     //image upload to server
     private void uploadImage(final Bitmap imageBit)
     {
          AlertDialog.Builder builder = new AlertDialog.Builder(this);
          View view = LayoutInflater.from(this).inflate(R.layout.image_viewer,null);
          builder.setView(view);
          builder.setCancelable(false);

          ImageView uImage = view.findViewById(R.id.uImage);
          Button bCancel,bUpload;

          bCancel = view.findViewById(R.id.cancel);
          bUpload = view.findViewById(R.id.bUpload);


          if(bitmap!=null)
               uImage.setImageBitmap(imageBit);

          alertDialog = builder.create();
          alertDialog.show();

          bCancel.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    alertDialog.dismiss();
               }
          });

          bUpload.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    new ImageUploadBackgroundTask().execute(imageBit);
                    alertDialog.dismiss();
               }
          });

     }

     //background class upload image to server
     @SuppressLint("StaticFieldLeak")
     public class ImageUploadBackgroundTask extends AsyncTask<Bitmap,Void,Void>
     {

          private ProgressBar progressBar;
          private TextView textView;
          private int progressStatus;
          private Handler handler = new Handler();
          private android.support.v7.app.AlertDialog alertDialog;

          @Override
          protected Void doInBackground(Bitmap... bitmaps)
          {
               String image = null;
               String userName = sharedPreferenceData.getCurrentUserName();
               if(bitmaps[0]!=null)
                    image = convertImageToString(bitmaps[0]);

               ApiClientInterface apiInterface = ApiClient.getApiClient().create(ApiClientInterface.class);
               Call<ImageModel> call = apiInterface.uploadImage(userName, image, someMethod.getDate(),sharedPreferenceData.getMyImageName());

               call.enqueue(new Callback<ImageModel>() {
                    @Override
                    public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                         ImageModel imageModel = response.body();
                         if(!imageModel.getResponse().equals("success"))
                              alertDialog.dismiss();
                         uploadImageResult.onSuccess(imageModel.getResponse(),bitmap);
                    }

                    @Override
                    public void onFailure(Call<ImageModel> call, Throwable t) {
                         alertDialog.dismiss();
                         dialogClass.error("Execution failed,Please try again.");
                    }
               });
               return null;
          }

          @Override
          protected void onPreExecute() {
               super.onPreExecute();
               android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(EditYourProfile.this);
               @SuppressLint("InflateParams") View view = LayoutInflater.from(EditYourProfile.this).inflate(R.layout.progress_bar,null);
               textView = view.findViewById(R.id.text);
               progressBar = view.findViewById(R.id.bar);
               progressBar.setMax(100);
               builder.setTitle("Please Wait");
               builder.setView(view);
               builder.setCancelable(false);
               progressStatus = 0;
               alertDialog = builder.create();
               alertDialog.show();
               new Thread(new Runnable() {
                    @Override
                    public void run() {
                         while (progressStatus<=100)
                         {
                              if(progressStatus==100)
                              {
                                   runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                             Toast.makeText(EditYourProfile.this,"Image upload successfully",Toast.LENGTH_LONG).show();
                                        }
                                   });
                              }
                              handler.post(new Runnable() {
                                   @SuppressLint("SetTextI18n")
                                   @Override
                                   public void run() {
                                        progressBar.setProgress(progressStatus);
                                        textView.setText("Uploading : "+progressStatus+"% / 100%");
                                   }
                              });
                              try {
                                   Thread.sleep(200);
                              }catch (InterruptedException e)
                              {
                                   e.printStackTrace();
                              }
                              progressStatus++;
                         }
                         alertDialog.dismiss();
                    }
               }).start();

          }
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
                              case "updated":
                                   someMethod.progress("Updating your information...","Your information is updated.");
                                   eName.setEnabled(false);
                                   eEmail.setEnabled(false);
                                   eFaWord.setEnabled(false);
                                   eAddress.setEnabled(false);
                                   eName.setFocusable(false);
                                   eEmail.setFocusable(false);
                                   eFaWord.setFocusable(false);
                                   eAddress.setFocusable(false);
                                   bEdit.setEnabled(false);
                                   bEdit.setBackgroundDrawable(getDrawable(R.drawable.disable_button));
                                   break;
                              default:
                                   dialogClass.error("Execution failed.Please try again.");
                                   break;
                         }
                    }
               });
          }
     };

     OnAsyncTaskInterface taskInterface = new OnAsyncTaskInterface() {
          @Override
          public void onResultSuccess(final String message) {
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         if(message!=null)
                              processJsonData(message);
                    }
               });
          }
     };

     OnUploadImageResult uploadImageResult = new OnUploadImageResult() {
          @Override
          public void onSuccess(final String response, final Bitmap bitmap) {
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         if(response.equals("success"))
                         {
                              imageView.setImageBitmap(bitmap);
                              sharedPreferenceData.myImage(bitmap,true);
                         }else dialogClass.error("Execution failed.Please retry");
                    }
               });
          }
     };
}
