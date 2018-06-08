package com.atik_faysal.interfaces;

import com.atik_faysal.model.ImageModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiClientInterface
{
     @FormUrlEncoded
     @POST("http://mealcounter.bdtechnosoft.com/imageUpload.php")
     Call<ImageModel> uploadImage(@Field("userName")String userName,
                                  @Field("image")String image,
                                  @Field("date")String date,
                                  @Field("preName")String preName);
}
