package com.atik_faysal.backend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownLoadImageTask extends AsyncTask<String,Void,Bitmap>
{
        private SharedPreferenceData sharedPreferenceData;

        public DownLoadImageTask(Context context)
        {
                sharedPreferenceData = new SharedPreferenceData(context);
        }

        @Override
        protected Bitmap doInBackground(String... voids)
        {
                String userName = voids[0];
                String imageUrl = "http://mealcounter.bdtechnosoft.com/"+userName+".png";
                try {
                        URLConnection connection = new URL(imageUrl).openConnection();
                        connection.setConnectTimeout(1000*20);
                        connection.setReadTimeout(1000*20);

                        return BitmapFactory.decodeStream((InputStream)connection.getContent(),null,null);
                } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if(bitmap!=null)
                        sharedPreferenceData.myImage(bitmap,true);
        }
}
