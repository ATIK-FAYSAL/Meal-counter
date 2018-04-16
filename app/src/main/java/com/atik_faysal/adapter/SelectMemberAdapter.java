package com.atik_faysal.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.ShoppingItemModel;

import java.util.List;
import android.content.Context;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by USER on 2/24/2018.
 */

public class SelectMemberAdapter extends RecyclerView.Adapter<SelectMemberAdapter.ViewHolder>
{
        List<ShoppingItemModel>models ;
        Context context;

        LayoutInflater inflater;

        public SelectMemberAdapter(Context context,List<ShoppingItemModel>models)
        {
                this.context = context;
                this.models = models;
                inflater = LayoutInflater.from(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
                View view = inflater.inflate(R.layout.mem_select_model,parent,false);
                ViewHolder holder = new ViewHolder(view);

                return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
                ShoppingItemModel current = models.get(position);
                holder.setData(current,position);
        }

        @Override
        public int getItemCount() {
                return models.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

                public TextView txtUserName;
                public CheckBox checkBox;

                public ViewHolder(View itemView) {
                        super(itemView);
                        this.txtUserName = itemView.findViewById(R.id.txtName);
                        this.checkBox = itemView.findViewById(R.id.checkBox);
                }

                public void setData(ShoppingItemModel current,int possition)
                {
                        txtUserName.setText(current.getItemName());
                }
        }
}
