package com.atik_faysal.others;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.atik_faysal.adapter.MealAdapter;
import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.GetDataFromServer;
import com.atik_faysal.backend.PostData;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.Feedback;
import com.atik_faysal.mealcounter.MealClass;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.MealModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by USER on 3/19/2018.
 */

public class InputMeal extends Fragment
{
        private TextView txtName,txtDate,txtMonth;
        private EditText eBreakfast,eDinner,eLaunch;
        private View view;
        private ListView listView;
        private RelativeLayout emptyView;

        private String breakfast,dinner,lunch;

        private CheckInternetIsOn internetIsOn;
        private NeedSomeMethod someMethod;
        private AlertDialogClass dialogClass;
        private SharedPreferenceData sharedPreferenceData;
        private TextView txtNoResult;
        private ProgressBar progressBar;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                //view = inflater.inflate(R.layout.input_meal, container, false);
                sharedPreferenceData = new SharedPreferenceData(getContext());
                if((sharedPreferenceData.getUserType().equals("admin"))||(sharedPreferenceData.getMyGroupType().equals("public"))
                        ||(sharedPreferenceData.getMyGroupType().equals("close")))
                {
                        view = inflater.inflate(R.layout.input_meal, container, false);
                        onActivityStart();
                }else if(sharedPreferenceData.getMyGroupType().equals("secret"))
                {
                        view = inflater.inflate(R.layout.only_show_meal, container, false);
                        initComponent();
                }
                return view;
        }


        //this method define which activity will start,
        private void onActivityStart()
        {
                //initialize component
                txtName = view.findViewById(R.id.txtName);
                txtDate = view.findViewById(R.id.txtDate);
                txtMonth = view.findViewById(R.id.txtMonth);
                eBreakfast = view.findViewById(R.id.eBreakfast);
                eDinner = view.findViewById(R.id.eDinner);
                eLaunch = view.findViewById(R.id.eLunch);
                Button bOk = view.findViewById(R.id.bOk);

                initComponent();
                setInfo();

                bOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                breakfast = eBreakfast.getText().toString();
                                lunch = eLaunch.getText().toString();
                                dinner = eDinner.getText().toString();

                                if(breakfast.isEmpty())
                                {
                                        eBreakfast.setError("Invalid");
                                        return;
                                }
                                if(lunch.isEmpty())
                                {
                                        eLaunch.setError("Invalid");
                                        return;
                                }
                                if(dinner.isEmpty())
                                {
                                        eDinner.setError("Invalid");
                                        return;
                                }

                                double totalMeal=0;

                                try {
                                        totalMeal = Double.parseDouble(breakfast)+Double.parseDouble(lunch)+Double.parseDouble(dinner);
                                }catch (NumberFormatException e)
                                {
                                        e.printStackTrace();
                                }

                                if(internetIsOn.isOnline())
                                {
                                        Map<String,String> map = new HashMap<>();
                                        map.put("group",sharedPreferenceData.getMyGroupName());
                                        map.put("date",someMethod.getDate());
                                        map.put("member",sharedPreferenceData.getCurrentUserName());
                                        map.put("breakfast",breakfast);
                                        map.put("lunch",lunch);
                                        map.put("dinner",dinner);
                                        map.put("total",String.valueOf(totalMeal));
                                        PostData postData = new PostData(getContext(),anInterface);
                                        postData.InsertData(getResources().getString(R.string.inputMeal),map);
                                }
                        }
                });
        }

        //initialize component and call method
        private void initComponent()
        {
                listView = view.findViewById(R.id.list);
                emptyView = view.findViewById(R.id.empty_view);
                txtNoResult = view.findViewById(R.id.txtNoResult);
                txtNoResult.setVisibility(View.INVISIBLE);
                progressBar = view.findViewById(R.id.progressBar);

                internetIsOn = new CheckInternetIsOn(getContext());
                dialogClass = new AlertDialogClass(getContext());
                someMethod = new NeedSomeMethod(getContext());

                //calling page reload method
                getAllMealListFromDb();
        }

        //when this activity start it get all meal list from database and set on list view
        private void getAllMealListFromDb()
        {
                //String url = "http://192.168.56.1/allMeal.php";
                if(internetIsOn.isOnline())
                {
                        Map<String,String> map = new HashMap<>();
                        map.put("userName",sharedPreferenceData.getCurrentUserName());
                        GetDataFromServer dataFromServer = new GetDataFromServer(getContext(),onAsyncTaskInterface,getResources().getString(R.string.allMeal),map);
                        dataFromServer.sendJsonRequest();
                }else dialogClass.noInternetConnection();
        }

        //get date from system and split and set month in text view
        @SuppressLint("SetTextI18n")
        private void setInfo()
        {
                txtName.setText(sharedPreferenceData.getCurrentUserName());

                String date = someMethod.getDate();
                txtDate.setText(date);
                txtMonth.setText("#"+sharedPreferenceData.getmyCurrentSession());
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

        //for insert member's meal
        OnAsyncTaskInterface anInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        switch (message)
                                        {
                                                case "success":
                                                        someMethod.progress("Inserting your today's meal.....","meal successfully inserted");
                                                        getAllMealListFromDb();
                                                        break;
                                                case "fail":
                                                        dialogClass.error("Your today's meal already added,if you edit this meal please contact with group admin.");
                                                        break;
                                                default:
                                                        dialogClass.error("Execution failed.Please try again.");
                                                        break;
                                        }
                                }
                        });
                }
        };

}
