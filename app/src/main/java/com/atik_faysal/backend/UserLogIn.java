package com.atik_faysal.backend;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;


import android.content.Context;
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

/**
 * Created by USER on 1/21/2018.
 */

public class UserLogIn extends AsyncTask<String,Void,String>
{
        private Context context;
         ProgressDialog ringProgressDialog;

        public UserLogIn(Context context)
        {
                this.context = context;
        }

        @Override
        protected void onPreExecute() {
                super.onPreExecute();
                ringProgressDialog = ProgressDialog.show(context, "Please wait", "Authenticating...", true);
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

        }

        @Override
        protected String doInBackground(String... params)
        {
                String insertMemberUrl = "http://192.168.56.1/user_log_in.php";

                StringBuilder result = new StringBuilder();
                String userName = params[1];
                String password = params[2];

                if(params[0].equals("login"))
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
                                String postInfo = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8")+"&"
                                        +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");

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
                super.onPostExecute(result);
                if(result.equals("log in success"))
                {
                        ringProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                        closeActivity((Activity)context,HomePageActivity.class);
                                }
                        });
                }else Toast.makeText(context,result,Toast.LENGTH_SHORT).show();
        }

        private static void closeActivity(Activity context, Class<?> clazz) {
                Intent intent = new Intent(context, clazz);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                context.finish();
        }
}
