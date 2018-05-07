package com.atik_faysal.superClasses;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;

import com.atik_faysal.adapter.NoticeAdapter;
import com.atik_faysal.adapter.NoticeAdapter.MyViewHolder;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.NoticeModel;

import java.util.List;

public class AdaptersSuperClass extends RecyclerView.Adapter<AdaptersSuperClass.ViewHolder> {

        private Context context;
        private List<Class<?>>modelList;
        private LayoutInflater inflater;
        private int layout;
        public AdaptersSuperClass(Context context,List<Class<?>>model,int layout)
        {
                this.context = context;
                this.modelList = model;
                this.inflater = LayoutInflater.from(context);
                this.layout = layout;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
                View view = inflater.inflate(layout,parent,false);
                return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position)
        {
                Class<?> current = modelList.get(position);
                holder.setData(current,position);
                holder.setListeners();
        }

        @Override
        public int getItemCount() {
                return modelList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {
                private int position;
                private Class<?>nameOfClass;

                public ViewHolder(View view) {
                        super(view);
                }

                @SuppressLint("SetTextI18n")
                protected void setData(Class<?> currentObject, int position)
                {
                        this.nameOfClass = currentObject;
                        this.position = position;
                }

                protected void setListeners()
                {

                }

                @Override
                public void onClick(View view) {

                }
        }
}
