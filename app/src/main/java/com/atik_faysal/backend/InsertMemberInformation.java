package com.atik_faysal.backend;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;


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


import static android.content.ContentValues.TAG;
/**
 * Created by USER on 1/20/2018.
 */

public class InsertMemberInformation extends AsyncTask<String,Void,String>
{

        private Context context;
        private String name,phone,userName,address,email,password;
        String memberType = "member";
        String taka = "0",groupId = "null";
        AlertDialog alertDialog;

        public InsertMemberInformation(Context context)
        {
                this.context = context;
        }


        @Override
        protected void onPreExecute() {
                super.onPreExecute();
                final ProgressDialog ringProgressDialog = ProgressDialog.show(context, "Please wait", "Saving your Information...", true);
                ringProgressDialog.setCancelable(true);
                new Thread(new Runnable() {
                        @Override
                        public void run() {
                                try {
                                        Thread.sleep(2500);
                                } catch (Exception e) {
                                }
                                ringProgressDialog.dismiss();
                        }
                }).start();
                ringProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                                closeActivity((Activity) context,HomePageActivity.class);
                        }
                });
        }

        @Override
        protected String doInBackground(String... params)
        {
                String insertMemberUrl = "http://192.168.56.1/insert_member_info.php";


                StringBuilder result= new StringBuilder();

                name = params[1];userName = params[2];email = params[3];phone = params[4];address = params[5];password = params[6];

                if(params[0].equals("insertMember"))
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
                                        +URLEncoder.encode("groupId","UTF-8")+"="+URLEncoder.encode(groupId,"UTF-8");


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

                }

                return null;
        }


        @Override
        protected void onPostExecute(String result) {
                Toast.makeText(context,result,Toast.LENGTH_SHORT).show();
        }


        private static void closeActivity(Activity context, Class<?> clazz) {
                Intent intent = new Intent(context, clazz);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                context.finish();
        }
}
