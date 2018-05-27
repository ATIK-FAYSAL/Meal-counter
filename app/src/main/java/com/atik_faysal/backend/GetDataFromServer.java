package com.atik_faysal.backend;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;

import java.util.Map;

public class GetDataFromServer
{
     private OnAsyncTaskInterface onAsyncTaskInterface;
     private String fileName;
     private RequestQueue requestQueue;
     private Map<String,String> dataMap;

     public GetDataFromServer(Context context, OnAsyncTaskInterface onAsyncTaskInterface, String file, final Map<String,String>infoMap)
     {
          this.fileName = file;
          this.onAsyncTaskInterface = onAsyncTaskInterface;
          this.dataMap = infoMap;
          requestQueue = Volley.newRequestQueue(context);
     }

     public void sendJsonRequest()
     {
          StringRequest stringRequest = new StringRequest(Request.Method.POST,fileName, new Response.Listener<String>() {
               @Override
               public void onResponse(String response) {
                    onAsyncTaskInterface.onResultSuccess(response.trim());
               }
          }, new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {
                    onAsyncTaskInterface.onResultSuccess("error");
               }
          }){
               @Override
               protected Map<String, String> getParams(){
                    Map<String,String> map;
                    map = dataMap;
                    return map;
               }
          };

          requestQueue.add(stringRequest);
     }
}
