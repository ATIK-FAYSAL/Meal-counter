package com.atik_faysal.others;

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
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.adapter.ShoppingAdapter;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.ShoppingItemModel;
import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 2/23/2018.
 */

public class ShoppingList extends Fragment
{
        private List<ShoppingItemModel>modelList;

        private TextView txtDate,txtTaka;
        private ShoppingAdapter adapter;
        private RecyclerView recyclerView;

        private SharedPreferenceData sharedPreferenceData;
        private NeedSomeMethod someMethod;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.shopping, container, false);
                initComponent(view);
                getFirebaseData();
                return view;
        }


        private void initComponent(View view)
        {
                txtDate = view.findViewById(R.id.txtDate);
                txtTaka = view.findViewById(R.id.txtTaka);
                Button bPublish = view.findViewById(R.id.bPublish);
                bPublish.setText("Upload");

                recyclerView = view.findViewById(R.id.recyclerView);
                recyclerView.setAdapter(adapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());

                sharedPreferenceData = new SharedPreferenceData(getContext());
                someMethod = new NeedSomeMethod(getContext());

                bPublish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                insertShoppingListToFirebase();
                        }
                });
        }

        //get shopping list from  firebase
        private void getFirebaseData()
        {
                modelList = new ArrayList<>();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("shopping");
                databaseReference.child(sharedPreferenceData.getMyGroupName()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                                int i=1;

                                String  date = dataSnapshot.child("date").getValue(String.class);
                                String taka = dataSnapshot.child("taka").getValue(String.class);

                                DataSnapshot contentSnapshot = dataSnapshot.child("/item");
                                for (DataSnapshot snapshot : contentSnapshot.getChildren()) {
                                        DataSnapshot cSnapshot = contentSnapshot.child("/item"+String.valueOf(i));
                                        String name = cSnapshot.child("name").getValue(String.class);
                                        String quantity = cSnapshot.child("quantity").getValue(String.class);
                                        String prize = cSnapshot.child("prize").getValue(String.class);

                                        try {
                                                modelList.add(new ShoppingItemModel(name,quantity,prize));
                                        }catch (NumberFormatException e)
                                        {
                                                e.printStackTrace();
                                        }

                                        i++;
                                }

                                //set into textview and recyclerview
                                txtTaka.setText("Total taka : "+taka);
                                txtDate.setText("Date : "+date);

                                if(getContext()!=null)
                                        adapter = new ShoppingAdapter(getContext(),modelList);
                                recyclerView.setAdapter(adapter);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

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
                                Toast.makeText(getContext(), "Shopping list published.", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                        }
                }
        }
}
