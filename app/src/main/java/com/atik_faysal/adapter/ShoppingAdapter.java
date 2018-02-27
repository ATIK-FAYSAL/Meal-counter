package com.atik_faysal.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.ShoppingItemModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by USER on 2/22/2018.
 */

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.MyViewHolder>
{
        List<ShoppingItemModel>itemModels = new ArrayList<>();
        LayoutInflater inflater;
        Context context;
        public ShoppingAdapter(Context context,List<ShoppingItemModel>itemModels)
        {
                inflater = LayoutInflater.from(context);
                this.context = context;
                this.itemModels = itemModels;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
                View view = inflater.inflate(R.layout.item_list, parent, false);
                MyViewHolder holder = new MyViewHolder(view);
                return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
                ShoppingItemModel current = itemModels.get(position);
                holder.setData(current, position);
                holder.setListeners();
        }

        @Override
        public int getItemCount() {
                return itemModels.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {
                public EditText itemName;
                public EditText itemQuantity;
                public EditText itemPrice;
                private int position;
                private ShoppingItemModel currentObject;
                private ImageView imgDelete, imgCopy;

                public MyViewHolder(View itemView) {
                        super(itemView);
                        itemName = itemView.findViewById(R.id.itemName);
                        itemQuantity = itemView.findViewById(R.id.itemQuantity);
                        itemPrice = itemView.findViewById(R.id.itemPrice);
                        imgCopy = itemView.findViewById(R.id.img_copy);
                        imgDelete = itemView.findViewById(R.id.img_delete);
                }

                public void setData(ShoppingItemModel currentObject, int position) {
                        this.itemName.setText(currentObject.getItemName());
                        this.position = position;
                        this.currentObject = currentObject;
                        this.itemQuantity.setText(currentObject.getQuantity());
                        this.itemPrice.setText(currentObject.getPrice());
                }

                public void setListeners() {
                        imgDelete.setOnClickListener(MyViewHolder.this);
                        imgCopy.setOnClickListener(MyViewHolder.this);
                }

                @Override
                public void onClick(View v) {
                        switch (v.getId())
                        {
                                case R.id.img_delete:
                                        removeItem(position);
                                        break;
                                case R.id.img_copy:
                                        addItem(position,currentObject);
                                        break;
                        }
                }

                public void removeItem(int position) {
                        itemModels.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, itemModels.size());
                }

                public void addItem(int position, ShoppingItemModel currentObject) {
                        itemModels.add(position, currentObject);
                        notifyItemInserted(position);
                        notifyItemRangeChanged(position, itemModels.size());
                }
        }
}
