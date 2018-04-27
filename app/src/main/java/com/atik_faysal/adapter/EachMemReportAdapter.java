package com.atik_faysal.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.CostModel;
import com.atik_faysal.model.MealModel;

import java.util.List;

public class EachMemReportAdapter extends BaseAdapter
{

        private List<MealModel> modelList;
        private Context context;

        public EachMemReportAdapter(Context context,List<MealModel>modelList)
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

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
                final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View userView = inflater.inflate(R.layout.meal_model, parent, false);
                TextView txtDate,txtName,txtBreakfast,txtDinner,txtLunch,txtTotal;
                ImageView imgEdit;

                txtDate = userView.findViewById(R.id.txtDate);
                txtName = userView.findViewById(R.id.txtName);
                txtBreakfast = userView.findViewById(R.id.eBreakfast);
                txtDinner = userView.findViewById(R.id.eDinner);
                txtLunch = userView.findViewById(R.id.eLunch);
                txtTotal = userView.findViewById(R.id.eTotal);

                imgEdit = userView.findViewById(R.id.bEdit);


                txtDate.setText(modelList.get(position).getDate());
                txtName.setText(modelList.get(position).getName());
                txtBreakfast.setText(modelList.get(position).getBreakfast());
                txtDinner.setText(modelList.get(position).getDinner());
                txtLunch.setText(modelList.get(position).getLunch());
                txtTotal.setText(modelList.get(position).getTotal());

                imgEdit.setEnabled(false);
                imgEdit.setImageDrawable(null);

                return userView;
        }
}
