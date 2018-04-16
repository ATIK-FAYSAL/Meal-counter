package com.atik_faysal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.CostModel;

import java.util.List;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

public class ReportAdapter extends BaseAdapter
{

        List<CostModel>modelList;
        Context context;

        public ReportAdapter(Context context,List<CostModel>modelList)
        {
                this.context = context;
                this.modelList = modelList;
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
        public View getView(int position, View view, ViewGroup parent)
        {
                TextView txtUserName,txtTaka,txtMeal,txtCost,txtStatus;
                Button bDetails;
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View userView = inflater.inflate(R.layout.report_model, parent, false);

                txtUserName = userView.findViewById(R.id.txtName);
                txtTaka = userView.findViewById(R.id.txtTaka);
                txtMeal = userView.findViewById(R.id.txtMeal);
                txtCost = userView.findViewById(R.id.txtCost);
                txtStatus = userView.findViewById(R.id.txtStatus);
                bDetails = userView.findViewById(R.id.bDetails);


                txtUserName.setText(modelList.get(position).getId());
                txtTaka.setText(modelList.get(position).getTaka());
                txtMeal.setText(modelList.get(position).getName());
                txtCost.setText(modelList.get(position).getDate());
                txtStatus.setText(modelList.get(position).getStatus());

                bDetails.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                });

                return userView;
        }
}
