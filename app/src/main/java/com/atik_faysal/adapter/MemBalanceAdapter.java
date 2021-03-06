package com.atik_faysal.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.backend.PostData;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.MemberDetails;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.MemBalanceModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemBalanceAdapter  extends BaseAdapter
{
        private Context context;
        private List<MemBalanceModel>balanceModels;
        private SharedPreferenceData sharedPreferenceData;
        private CheckInternetIsOn internetIsOn;
        private Activity activity;
        private AlertDialogClass dialogClass;
        private AlertDialog alertDialog;

        public MemBalanceAdapter(Context context, List<MemBalanceModel> modelList)
        {
                this.context = context;
                this.balanceModels = modelList;
                this.sharedPreferenceData = new SharedPreferenceData(context);
                this.internetIsOn = new CheckInternetIsOn(context);
                this.activity = (Activity)context;
                this.dialogClass = new AlertDialogClass(context);
        }

        @Override
        public int getCount() {
                return balanceModels.size();
        }

        @Override
        public Object getItem(int position) {
                return balanceModels.get(position);
        }

        @Override
        public long getItemId(int position) {
                return position;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                @SuppressLint("ViewHolder") View view = inflater.inflate(R.layout.mem_balance_model, parent, false);
                TextView txtId,txtName,txtTaka;
                txtId = view.findViewById(R.id.txtId);
                txtName = view.findViewById(R.id.txtName);
                txtTaka = view.findViewById(R.id.txtTaka);

                txtId.setText("#ID-10050"+balanceModels.get(position).getId());
                txtName.setText(balanceModels.get(position).getName());
                txtTaka.setText(balanceModels.get(position).getBalance());

                if(sharedPreferenceData.getUserType().equals("admin"))
                {
                        txtTaka.setClickable(true);
                }

                txtTaka.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                editShoppingCost(balanceModels.get(position).getName(),balanceModels.get(position).getId(),balanceModels.get(position).getBalance());
                        }
                });

                txtName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                Intent page = new Intent(context,MemberDetails.class);
                                page.putExtra("userName",balanceModels.get(position).getName());
                                context.startActivity(page);
                        }
                });

                return view;
        }



        //it will show an alertDialog and you can edit shopping cost from here
        @SuppressLint("SetTextI18n")
        private void editShoppingCost(final String name,final String id,String taka)
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

                txtId.setText("#ID-10050"+id);
                txtDate.setText(name);
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

                                if(internetIsOn.isOnline()) {
                                        Map<String,String>map = new HashMap<>();
                                        map.put("user",name);
                                        map.put("taka",taka);
                                        map.put("action","update");
                                        PostData postData = new PostData(context,onAsyncTaskInterface);
                                        postData.InsertData(context.getResources().getString(R.string.memBalances),map);
                                }else dialogClass.noInternetConnection();

                                /*try {
                                        if(internetIsOn.isOnline())
                                        {
                                                String data = URLEncoder.encode("user","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"
                                                        +URLEncoder.encode("taka","UTF-8")+"="+URLEncoder.encode(taka,"UTF-8")+"&"
                                                        +URLEncoder.encode("action","UTF-8")+"="+URLEncoder.encode("update","UTF-8");

                                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                                backgroundTask.execute(context.getResources().getString(R.string.memBalances),data);
                                        }else dialogClass.noInternetConnection();
                                } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                }*/
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

                                        Toast.makeText(context,"result :"+message,Toast.LENGTH_LONG).show();
                                        switch (message)
                                        {
                                                case "success":
                                                        alertDialog.dismiss();
                                                        new NeedSomeMethod(context).progress("Updating member balance...","Balance updated");
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
