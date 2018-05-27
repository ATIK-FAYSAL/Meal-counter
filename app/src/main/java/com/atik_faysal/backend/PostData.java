package com.atik_faysal.backend;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.CheckInternetIsOn;

public class PostData
{
     private Context context;

     private OnAsyncTaskInterface onAsyncTaskInterface;
     private CheckInternetIsOn internetIsOn;
     private RequestQueue requestQueue;

     //constructor
     public PostData(Context context,OnAsyncTaskInterface onAsyncTaskInterface)
     {
          this.context = context;
          internetIsOn = new CheckInternetIsOn(context);
          requestQueue = Volley.newRequestQueue(context);
          this.onAsyncTaskInterface = onAsyncTaskInterface;
     }


     public void InsertData(String serverUrl, final Map<String,String>dataMap){

          if(internetIsOn.isOnline())
          {
               StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                    new Response.Listener<String>() {
                         @Override
                         public void onResponse(String ServerResponse) {
                              onAsyncTaskInterface.onResultSuccess(ServerResponse.trim());
                         }
                    },
                    new Response.ErrorListener() {
                         @Override
                         public void onErrorResponse(VolleyError volleyError) {

                         }
                    }) {
                    @Override
                    protected Map<String, String> getParams() {

                         // Creating Map String Params.
                         Map<String, String> params;
                         params = dataMap;

                         return params;
                    }
               };
               requestQueue.add(stringRequest);
          }
     }
}
