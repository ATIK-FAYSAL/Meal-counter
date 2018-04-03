package com.atik_faysal.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.AddCost;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.CostModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by USER on 3/1/2018.
 */

public class CostAdapter extends BaseAdapter
{
        private List<CostModel>costList;
        private Context context;
        private View view;
        private Activity activity;


        private AlertDialog alertDialog;
        private DatabaseBackgroundTask backgroundTask;
        private AlertDialogClass dialogClass;
        private SharedPreferenceData sharedPreferenceData;
        private CheckInternetIsOn internetIsOn;

        public CostAdapter(Context context,List<CostModel>cosList)
        {
                this.context = context;
                this.costList = cosList;
                this.activity = (Activity)context;
                sharedPreferenceData = new SharedPreferenceData(context);
                dialogClass = new AlertDialogClass(context);
                internetIsOn = new CheckInternetIsOn(context);
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
                                editShoppingCost(costList.get(position).getId(),costList.get(position).getDate(),costList.get(position).getTaka());
                        }
                });

                return view;
        }


        //it will show an alertDialog and you can edit shopping cost from here
        private void editShoppingCost(final String id,String date,String taka)
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

                txtId.setText("#D"+id);
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
                                backgroundTask = new DatabaseBackgroundTask(context);
                                //String url = "http://192.168.56.1/costEdit.php";
                                String taka = txtTaka.getText().toString();
                                if(TextUtils.isEmpty(taka))
                                {
                                        txtTaka.setError("Invalid taka");
                                        return;
                                }
                                try {
                                        if(internetIsOn.isOnline())
                                        {
                                                String data = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(id,"UTF-8")+"&"
                                                        +URLEncoder.encode("taka","UTF-8")+"="+URLEncoder.encode(taka,"UTF-8");

                                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                                backgroundTask.execute(context.getResources().getString(R.string.costEdit),data);
                                        }else dialogClass.noInternetConnection();
                                } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                }
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
                                                        context.startActivity(new Intent(context, AddCost.class));
                                                        activity.finish();
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