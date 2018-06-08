package com.atik_faysal.backend;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient
{
     private static final String IMAGE_URL = "http://mealcounter.bdtechnosoft.com/images/";
     private static Retrofit retrofit;

     public static Retrofit getApiClient()
     {
          if(retrofit==null)
          {
               retrofit = new Retrofit.Builder().baseUrl(IMAGE_URL).
                    addConverterFactory(GsonConverterFactory.create()).build();
          }
          return retrofit;
     }
}
