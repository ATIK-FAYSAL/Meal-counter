package com.atik_faysal.others;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.atik_faysal.adapter.MealAdapter;
import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.MealClass;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.MealModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 3/19/2018.
 */

public class MyMeal extends Fragment
{
        private ListView listView;

        private SharedPreferenceData sharedPreferenceData;
        private NeedSomeMethod someMethod;
        private AlertDialogClass dialogClass;
        private DatabaseBackgroundTask backgroundTask;
        private CheckInternetIsOn internetIsOn;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.only_show_meal, container, false);
                listView = view.findViewById(R.id.list);
                SwipeRefreshLayout refreshLayout = view.findViewById(R.id.refresh);
                refreshLayout.setColorSchemeResources(R.color.color2,R.color.red,R.color.color6);

                sharedPreferenceData = new SharedPreferenceData(getContext());
                someMethod = new NeedSomeMethod(getContext());
                dialogClass = new AlertDialogClass(getContext());
                internetIsOn = new CheckInternetIsOn(getContext());
                backgroundTask = new DatabaseBackgroundTask(getContext());

                someMethod.reloadPage(refreshLayout, MealClass.class);
                getAllMealListFromDb();

                return view;
        }

        //when this activity start it get all meal list from database and set on list view
        private void getAllMealListFromDb()
        {
                //String url = "http://192.168.56.1/myAllMeal.php";
                if(internetIsOn.isOnline())
                {
                        try {
                                String data = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getCurrentUserName(),"UTF-8");
                                backgroundTask = new DatabaseBackgroundTask(getContext());
                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                backgroundTask.execute(getResources().getString(R.string.myMeal),data);
                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }
                }else dialogClass.noInternetConnection();
        }



        //json data processing ,it's convert json data to string and set on array list
        private void processJsonData(String jsonData)
        {
                String name,date,breakfast,dinner,lunch,total;
                List<MealModel> modelList = new ArrayList<>();
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

                        MealAdapter adapter = new MealAdapter(getContext(), modelList);
                        listView.setAdapter(adapter);


                } catch (JSONException e) {
                        e.printStackTrace();
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
