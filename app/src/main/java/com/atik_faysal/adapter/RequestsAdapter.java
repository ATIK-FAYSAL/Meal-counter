package com.atik_faysal.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.ApproveBalance;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.MemberDetails;
import com.atik_faysal.mealcounter.MemberJoinRequest;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.MemberModel;
import com.atik_faysal.others.NoResultFound;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by USER on 2/11/2018.
 */

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder>
{
        private Context context;
        private Activity activity;
        private List<MemberModel>memberModelList;
        private LayoutInflater inflater;
        private SharedPreferenceData sharedPreferenceData;

        public RequestsAdapter(Context context,List<MemberModel>models)
        {
                this.context = context;
                this.activity = (Activity)context;
                this.inflater = LayoutInflater.from(context);
                this.memberModelList = models;
                sharedPreferenceData = new SharedPreferenceData(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view;
                if(sharedPreferenceData.getUserType().equals("admin"))
                         view = inflater.inflate(R.layout.member_model_show,parent,false);
                else  view = inflater.inflate(R.layout.request_layout,parent,false);
                return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                MemberModel current = memberModelList.get(position);
                holder.setData(current,position);
                holder.setListener();
        }

        @Override
        public int getItemCount() {
                return memberModelList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {

                private DatabaseBackgroundTask backgroundTask;
                private AlertDialogClass dialogClass;
                private CheckInternetIsOn internetIsOn;
                private NeedSomeMethod someMethod;
                private SharedPreferenceData sharedPreferenceData;
                private NoResultFound noResultFound;
                private TextView txtUserName,txtName,txtPhone,txtDate,txtStatus,txtId;
                private Button bAccept,bCancel;
                private MemberModel model;
                private int position;
                private String action;

                @SuppressLint("SetTextI18n")
                public ViewHolder(View view) {
                        super(view);
                        backgroundTask = new DatabaseBackgroundTask(context);
                        dialogClass = new AlertDialogClass(context);
                        internetIsOn = new CheckInternetIsOn(context);
                        txtName = view.findViewById(R.id.txtName);
                        txtUserName = view.findViewById(R.id.txtUserName);
                        txtPhone = view.findViewById(R.id.txtPhone);
                        txtId = view.findViewById(R.id.txtType);
                        txtStatus = view.findViewById(R.id.txtTaka);
                        txtDate = view.findViewById(R.id.txtDate);

                        //bAccept.setTextColor(ContextCompat.getColor(context, R.color.green));
                        txtStatus.setTextColor(ContextCompat.getColor(context, R.color.color6));

                        internetIsOn = new CheckInternetIsOn(context);
                        dialogClass = new AlertDialogClass(context);
                        sharedPreferenceData = new SharedPreferenceData(context);
                        someMethod = new NeedSomeMethod(context);
                        noResultFound = new NoResultFound(context);

                        if(sharedPreferenceData.getUserType().equals("admin"))
                        {
                                bAccept = view.findViewById(R.id.bDetails);
                                bCancel = view.findViewById(R.id.bRemove);
                                bAccept.setText("Accept");
                                bCancel.setText("Cancel");
                        }

                }


                @SuppressLint("SetTextI18n")
                protected void setData(MemberModel currentObject, int position)
                {
                        this.model = currentObject;
                        this.position = position;

                        txtName.setText(model.getName());
                        txtUserName.setText(model.getUserName());
                        txtPhone.setText(model.getPhone());
                        txtId.setText("#R105"+model.getType());
                        txtStatus.setText(model.getTaka());
                        txtDate.setText(model.getDate());
                }


                protected void setListener()
                {
                        if(sharedPreferenceData.getUserType().equals("admin"))
                        {
                                bAccept.setOnClickListener(RequestsAdapter.ViewHolder.this);
                                bCancel.setOnClickListener(RequestsAdapter.ViewHolder.this);
                        }
                        txtUserName.setOnClickListener(RequestsAdapter.ViewHolder.this);
                }

                @Override
                public void onClick(View view) {
                        switch (view.getId())
                        {
                                case R.id.bRemove:
                                        if(sharedPreferenceData.getUserType().equals("admin"))
                                        {
                                                dialogClass.onSuccessListener(anInterface);
                                                dialogClass.warning("Do you want to cancel this request?");
                                                action = "cancel";
                                        }else dialogClass.error("Only admin can approved balance.you are not an admin..");
                                        break;
                                case R.id.bDetails:
                                        if(sharedPreferenceData.getUserType().equals("admin"))
                                        {
                                                dialogClass.onSuccessListener(anInterface);
                                                dialogClass.warning("Do you want to accept this request?");
                                                action = "accept";
                                        }else dialogClass.error("Only admin can approved balance.you are not an admin..");
                                        break;
                                case R.id.txtUserName:
                                        if(internetIsOn.isOnline())
                                        {
                                                Intent page = new Intent(context,MemberDetails.class);
                                                page.putExtra("userName",memberModelList.get(position).getUserName());
                                                context.startActivity(page);
                                        }else dialogClass.noInternetConnection();
                                        break;
                        }
                }


                //admin take action about join request.accept or cancel request
                private void requestAction(String userName,String action,String group)
                {
                        try {
                                String POST = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8")+"&"
                                        +URLEncoder.encode("action","UTF-8")+"="+URLEncoder.encode(action,"UTF-8")+"&"
                                        +URLEncoder.encode("group","UTF-8")+"="+URLEncoder.encode(group,"UTF-8");

                                backgroundTask = new DatabaseBackgroundTask(context);
                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                backgroundTask.execute(context.getResources().getString(R.string.actionRequest),POST);
                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }
                }


                private OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                        @Override
                        public void onResultSuccess(final String result) {
                                activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                                switch(result)
                                                {
                                                        case "accepted":
                                                                someMethod.progress("Working on it...","One request accepted.");
                                                                memberModelList.remove(position);
                                                                notifyItemRemoved(position);
                                                                notifyItemRangeChanged(position, memberModelList.size());
                                                                break;
                                                        case "deleted":
                                                                someMethod.progress("Working on it...","One Request canceled.");
                                                                memberModelList.remove(position);
                                                                notifyItemRemoved(position);
                                                                notifyItemRangeChanged(position, memberModelList.size());
                                                                break;
                                                        case "member":
                                                                dialogClass.alreadyMember("You are already member"+".Please leave from previous group and retry.");
                                                                break;
                                                        default:
                                                                dialogClass.error("Execution failed.Please try again.");
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
                                                        case "yes":
                                                                if(internetIsOn.isOnline())
                                                                {
                                                                        requestAction(model.getUserName(),action,model.getGroup());
                                                                }else dialogClass.noInternetConnection();

                                                                break;
                                                }
                                        }
                                });
                        }
                };
        }

}
