package com.atik_faysal.model;

import com.google.gson.annotations.SerializedName;

public class ImageModel
{
     @SerializedName("userName")
     private String userName;

     @SerializedName("image")
     private String image;

     @SerializedName("date")
     private String date;

     @SerializedName("preName")
     private String preName;

     @SerializedName("response")
     private String response;

     public String getResponse()
     {
          return response;
     }
}
