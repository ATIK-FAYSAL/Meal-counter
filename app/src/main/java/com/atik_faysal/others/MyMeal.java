package com.atik_faysal.others;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atik_faysal.adapter.MealAdapter;
import com.atik_faysal.backend.GetDataFromServer;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.MealClass;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.MealModel;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by USER on 3/19/2018.
 */

public class MyMeal extends Fragment
{
        private ListView listView;

        private SharedPreferenceData sharedPreferenceData;
        private AlertDialogClass dialogClass;
        private CheckInternetIsOn internetIsOn;
        private RelativeLayout emptyView;
        private TextView txtNoResult;
        private ProgressBar progressBar;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.only_show_meal, container, false);
                listView = view.findViewById(R.id.list);
                emptyView = view.findViewById(R.id.empty_view);
                AdView adView = view.findViewById(R.id.adView);
                SwipeRefreshLayout refreshLayout = view.findViewById(R.id.refresh);
                refreshLayout.setColorSchemeResources(R.color.color2,R.color.red,R.color.color6);

                sharedPreferenceData = new SharedPreferenceData(getContext());
                NeedSomeMethod someMethod = new NeedSomeMethod(getContext());
                dialogClass = new AlertDialogClass(getContext());
                internetIsOn = new CheckInternetIsOn(getContext());
                txtNoResult = view.findViewById(R.id.txtNoResult);
                txtNoResult.setVisibility(View.INVISIBLE);
                progressBar = view.findViewById(R.id.progressBar);

                someMethod.reloadPage(refreshLayout, MealClass.class);
                someMethod.setAdmob(adView);
                getAllMealListFromDb();

                return view;
        }

        //when this activity start it get all meal list from database and set on list view
        private void getAllMealListFromDb()
        {
                //String url = "http://192.168.56.1/myAllMeal.php";
                if(internetIsOn.isOnline())
                {
                        Map<String,String> map = new HashMap<>();
                        map.put("userName",sharedPreferenceData.getCurrentUserName());
                        GetDataFromServer dataFromServer = new GetDataFromServer(getContext(),onAsyncTaskInterface,getResources().getString(R.string.myMeal),map);
                        dataFromServer.sendJsonRequest();
                }else dialogClass.noInternetConnection();
        }


        //json data processing ,it's convert json data to string and set on array list
        private void processJsonData(String jsonData)
        {
                String name,date,breakfast,dinner,lunch,total;
                final List<MealModel> modelList = new ArrayList<>();
                try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray jsonArray = jsonObject.optJSONArray("mealInfo");

                        int count = 0;
                        while (count < jsonArray.length()) {
                                JSONObject jObject = jsonArray.getJSONObject(count);
                                name = jObject.getString("name");
                                date = jObject.getString("date");
                                breakfast = jObject.getString("breakfast");
                                dinner = jObject.getString("dinner");
                                lunch = jObject.getString("lunch");
                                total = jObject.getString("total");
                                modelList.add(new MealModel(date,name,breakfast,lunch,dinner,total));
                                count++;
                        }

                } catch (JSONException e) {
                        e.printStackTrace();
                }finally {
                        final Timer timer = new Timer();
                        final Handler handler = new Handler();

                        final  Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                        if(modelList.isEmpty())
                                        {
                                                txtNoResult.setVisibility(View.VISIBLE);
                                                listView.setEmptyView(emptyView);
                                        }
                                        else
                                        {
                                                emptyView.setVisibility(View.INVISIBLE);
                                                MealAdapter adapter = new MealAdapter(getContext(), modelList);
                                                listView.setAdapter(adapter);
                                        }
                                        progressBar.setVisibility(View.GONE);
                                        timer.cancel();
                                }
                        };
                        timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                        handler.post(runnable);
                                }
                        },2800);
                }
        }

        //for getting all member's meal and show on listview
        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        if(message!=null)
                                                processJsonData(message);
                                }
                        });
                }
        };
}
