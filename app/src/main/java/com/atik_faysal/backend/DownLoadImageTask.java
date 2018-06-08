package com.atik_faysal.backend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.atik_faysal.interfaces.ImageSuccessResult;
import com.atik_faysal.interfaces.OnUploadImageResult;
import com.atik_faysal.model.MyImageModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownLoadImageTask extends AsyncTask<Void,Void,Void>
{

        private SharedPreferenceData sharedPreferenceData;
        private OnUploadImageResult successResult;
        @SuppressLint("StaticFieldLeak")
        private Context context;
        public DownLoadImageTask(Context context)
        {
                sharedPreferenceData = new SharedPreferenceData(context);
                this.context = context;
        }

        public void setOnResultListener(OnUploadImageResult resultListener)
        {
                this.successResult = resultListener;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
                String name = sharedPreferenceData.getMyImageName();
                Log.d("IMAGE UNIQUE ID ",name);
                String url = "http://mealcounter.bdtechnosoft.com/images/"+name+".png";
                ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                                successResult.onSuccess("success",response);
                        }
                }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                                Log.d("IMAGE ERROR ",error.toString());
                                error.printStackTrace();
                        }
                });
                MyImageModel.getInstances(context).addToRequestQue(imageRequest);
                return null;
        }

}
