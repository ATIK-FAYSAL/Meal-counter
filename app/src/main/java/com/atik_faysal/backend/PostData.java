package com.atik_faysal.backend;

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
                         public void onErrorResponse(VolleyError volleyError) {}
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
