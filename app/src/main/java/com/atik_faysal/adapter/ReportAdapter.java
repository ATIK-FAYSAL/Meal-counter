package com.atik_faysal.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.atik_faysal.mealcounter.MemberDetails;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.CostModel;
import com.atik_faysal.others.EachMemReport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

public class ReportAdapter extends BaseAdapter
{

        private List<CostModel>modelList;
        private Context context;
        private String mealRate,month;

        public ReportAdapter(Context context,List<CostModel>modelList,String mealRate,String month)
        {
                this.context = context;
                this.modelList = modelList;
                this.mealRate = mealRate;
                this.month = month;
        }

        @Override
        public int getCount() {
                return modelList.size();
        }

        @Override
        public Object getItem(int i) {
                return modelList.get(i);
        }

        @Override
        public long getItemId(int i) {
                return i;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent)
        {
                final Map<String,String> infoMap = new HashMap<>();

                TextView txtUserName,txtTaka,txtMeal,txtCost,txtStatus;
                TextView bSee;
                final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View userView = inflater.inflate(R.layout.report_model, parent, false);

                txtUserName = userView.findViewById(R.id.txtName);
                txtTaka = userView.findViewById(R.id.txtTaka);
                txtMeal = userView.findViewById(R.id.txtMeal);
                txtCost = userView.findViewById(R.id.txtCost);
                txtStatus = userView.findViewById(R.id.txtStatus);
                bSee = userView.findViewById(R.id.bDetails);


                txtUserName.setText(modelList.get(position).getId());
                txtTaka.setText(modelList.get(position).getTaka());
                txtMeal.setText(modelList.get(position).getName());
                txtCost.setText(modelList.get(position).getDate());
                txtStatus.setText(modelList.get(position).getStatus());

                bSee.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                infoMap.put("name",modelList.get(position).getId());
                                infoMap.put("mealRate",mealRate);
                                infoMap.put("month",month);
                                infoMap.put("taka",modelList.get(position).getTaka());
                                infoMap.put("cost",modelList.get(position).getDate());
                                infoMap.put("meal",modelList.get(position).getName());
                                infoMap.put("status",modelList.get(position).getStatus());
                                Intent intent = new Intent(context, EachMemReport.class);
                                intent.putExtra("map", (Serializable) infoMap);
                                context.startActivity(intent);
                        }
                });

                txtUserName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                Intent page = new Intent(context,MemberDetails.class);
                                page.putExtra("userName",modelList.get(position).getId());
                                context.startActivity(page);
                        }
                });

                return userView;
        }
}
