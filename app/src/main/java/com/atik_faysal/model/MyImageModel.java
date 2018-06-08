package com.atik_faysal.model;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import android.annotation.SuppressLint;
import android.content.Context;

public class MyImageModel
{
     private RequestQueue requestQueue;
     private Context context;
     @SuppressLint("StaticFieldLeak")
     private static MyImageModel myImageModel;

     public MyImageModel(Context context)
     {
          this.context = context;
          requestQueue = getRequestQueue();
     }

     public RequestQueue getRequestQueue()
     {
          if(requestQueue==null)
               requestQueue = Volley.newRequestQueue(context.getApplicationContext());
          return requestQueue;
     }

     public static synchronized MyImageModel getInstances(Context context)
     {
          if(myImageModel==null)
               myImageModel = new MyImageModel(context);
          return myImageModel;
     }

     public void addToRequestQue(Request<?> request)
     {
          requestQueue.add(request);
     }
}
