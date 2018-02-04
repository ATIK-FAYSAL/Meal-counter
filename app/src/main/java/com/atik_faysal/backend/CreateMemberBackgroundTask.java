package com.atik_faysal.backend;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.content.Context;
import android.widget.Toast;


import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.HomePageActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by USER on 1/20/2018.
 */

public class CreateMemberBackgroundTask extends AsyncTask<String,Void,String>
{

        private CheckInternetIsOn internetIsOn;

        private final static String USER_LOGIN = "userLogIn";
        private final static String USER_INFO = "currentInfo";

        private Context context;
        private String name,phone,userName,address,email,password,date,favouriteWord;
        String memberType = "member";
        String taka = "0",groupId = "Null";

        ProgressDialog progressDialog;

        Activity activity;
        public CreateMemberBackgroundTask(Context context)
        {
                this.context = context;
                activity = (Activity)context;
                internetIsOn = new CheckInternetIsOn(context);
        }


        @Override
        protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(context, "Please wait", "Saving your information...", true);
                progressDialog.setCancelable(true);
                new Thread( new Runnable() {
                        @Override
                        public void run() {
                                try {
                                        Thread.sleep(2000);
                                }catch(Exception e){
                                }
                                progressDialog.dismiss();
                        }
                }).start();
        }

        @Override
        protected String doInBackground(String... params)
        {
                String insertMemberUrl = "http://192.168.56.1/insert_member_info.php";

                StringBuilder result= new StringBuilder();

                name = params[1];userName = params[2];email = params[3];phone = params[4];address = params[5];password = params[6];
                date = params[7];favouriteWord = params[8];

                if(params[0].equals("insertMember"))
                {
                        if(internetIsOn.isOnline())
                        {
                                try {
                                        URL url = new URL(insertMemberUrl);
                                        HttpURLConnection httpsURLConnection = (HttpURLConnection)url.openConnection();
                                        httpsURLConnection.setRequestMethod("POST");
                                        httpsURLConnection.setDoOutput(true);
                                        httpsURLConnection.setDoInput(true);
                                        OutputStream outputStream = httpsURLConnection.getOutputStream();
                                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream,"UTF-8");
                                        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                                        String postInfo = URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"
                                                +URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8")+"&"
                                                +URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"
                                                +URLEncoder.encode("phone","UTF-8")+"="+URLEncoder.encode(phone,"UTF-8")+"&"
                                                +URLEncoder.encode("address","UTF-8")+"="+URLEncoder.encode(address,"UTF-8")+"&"
                                                +URLEncoder.encode("taka","UTF-8")+"="+URLEncoder.encode(taka,"UTF-8")+"&"
                                                +URLEncoder.encode("memberType","UTF-8")+"="+URLEncoder.encode(memberType,"UTF-8")+"&"
                                                +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8")+"&"
                                                +URLEncoder.encode("groupId","UTF-8")+"="+URLEncoder.encode(groupId,"UTF-8")+"&"
                                                +URLEncoder.encode("date","UTF-8")+"="+URLEncoder.encode(date,"UTF-8")+"&"
                                                +URLEncoder.encode("favouriteWord","UTF-8")+"="+URLEncoder.encode(favouriteWord,"UTF-8");


                                        bufferedWriter.write(postInfo);
                                        bufferedWriter.flush();
                                        bufferedWriter.close();
                                        outputStream.close();
                                        InputStream inputStream = httpsURLConnection.getInputStream();
                                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

                                        String line;

                                        while ((line=bufferedReader.readLine())!=null)
                                                result.append(line);

                                        bufferedReader.close();
                                        inputStream.close();
                                        httpsURLConnection.disconnect();


                                        return result.toString();
                                } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                        //Log.d(TAG,"Error is 1: "+e.toString());
                                } catch (IOException e) {
                                        e.printStackTrace();
                                        //Log.d(TAG,"Error is 2: "+e.toString());
                                }catch (Exception e)
                                {
                                        e.printStackTrace();
                                        //Log.d(TAG,"Error is 3: "+e.toString());
                                }
                        }else return "No internet connection";

                }

                return null;
        }

        @Override
        protected void onPostExecute(final String result) {
                progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                                if(result.equals("Information saving complete"))
                                {
                                        closeActivity((Activity) context,HomePageActivity.class);
                                        new SharedPreferenceData(context).ifUserLogIn(USER_LOGIN,true);
                                        new SharedPreferenceData(context).currentUserInfo(USER_INFO,userName,password);
                                        new SharedPreferenceData(context).userType("member");
                                }else Toast.makeText(context,result,Toast.LENGTH_SHORT).show();
                        }
                });
        }


        private static void closeActivity(Activity context, Class<?> clazz) {
                Intent intent = new Intent(context, clazz);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                context.finish();
        }
}
