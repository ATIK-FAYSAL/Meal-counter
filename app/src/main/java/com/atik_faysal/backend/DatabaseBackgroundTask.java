package com.atik_faysal.backend;

import android.os.AsyncTask;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.CheckInternetIsOn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import static android.content.ContentValues.TAG;

/**
 * Created by USER on 1/25/2018.
 */

public class DatabaseBackgroundTask extends AsyncTask<String,Void,String>
{

        Context context;

        private OnAsyncTaskInterface onAsyncTaskInterface;
        private CheckInternetIsOn internetIsOn;

        //constructor
        public DatabaseBackgroundTask(Context context)
        {
                this.context = context;
                internetIsOn = new CheckInternetIsOn(context);
        }

        //success result
        public void setOnResultListener(OnAsyncTaskInterface onAsyncResult) {
                if (onAsyncResult != null) {
                        this.onAsyncTaskInterface = onAsyncResult;
                }
        }

        //background method .data inserting to database.
        @Override
        public String doInBackground(String... params) {

                String result = "";
                String fileName = params[0];
                String postData = params[1];


                if(internetIsOn.isOnline())
                {
                        try {
                                URL url = new URL(fileName);
                                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                httpURLConnection.setConnectTimeout(1000*20);
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.setDoOutput(true);
                                httpURLConnection.setDoInput(true);
                                OutputStream outputStream = httpURLConnection.getOutputStream();
                                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

                                bufferedWriter.write(postData);
                                bufferedWriter.flush();
                                bufferedWriter.close();
                                outputStream.close();
                                InputStream inputStream = httpURLConnection.getInputStream();
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                                String line;

                                while ((line= bufferedReader.readLine())!=null)
                                        result = line;

                                bufferedReader.close();
                                inputStream.close();
                                httpURLConnection.disconnect();
                                onAsyncTaskInterface.onResultSuccess(result);

                        } catch (MalformedURLException e) {
                                e.printStackTrace();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }catch (Exception e)
                        {
                                e.printStackTrace();
                        }
                        return result.toString();
                }else return "offline";
        }


        //interface,
       /* public interface OnAsyncTaskInterface {
                void onResultSuccess(String message);
        }*/


}
