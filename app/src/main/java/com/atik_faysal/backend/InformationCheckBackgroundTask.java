package com.atik_faysal.backend;

import android.os.AsyncTask;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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

public class InformationCheckBackgroundTask extends AsyncTask<String,Void,String>
{

        Context context;

        OnAsyncTaskInterface onAsyncTaskInterface;
        CheckInternetIsOn internetIsOn;

        URL url;
        HttpURLConnection httpURLConnection;
        OutputStreamWriter outputStreamWriter;
        OutputStream outputStream;
        BufferedWriter bufferedWriter;
        InputStream inputStream;
        BufferedReader bufferedReader;



        public InformationCheckBackgroundTask(Context context)
        {
                this.context = context;
                internetIsOn = new CheckInternetIsOn(context);
        }


        public void setOnResultListener(OnAsyncTaskInterface onAsyncResult) {
                if (onAsyncResult != null) {
                        this.onAsyncTaskInterface = onAsyncResult;
                }
        }

        @Override
        public String doInBackground(String... params) {

                String result = "";
                String fileName = params[0];
                String postData = params[1];


                if(internetIsOn.isOnline())
                {
                        try {
                                url = new URL(fileName);
                                httpURLConnection = (HttpURLConnection)url.openConnection();
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.setDoOutput(true);
                                httpURLConnection.setDoInput(true);
                                outputStream = httpURLConnection.getOutputStream();
                                outputStreamWriter = new OutputStreamWriter(outputStream,"UTF-8");
                                bufferedWriter = new BufferedWriter(outputStreamWriter);

                                bufferedWriter.write(postData);
                                bufferedWriter.flush();
                                bufferedWriter.close();
                                outputStream.close();
                                inputStream = httpURLConnection.getInputStream();
                                bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

                                String line;

                                while ((line=bufferedReader.readLine())!=null)
                                        result = line;

                                bufferedReader.close();
                                inputStream.close();
                                httpURLConnection.disconnect();
                                Log.d(TAG,"result : "+result);
                                onAsyncTaskInterface.onResultSuccess(result);

                                return result.toString();
                        } catch (MalformedURLException e) {
                                e.printStackTrace();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }catch (Exception e)
                        {
                                e.printStackTrace();
                        }
                }else return "offline";


                return null;
        }


        public interface OnAsyncTaskInterface {
                void onResultSuccess(String message);
        }


}
