package com.atik_faysal.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.PostData;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.CostModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BalanceApprovalAdapter extends RecyclerView.Adapter<BalanceApprovalAdapter.ViewHolder>
{
        private List<CostModel>modelList;
        private Context context;
        private LayoutInflater inflater;
        private Activity activity;
        private SharedPreferenceData sharedPreferenceData;

        public BalanceApprovalAdapter(Context context,List<CostModel>costModels)
        {
                this.modelList = costModels;
                this.context = context;
                this.inflater = LayoutInflater.from(context);
                this.activity = (Activity)context;
                sharedPreferenceData = new SharedPreferenceData(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
                View view;
                if(sharedPreferenceData.getUserType().equals("admin"))
                        view = inflater.inflate(R.layout.approve_balance,parent,false);
                else
                {
                        view = inflater.inflate(R.layout.pending_balance,parent,false);
                        EditText text = view.findViewById(R.id.txtTaka);
                        text.setEnabled(false);
                        text.setClickable(false);
                        text.setTextColor(activity.getResources().getColor(R.color.black));
                }
                return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position)
        {
                CostModel model = modelList.get(position);
                holder.setData(model,position);
                holder.setListeners();
        }

        @Override
        public int getItemCount() {
                return modelList.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {
                private int position;
                private CostModel model;
                private TextView txtDate,txtName,txtStatus,txtId ;
                private EditText txtTaka;
                private Button bCancel,bApprv;
                private SharedPreferenceData sharedPreferenceData;
                private AlertDialogClass dialogClass;
                private CheckInternetIsOn internetIsOn;
                private NeedSomeMethod someMethod;
                Map<String,String> map = new HashMap<>();

                public ViewHolder(View view) {
                        super(view);
                        txtName = view.findViewById(R.id.txtName);
                        txtDate = view.findViewById(R.id.txtDate);
                        txtStatus = view.findViewById(R.id.txtStatus);
                        txtTaka = view.findViewById(R.id.txtTaka);
                        txtId = view.findViewById(R.id.txtId);
                        sharedPreferenceData = new SharedPreferenceData(context);
                        internetIsOn = new CheckInternetIsOn(context);
                        dialogClass = new AlertDialogClass(context);
                        someMethod = new NeedSomeMethod(context);

                        if(sharedPreferenceData.getUserType().equals("admin"))
                        {
                                bCancel = view.findViewById(R.id.bCancel);
                                bApprv = view.findViewById(R.id.bApprove);
                        }
                }

                @SuppressLint("SetTextI18n")
                public void setData(CostModel currentObject, int position) {
                        this.position = position;
                        this.model = currentObject;

                        txtStatus.setText(model.getStatus());
                        txtName.setText(model.getName());
                        txtDate.setText(model.getDate());
                        txtTaka.setText(model.getTaka());
                        txtId.setText("#XY-10500"+model.getId());
                }

                private void setListeners() {
                        if(sharedPreferenceData.getUserType().equals("admin"))
                        {
                                bApprv.setOnClickListener(BalanceApprovalAdapter.ViewHolder.this);
                                bCancel.setOnClickListener(BalanceApprovalAdapter.ViewHolder.this);
                        }
                }


                @Override
                public void onClick(View view) {
                        switch (view.getId())
                        {
                                case R.id.bCancel:
                                        cancelRequest();
                                        break;
                                case R.id.bApprove:
                                        approveBalance();
                                        break;
                        }
                }


                private void cancelRequest()
                {
                        if(sharedPreferenceData.getUserType().equals("admin"))
                        {
                                dialogClass.onSuccessListener(onAsyncTaskInterface);
                                dialogClass.warning("Do you want to remove this ?");
                                map.put("id",model.getId());
                                map.put("check","remove");

                                /*try {
                                         data = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(model.getId(),"UTF-8")+"&"
                                                +URLEncoder.encode("check","UTF-8")+"="+URLEncoder.encode("remove","UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                }*/
                        }else dialogClass.error("Only admin can delete balance.you are not an admin..");
                }

                private void approveBalance()
                {
                        if(sharedPreferenceData.getUserType().equals("admin"))
                        {
                                dialogClass.onSuccessListener(onAsyncTaskInterface);
                                dialogClass.warning("Do you want to approve this ?");
                                map.put("id",model.getId());
                                map.put("check","approve");
                                map.put("taka",model.getTaka());
                                map.put("userName",model.getName());
                                /*try {
                                         data = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(model.getId(),"UTF-8")+"&"
                                                +URLEncoder.encode("check","UTF-8")+"="+URLEncoder.encode("approve","UTF-8")+"&"
                                                +URLEncoder.encode("taka","UTF-8")+"="+URLEncoder.encode(model.getTaka(),"UTF-8")+"&"
                                                +URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(model.getName(),"UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                }*/
                        }else dialogClass.error("Only admin can approved balance.you are not an admin..");
                }



                private OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                        @Override
                        public void onResultSuccess(final String message) {
                                activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                                String file = context.getResources().getString(R.string.approveBalance);
                                                switch (message)
                                                {
                                                        case "yes":
                                                                if(internetIsOn.isOnline())
                                                                {
                                                                        //backgroundTask = new DatabaseBackgroundTask(context);
                                                                        //backgroundTask.setOnResultListener(anInterface);
                                                                        //backgroundTask.execute(file,data);
                                                                        PostData postData = new PostData(context,anInterface);
                                                                        postData.InsertData(file,map);
                                                                }else dialogClass.noInternetConnection();

                                                                break;
                                                }
                                        }
                                });
                        }
                };

                private OnAsyncTaskInterface anInterface = new OnAsyncTaskInterface() {
                        @Override
                        public void onResultSuccess(final String message) {
                                activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                                switch (message)
                                                {
                                                        case "success":
                                                                someMethod.progress("Working on it....","Execution complete.");
                                                                modelList.remove(position);
                                                                notifyItemRemoved(position);
                                                                notifyItemRangeChanged(position, modelList.size());
                                                                break;
                                                        default:
                                                                dialogClass.error("Execution failed,please try again.");
                                                                break;
                                                }
                                        }
                                });
                        }
                };

        }
}
