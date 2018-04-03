package com.atik_faysal.mealcounter;

import android.app.AlertDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.backend.SharedPreferenceData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.hdodenhof.circleimageview.CircleImageView;

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

        private Button bEdit;
        private Toolbar toolbar;
        private TextView txtTaka,txtGroup,txtUserName,txtDate,txtUploadPhoto;
        private EditText eName,eEmail,eAddress,eFaWord,ePhone;
        private CircleImageView imageView;

        private Uri imageUri;
        private String name,userName,phone,email,address,fWord,taka,group,date;
        private String currentUser;
       // private final static String FILE_URL = "http://192.168.56.1/json_read_member_info.php";
        private static String POST_DATA;
        //private final static String FILE_URL1 = "http://192.168.56.1/editProfile.php";
        private static String POST_DATA1;
        private static final int PICK_IMAGE_REQUEST = 1;
        private Bitmap bitmap;
        private final static long IMAGE_SIZE = 1600;//in kb
        private AlertDialog alertDialog;

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
                imageView = findViewById(R.id.image);
                txtUploadPhoto = findViewById(R.id.txtUploadPhoto);
                imageView.setEnabled(false);
                txtUploadPhoto.setEnabled(false);


                eName = findViewById(R.id.txtName);
                eEmail = findViewById(R.id.txtEmail);
                eAddress = findViewById(R.id.gAddress);
                eFaWord = findViewById(R.id.fWord);
                ePhone = findViewById(R.id.txtPhoneNumber);

                SwipeRefreshLayout refreshLayout = findViewById(R.id.layout1);
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
                if(sharedPreferenceData.myImageIsSave())
                        imageView.setImageBitmap(sharedPreferenceData.getMyImage());

               if(internetIsOn.isOnline())
               {
                       try {
                               if(currentUser!=null)
                                       POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(currentUser,"UTF-8");
                               else Toast.makeText(this,"under construction",Toast.LENGTH_SHORT).show();

                               databaseBackgroundTask = new DatabaseBackgroundTask(this);
                               databaseBackgroundTask.setOnResultListener(onAsyncTaskInterface);
                               databaseBackgroundTask.execute(getResources().getString(R.string.getMemberInfo),POST_DATA);
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
                                                        //POST_DATA1 = URLEncoder.encode("image","UTF-8")+"="+URLEncoder.encode(convertImageToString(bitmap),"UTF-8");
                                                } catch (UnsupportedEncodingException e) {
                                                        e.printStackTrace();
                                                }
                                                DatabaseBackgroundTask checkBackgroundTask = new DatabaseBackgroundTask(EditYourProfile.this);
                                                checkBackgroundTask.setOnResultListener(onAsyncTaskInterface);
                                                checkBackgroundTask.execute(getResources().getString(R.string.editProfile),POST_DATA1);

                                        }
                                }else dialogClass.noInternetConnection();
                        }
                });

                txtUploadPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
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
                        imageUri = data.getData();
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

        //process json data to string
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
                eFaWord.setText("Favourite word : "+fWord);
                txtDate.setText("Join "+date);
                eEmail.setText(email);
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
                                txtUploadPhoto.setEnabled(true);
                                txtUploadPhoto.setText("Choose photo");

                                Drawable image = this.getResources().getDrawable(R.drawable.icon_photo);
                                image.setBounds(0,0,30,30);
                                txtUploadPhoto.setCompoundDrawables(image,null,null,null);
                                imageView.setEnabled(true);
                                break;
                }
                return true;
        }


        //image upload to server
        private void uploadImage(Bitmap imageBit)
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
                                String image = null;
                                String url = "http://192.168.56.1/imageUpload.php";
                                if(bitmap!=null)
                                        image = convertImageToString(bitmap);

                                try {
                                        String data = URLEncoder.encode("image_path","UTF-8")+"="+URLEncoder.encode(image,"UTF-8")+"&"
                                                        +URLEncoder.encode("date","UTF-8")+"="+URLEncoder.encode(someMethod.getDate(),"UTF-8")+"&"
                                                        +URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getCurrentUserName(),"UTF-8");

                                        databaseBackgroundTask = new DatabaseBackgroundTask(EditYourProfile.this);
                                        databaseBackgroundTask.setOnResultListener(anInterface);
                                        databaseBackgroundTask.execute(url,data);
                                } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                }
                        }
                });

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
                                                       dialogClass.error("Execution failed.Please try again.");
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
                                                               databaseBackgroundTask.execute(getResources().getString(R.string.getMemberInfo),POST_DATA);
                                                               Toast.makeText(EditYourProfile.this,"Information updated successfully.",Toast.LENGTH_SHORT).show();
                                                       }else dialogClass.noInternetConnection();
                                                       break;
                                               default:
                                                       processJsonData(userInfo);
                                                       break;
                                       }
                                }
                        });
                }
        };


        //check result for upload image
        OnAsyncTaskInterface anInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                        switch (message)
                                        {
                                                case "success":
                                                        imageView.setImageBitmap(bitmap);
                                                        sharedPreferenceData.myImage(bitmap,true);
                                                        someMethod.progresDialog("Image uploading...");
                                                        alertDialog.dismiss();
                                                        break;
                                                default:
                                                        dialogClass.error("Execution failed.Please try again");
                                                        break;
                                        }
                                }
                        });
                }
        };
}
