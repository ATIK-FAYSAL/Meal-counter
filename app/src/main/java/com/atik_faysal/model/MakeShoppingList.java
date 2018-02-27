package com.atik_faysal.model;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.atik_faysal.adapter.ShoppingAdapter;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.others.SelectMember;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 2/22/2018.
 */

public class MakeShoppingList extends Fragment {

        private List<ShoppingItemModel> itemModels = new ArrayList<>();


        private RecyclerView recyclerView;
        private SharedPreferenceData sharedPreferenceData;

        private View view;
        private ShoppingAdapter adapter;
        private NeedSomeMethod someMethod;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                view = inflater.inflate(R.layout.shopping_list, container, false);
                Firebase.setAndroidContext(getContext());
                initComponent();
                return view;
        }

        private void initComponent()
        {
                //set some value in list
                itemModels.add(new ShoppingItemModel("Rice","1 KG","70.00"));
                itemModels.add(new ShoppingItemModel("Meet","1 KG","500.00"));
                itemModels.add(new ShoppingItemModel("Fish","1 KG","300.00"));
                itemModels.add(new ShoppingItemModel("Vegetable","2 KG","100.00"));
                itemModels.add(new ShoppingItemModel("Others","5 KG","500.00"));

                recyclerView = view.findViewById(R.id.recyclerView);

                if(getContext()!=null)
                        adapter = new ShoppingAdapter(getContext(),itemModels);

                recyclerView.setAdapter(adapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                Button bPublish = view.findViewById(R.id.bPublish);

                someMethod = new NeedSomeMethod(getContext());
                sharedPreferenceData = new SharedPreferenceData(getContext());

                bPublish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                insertShoppingListToFirebase();
                        }
                });
        }

        //store shopping list in firebase database
        public void insertShoppingListToFirebase()
        {
                double totalTaka=0;
                boolean flag = true;

                Firebase firebase = new Firebase("https://mealcounter-d5a11.firebaseio.com/shopping");

                firebase.child(sharedPreferenceData.getMyGroupName()).removeValue();

                String name,quantity,price;
                if(recyclerView.getChildCount()>0)
                {
                        for(int i=0;i<recyclerView.getChildCount();i++)
                        {
                                if(recyclerView.findViewHolderForLayoutPosition(i)instanceof ShoppingAdapter.MyViewHolder)
                                {
                                        ShoppingAdapter.MyViewHolder holder = (ShoppingAdapter.MyViewHolder) recyclerView.findViewHolderForLayoutPosition(i);
                                        name = holder.itemName.getText().toString();
                                        quantity = holder.itemQuantity.getText().toString();
                                        price = holder.itemPrice.getText().toString();
                                        try {
                                                if(TextUtils.isEmpty(name)||TextUtils.isEmpty(quantity))
                                                {
                                                        if(TextUtils.isEmpty(name))
                                                                holder.itemName.setError("Empty name,you can remove this row");
                                                        else if(TextUtils.isEmpty(quantity))
                                                                holder.itemQuantity.setError("Empty quantity,you can remove this row.");
                                                        flag = false;
                                                }else
                                                {
                                                        totalTaka+=Double.parseDouble(price);
                                                        firebase.child(sharedPreferenceData.getMyGroupName()).child("item").child("item"+String.valueOf(i+1)).child("name").setValue(name);
                                                        firebase.child(sharedPreferenceData.getMyGroupName()).child("item").child("item"+String.valueOf(i+1)).child("quantity").setValue(quantity);
                                                        firebase.child(sharedPreferenceData.getMyGroupName()).child("item").child("item"+String.valueOf(i+1)).child("prize").setValue(price);
                                                }
                                        }catch (NumberFormatException e)
                                        {
                                                holder.itemPrice.setError("Invalid taka");
                                                flag = false;
                                        }

                                }
                        }

                        if(flag)
                        {
                                firebase.child(sharedPreferenceData.getMyGroupName()).child("date").setValue(someMethod.getDate());
                                firebase.child(sharedPreferenceData.getMyGroupName()).child("taka").setValue(String.valueOf(totalTaka));
                                startActivity(new Intent(getActivity(),SelectMember.class));
                        }
                }
        }

}
