package com.atik_faysal.adapter;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.CostModel;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by USER on 3/1/2018.
 */

public class CostAdapter extends BaseAdapter
{
        private List<CostModel>costList = new ArrayList<>();
        private Context context;
        private View view;

        private SharedPreferenceData sharedPreferenceData;

        public CostAdapter(Context context,List<CostModel>cosList)
        {
                this.context = context;
                this.costList = cosList;
                sharedPreferenceData = new SharedPreferenceData(context);
        }

        @Override
        public int getCount() {
                return costList.size();
        }

        @Override
        public Object getItem(int position) {
                return costList.get(position);
        }

        @Override
        public long getItemId(int position) {
                return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {

                TextView txtName,txtDate,txtTaka,txtId;
                ImageView bEdit;
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.cost_model, parent, false);

                txtName = view.findViewById(R.id.txtName);
                txtDate = view.findViewById(R.id.txtDate);
                txtTaka = view.findViewById(R.id.txtTaka);
                txtId = view.findViewById(R.id.txtId);
                bEdit = view.findViewById(R.id.bEdit);

                txtId.setText("#D"+costList.get(position).getId());
                txtName.setText(costList.get(position).getName());
                txtTaka.setText(costList.get(position).getTaka());
                txtDate.setText(costList.get(position).getDate());

                bEdit.setEnabled(false);
                bEdit.setImageDrawable(null);

                if(sharedPreferenceData.getUserType().equals("admin"))
                {
                        bEdit.setEnabled(true);
                        bEdit.setBackgroundResource(R.drawable.edit);
                }else if(sharedPreferenceData.getMyGroupType().equals("public"))
                {
                        if(sharedPreferenceData.getCurrentUserName().equals(costList.get(position).getName())) {
                                bEdit.setEnabled(true);
                                bEdit.setBackgroundResource(R.drawable.edit);
                        }
                }

                bEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                editShoppingCost(costList.get(position).getId());
                        }
                });

                return view;
        }


        private void editShoppingCost(String id)
        {

        }
}