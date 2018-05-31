package com.atik_faysal.mealcounter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.TextView;

import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.superClasses.ShoppingCost;


public class CostForPublicGroup extends ShoppingCost
{



        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.cost_public_group);
                super.initComponent();
                init();
                super.onButtonClick(onAsyncTaskInterface,"pending");
                super.setToolbar();
        }

        @SuppressLint("SetTextI18n")
        private void init()
        {
                TextView txtName = findViewById(R.id.txtName);
                TextView txtSession = findViewById(R.id.txtSession);

                someMethod = new NeedSomeMethod(this);
                sharedPreferenceData = new SharedPreferenceData(this);
                dialogClass = new AlertDialogClass(this);
                internetIsOn = new CheckInternetIsOn(this);
                someMethod = new NeedSomeMethod(this);

                txtName.setText(sharedPreferenceData.getCurrentUserName());
                txtSession.setText("#"+sharedPreferenceData.getmyCurrentSession());
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
                                                        CostForPublicGroup.super.initComponent();
                                                        someMethod.progress("Adding today's cost...","Today's cost added successfully.Please wait for admin approval.");
                                                        break;
                                                case "error":
                                                        dialogClass.error("Execution failed,Please try again.");
                                                        break;
                                                case "exist":
                                                        dialogClass.error("Execution failed,Today's shopping cost is already added.");
                                                        break;
                                        }
                                }
                        });
                }
        };
}
