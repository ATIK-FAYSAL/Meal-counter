package com.atik_faysal.mealcounter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atik_faysal.adapter.CostAdapter;
import com.atik_faysal.backend.GetImportantData;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.model.CostModel;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestActivity extends AppCompatActivity
{
     private ListView listView;
     private RelativeLayout emptyView;
     private RequestQueue requestQueue;

     @Override
     protected void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.costs_layout);
          initComponent();
     }

     @SuppressLint("SetTextI18n")
     private void initComponent()
     {
          SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh1);
          emptyView = findViewById(R.id.empty_view);
          AdView adView = findViewById(R.id.adView);
          refreshLayout.setColorSchemeResources(R.color.color2, R.color.red, R.color.color6);
          TextView txtSession = findViewById(R.id.txtSession);

          NeedSomeMethod someMethod = new NeedSomeMethod(this);
          SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(this);
          AlertDialogClass dialogClass = new AlertDialogClass(this);
          CheckInternetIsOn internetIsOn = new CheckInternetIsOn(this);
          GetImportantData importantData = new GetImportantData(this);
          someMethod.reloadPage(refreshLayout, CostOfSecretCloseGroup.class);
          requestQueue = Volley.newRequestQueue(this);
          someMethod.setAdmob(adView);
          Map<String,String>map = new HashMap<>();
          map.put("userName",sharedPreferenceData.getCurrentUserName());

          //VollyBackedn backedn = new VollyBackedn(this,infoInterfaces,getResources().getString(R.string.shoppingCost),map);
          //backedn.sendJsonRequest();

          listView = findViewById(R.id.list);
          //sendJsonRequest();

     }



     private void sendJsonRequest()
     {
          StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.shoppingCost), new Response.Listener<String>() {
               @Override
               public void onResponse(String response) {
                    processJsonData(response);
               }
          }, new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {

               }
          }){

               @Override
               protected Map<String, String> getParams(){

                    Map<String,String> map = new HashMap<>();
                    map.put("userName","atik1404");


                    return map;
               }
          };

          requestQueue.add(stringRequest);
     }

     //process shopping only_show_cost json data
     private void processJsonData(String result)
     {
          List<CostModel> costList = new ArrayList<>();
          int count=0;
          String name,taka,date,id;
          try {
               JSONObject jsonObject = new JSONObject(result);
               JSONArray jsonArray = jsonObject.optJSONArray("costList");

               while (count<jsonArray.length())
               {
                    JSONObject jObject = jsonArray.getJSONObject(count);
                    id = jObject.getString("id");
                    name = jObject.getString("name");
                    taka = jObject.getString("taka");
                    date = jObject.getString("date");

                    costList.add(new CostModel(id,name,taka,date,""));
                    count++;
               }

               if(costList.isEmpty())
                    listView.setEmptyView(emptyView);
               else
                    emptyView.setVisibility(View.INVISIBLE);

               CostAdapter adapter = new CostAdapter(this, costList);
               listView.setAdapter(adapter);
          } catch (JSONException e) {
               e.printStackTrace();
          }
     }

     //get all shopping list
     OnAsyncTaskInterface infoInterfaces = new OnAsyncTaskInterface() {
          @Override
          public void onResultSuccess(final String result) {
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         if(result!=null)
                              processJsonData(result);
                    }
               });
          }
     };

}
