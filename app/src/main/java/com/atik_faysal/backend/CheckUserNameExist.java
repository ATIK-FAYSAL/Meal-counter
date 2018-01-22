package com.atik_faysal.backend;

import android.content.Intent;
import android.os.AsyncTask;
import android.content.Context;
import android.widget.Toast;

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

/**
 * Created by USER on 1/22/2018.
 */

public class CheckUserNameExist extends AsyncTask<String,String,String>
{
        private Context context;

        private String name,userName,email,password,address;

        public CheckUserNameExist(Context context,String name,String userName,String address,String email,String password)
        {
                this.context = context;
                this.name = name;
                this.userName = userName;
                this.email = email;
                this.password = password;
                this.address = address;
        }

        @Override
        protected String doInBackground(String... params)
        {
                String userName = params[0];

                String checkUrl = "http://192.168.56.1/userNameExist.php";

                URL url;
                HttpURLConnection httpURLConnection;
                OutputStreamWriter outputStreamWriter;
                OutputStream outputStream;
                BufferedWriter bufferedWriter;
                InputStream inputStream;
                BufferedReader bufferedReader;

                StringBuilder result = new StringBuilder();

                try {
                        url = new URL(checkUrl);
                        httpURLConnection = (HttpURLConnection)url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.getOutputStream();
                        outputStream = httpURLConnection.getOutputStream();
                        outputStreamWriter = new OutputStreamWriter(outputStream,"UTF-8");
                        bufferedWriter = new BufferedWriter(outputStreamWriter);
                        String checkData = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8");
                        bufferedWriter.write(checkData);
                        bufferedWriter.flush();
                        bufferedWriter.close();
                        outputStream.close();

                        inputStream = httpURLConnection.getInputStream();
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                        String line;

                        while((line = bufferedReader.readLine())!=null)
                                result.append(line);


                        bufferedReader.close();
                        inputStream.close();
                        httpURLConnection.disconnect();

                        return result.toString();
                } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                } catch (ProtocolException e) {
                        e.printStackTrace();
                } catch (MalformedURLException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }

                return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if(!result.equals("not exist"))Toast.makeText(context,result,Toast.LENGTH_SHORT).show();
                else
                {
                        Intent intent = new Intent(context,PhoneNumberVerification.class);
                        intent.putExtra("name",name);
                        intent.putExtra("userName",userName);
                        intent.putExtra("email",email);
                        intent.putExtra("address",address);
                        intent.putExtra("password",password);
                        context.startActivity(intent);
                }
        }

}
