package com.atik_faysal.mealcounter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.atik_faysal.backend.GetDataFromServer;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.superClasses.ShoppingCost;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by USER on 2/28/2018.
 */

public class CostOfSecretCloseGroup extends ShoppingCost {


        private TextView txtName;
        private Spinner spinner;
        private List<String> memName;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.cost_secret_close_group);
                init();
                super.initComponent();
                super.onButtonClick(onAsyncTaskInterface,"addBalance");
                super.setToolbar();
        }

        //initialize all  component
        @SuppressLint("SetTextI18n")
        protected void init() {
                spinner = findViewById(R.id.spinner);
                txtName = findViewById(R.id.txtName);
                spinner = findViewById(R.id.spinner);
                TextView txtSession = findViewById(R.id.txtSession);

                someMethod = new NeedSomeMethod(this);
                sharedPreferenceData = new SharedPreferenceData(this);
                dialogClass = new AlertDialogClass(this);
                internetIsOn = new CheckInternetIsOn(this);
                someMethod = new NeedSomeMethod(this);
                txtSession.setText("#"+sharedPreferenceData.getmyCurrentSession());

                if (internetIsOn.isOnline()) {
                        Map<String,String> map = new HashMap<>();
                        map.put("group",sharedPreferenceData.getMyGroupName());
                        GetDataFromServer dataFromServer = new GetDataFromServer(this,onAsyncTaskInterface
                             ,getResources().getString(R.string.groupMemberName),map);
                        dataFromServer.sendJsonRequest();
                } else dialogClass.noInternetConnection();
        }



        //here,get all name of this group and add to this spinner.when you select a name this will show in txtName textField.
        private void addMemberNameSpinner(List<String> names) {
                ArrayAdapter<String> memAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, names);
                spinner.setAdapter(memAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String name = parent.getItemAtPosition(position).toString();
                                if (name.equals("Select"))
                                        txtName.setText("Member's name");
                                else txtName.setText(name);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                });
        }

        //to add today's only_show_cost interface
        protected OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String result) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        switch (result) {
                                                case "success":
                                                        CostOfSecretCloseGroup.super.initComponent();
                                                        someMethod.progress("Adding today's cost...","Today's cost add successfully.");
                                                        break;
                                                case "error":
                                                        dialogClass.error("Execution failed,Please try again.");
                                                        break;
                                                case "exist":
                                                        dialogClass.error("Execution failed,Today's shopping cost is already added.");
                                                        break;
                                                default:
                                                        memName = new ArrayList<>();
                                                        memName.add("Select");
                                                        int count = 0;
                                                        String name;
                                                        try {
                                                                JSONObject jsonObject = new JSONObject(result);
                                                                JSONArray jsonArray = jsonObject.optJSONArray("userName");
                                                                while (count < jsonArray.length()) {
                                                                        JSONObject jObject = jsonArray.getJSONObject(count);
                                                                        name = jObject.getString("userName");
                                                                        memName.add(name);
                                                                        count++;
                                                                }
                                                                addMemberNameSpinner(memName);
                                                        } catch (JSONException e) {
                                                                e.printStackTrace();
                                                        }
                                                        break;
                                        }
                                }
                        });
                }
        };
}