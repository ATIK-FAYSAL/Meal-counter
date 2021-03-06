package com.atik_faysal.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.atik_faysal.backend.PostData;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.MemberDetails;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.CostModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by USER on 3/1/2018.
 */

public class CostAdapter extends BaseAdapter
{
        private List<CostModel>costList;
        private Context context;
        private Activity activity;


        private AlertDialog alertDialog;
        private AlertDialogClass dialogClass;
        private SharedPreferenceData sharedPreferenceData;
        private CheckInternetIsOn internetIsOn;
        private NeedSomeMethod someMethod;

        public CostAdapter(Context context,List<CostModel>cosList)
        {
                this.context = context;
                this.costList = cosList;
                this.activity = (Activity)context;
                sharedPreferenceData = new SharedPreferenceData(context);
                dialogClass = new AlertDialogClass(context);
                internetIsOn = new CheckInternetIsOn(context);
                someMethod = new NeedSomeMethod(context);
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

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {

                TextView txtName,txtDate,txtTaka,txtId,txtStatus;
                ImageView bEdit;
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                @SuppressLint("ViewHolder") View view = inflater.inflate(R.layout.cost_model, parent, false);

                txtName = view.findViewById(R.id.txtName);
                txtDate = view.findViewById(R.id.txtDate);
                txtTaka = view.findViewById(R.id.txtTaka);
                txtId = view.findViewById(R.id.txtId);
                bEdit = view.findViewById(R.id.bEdit);
                txtStatus = view.findViewById(R.id.txtStatus);

                txtId.setText("#ID-1005"+costList.get(position).getId());
                txtName.setText(costList.get(position).getName());
                txtTaka.setText(costList.get(position).getTaka());
                txtDate.setText(costList.get(position).getDate());
                txtStatus.setText(costList.get(position).getStatus());

                bEdit.setEnabled(false);
                bEdit.setImageDrawable(null);

                if(sharedPreferenceData.getUserType().equals("admin")&&
                        (sharedPreferenceData.getMyGroupType().equals("close")||sharedPreferenceData.getMyGroupType().equals("secret")
                        ||sharedPreferenceData.getMyGroupType().equals("public")))
                {
                        bEdit.setEnabled(true);
                        bEdit.setBackgroundResource(R.drawable.icon_edit_blue);
                }else if((sharedPreferenceData.getMyGroupType().equals("public"))&&
                        (sharedPreferenceData.getUserType().equals("member")))
                {
                        if(sharedPreferenceData.getCurrentUserName().equals(costList.get(position).getName())) {
                                bEdit.setEnabled(true);
                                bEdit.setBackgroundResource(R.drawable.icon_edit_blue);
                        }
                }

                bEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                editShoppingCost(costList.get(position).getId(),costList.get(position).getDate(),costList.get(position).getTaka());
                        }
                });

                txtName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                Intent page = new Intent(context,MemberDetails.class);
                                page.putExtra("userName",costList.get(position).getName());
                                context.startActivity(page);
                        }
                });

                return view;
        }


        //it will show an alertDialog and you can icon_edit_blue shopping cost from here
        @SuppressLint("SetTextI18n")
        private void editShoppingCost(final String id, final String date, String taka)
        {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.edit_cost,null);
                builder.setView(view);
                builder.setCancelable(false);

                final TextView txtDate,txtId;
                final EditText txtTaka;
                Button cancel,done;

                txtDate = view.findViewById(R.id.txtDate);
                txtId = view.findViewById(R.id.txtId);
                txtTaka = view.findViewById(R.id.txtTaka);

                txtId.setText("#ID-1005"+id);
                txtDate.setText(date);
                txtTaka.setText(taka);

                cancel = view.findViewById(R.id.bCancel);
                done = view.findViewById(R.id.bDone);

                alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();

                done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                //String url = "http://192.168.56.1/costEdit.php";
                                String taka = txtTaka.getText().toString();
                                if(TextUtils.isEmpty(taka))
                                {
                                        txtTaka.setError("Invalid taka");
                                        return;
                                }

                                if (internetIsOn.isOnline())
                                {
                                        Map<String,String> map = new HashMap<>();
                                        map.put("id",id);
                                        map.put("taka",taka);
                                        PostData postData = new PostData(context,onAsyncTaskInterface);
                                        postData.InsertData(context.getResources().getString(R.string.costEdit),map);
                                }else dialogClass.noInternetConnection();
                        }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                alertDialog.dismiss();
                        }
                });
        }

        private OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        switch (message)
                                        {
                                                case "success":
                                                        alertDialog.dismiss();
                                                        someMethod.progress("Working on it....","Cost updated successfully,please reload this page.");
                                                        //context.startActivity(new Intent(context, CostOfSecretCloseGroup.class));
                                                        //activity.finish();
                                                        break;
                                                default:
                                                        alertDialog.dismiss();
                                                        dialogClass.error("Execution failed.please try again.");
                                                        break;
                                        }
                                }
                        });
                }
        };
}