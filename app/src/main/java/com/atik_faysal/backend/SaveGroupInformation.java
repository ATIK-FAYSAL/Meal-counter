package com.atik_faysal.backend;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
 * Created by USER on 1/22/2018.
 */

public class SaveGroupInformation extends AsyncTask<String,Void,String>
{

        String groupName,groupId,groupDescription,groupAddress,groupAdmin,date;
        Context context;
        StringBuilder result ;
        ProgressDialog ringProgressDialog;

        Activity activity;

        public SaveGroupInformation(Context context)
        {
                this.context = context;
                activity = (Activity)context;
        }

        @Override
        protected void onPreExecute() {
                super.onPreExecute();
                ringProgressDialog = ProgressDialog.show(activity, "Please wait", "Saving group information.....", true);
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
        protected String doInBackground(String... params) {

                groupName = params[0];groupId = params[1];groupAddress = params[2];groupDescription=params[3];date = params[4];

                result = new StringBuilder();
                String groupUrl = "http://192.168.56.1/group_information.php";
                HttpURLConnection httpURLConnection;
                URL url;
                OutputStream outputStream;
                OutputStreamWriter outputStreamWriter;
                BufferedReader bufferedReader;
                BufferedWriter bufferedWriter;
                InputStream inputStream;

                try {
                        url = new URL(groupUrl);
                        HttpURLConnection httpsURLConnection = (HttpURLConnection)url.openConnection();
                        httpsURLConnection.setRequestMethod("POST");
                        httpsURLConnection.setDoOutput(true);
                        httpsURLConnection.setDoInput(true);
                        outputStream = httpsURLConnection.getOutputStream();
                        outputStreamWriter = new OutputStreamWriter(outputStream,"UTF-8");
                        bufferedWriter = new BufferedWriter(outputStreamWriter);
                        String postInfo = URLEncoder.encode("groupName","UTF-8")+"="+URLEncoder.encode(groupName,"UTF-8")+"&"
                                +URLEncoder.encode("groupId","UTF-8")+"="+URLEncoder.encode(groupId,"UTF-8")+"&"
                                +URLEncoder.encode("groupAddress","UTF-8")+"="+URLEncoder.encode(groupAddress,"UTF-8")+"&"
                                +URLEncoder.encode("groupDescription","UTF-8")+"="+URLEncoder.encode(groupDescription,"UTF-8")+"&"
                                +URLEncoder.encode("groupAdmin","UTF-8")+"="+URLEncoder.encode("atik faysal","UTF-8")+"&"
                                +URLEncoder.encode("date","UTF-8")+"="+URLEncoder.encode(date,"UTF-8");

                        bufferedWriter.write(postInfo);
                        bufferedWriter.flush();
                        bufferedWriter.close();
                        outputStream.close();
                        inputStream = httpsURLConnection.getInputStream();
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

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

                return null;
        }


        @Override
        protected void onPostExecute(final String result) {
                super.onPostExecute(result);
                if(result.equals("Information saving complete"))
                {
                        ringProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                        Toast.makeText(context,result,Toast.LENGTH_SHORT).show();
                                }
                        });
                }else Toast.makeText(context,result,Toast.LENGTH_LONG).show();
        }

}
