package com.atik_faysal.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.PostData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.Feedback;
import com.atik_faysal.mealcounter.MemberDetails;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.NoticeBoard;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.NoticeModel;
import com.atik_faysal.model.ShoppingItemModel;
import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by USER on 2/9/2018.
 */

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MyViewHolder>
{
        private List<NoticeModel>noticeModels;
        Context context;
        private LayoutInflater inflater;
        private Activity activity;
        private String userType;


        public NoticeAdapter(Context context,List<NoticeModel>models)
        {
                this.context = context;
                this.noticeModels = models;
                inflater = LayoutInflater.from(context);
                activity = (Activity)context;
                SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(context);
                userType = sharedPreferenceData.getUserType();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = inflater.inflate(R.layout.notice_model,parent,false);
                return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
                NoticeModel current = noticeModels.get(position);
                holder.setData(current,position);
                holder.setListeners();
        }

        @Override
        public int getItemCount() {
                return noticeModels.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {
                private ImageView bRemove;
                private int position;
                private NoticeModel model;
                private TextView txtUserName,txtDate,txtTitle,txtId;
                private ReadMoreTextView moreTextView;
                private AlertDialogClass dialogClass;
                private NeedSomeMethod someMethod;

                public MyViewHolder(View view) {
                        super(view);
                        txtUserName = view.findViewById(R.id.txtName);
                        txtDate = view.findViewById(R.id.txtDate);
                        txtTitle = view.findViewById(R.id.txtTitle);
                        txtId = view.findViewById(R.id.txtId);
                        moreTextView = view.findViewById(R.id.txtNotice);
                        bRemove = view.findViewById(R.id.bRemove);
                        dialogClass = new AlertDialogClass(context);
                        someMethod = new NeedSomeMethod(context);
                }

                @SuppressLint("SetTextI18n")
                public void setData(NoticeModel currentObject, int position) {
                        this.position = position;
                        this.model = currentObject;
                        txtUserName.setText(model.getUserName());
                        txtDate.setText(model.getDate());
                        txtTitle.setText(model.getTitle());
                        txtId.setText("#P"+model.getId());
                        moreTextView.setText(model.getNotice());

                        bRemove.setEnabled(false);
                        bRemove.setImageDrawable(null);

                        if(userType.equals("admin"))
                        {
                                bRemove.setEnabled(true);
                                bRemove.setBackgroundResource(R.drawable.icon_delete);
                        }
                }

                private void setListeners() {
                        bRemove.setOnClickListener(NoticeAdapter.MyViewHolder.this);
                        txtUserName.setOnClickListener(NoticeAdapter.MyViewHolder.this);
                }

                @Override
                public void onClick(View view) {
                        switch (view.getId())
                        {
                                case R.id.bRemove:
                                        removeNotice(model.getId());
                                        break;
                                case R.id.txtName:
                                        Intent page = new Intent(context,MemberDetails.class);
                                        page.putExtra("userName",model.getUserName());
                                        context.startActivity(page);
                                        break;
                        }
                }

                private void removeNotice(final String noticeId)
                {
                        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        iOSDialogBuilder builder = new iOSDialogBuilder(context);

                        builder.setTitle("Warning")
                                .setSubtitle("Want to remove this notice?")
                                .setBoldPositiveLabel(true)
                                .setCancelable(false)
                                .setPositiveListener("Yes",new iOSDialogClickListener() {
                                        @Override
                                        public void onClick(iOSDialog dialog) {
                                                String id = noticeId.substring(0,noticeId.length());

                                                //String file = "http://192.168.56.1/removeNotice.php";
                                                //String post;

                                                Map<String,String> map = new HashMap<>();
                                                map.put("id",id);
                                                PostData postData = new PostData(context,onAsyncTaskInterface);
                                                postData.InsertData(context.getResources().getString(R.string.removeNotice),map);
                                                dialog.dismiss();

                                        }
                                }).setNegativeListener("No", new iOSDialogClickListener() {
                                @Override
                                public void onClick(iOSDialog dialog) {
                                        dialog.dismiss();
                                }
                        }).build().show();
                }

                private OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                        @Override
                        public void onResultSuccess(final String message) {
                                activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                                if(message.equals("success"))
                                                {
                                                        someMethod.progress("Working on it....","One notice deleted");
                                                        noticeModels.remove(position);
                                                        notifyItemRemoved(position);
                                                        notifyItemRangeChanged(position, noticeModels.size());
                                                }else
                                                        dialogClass.error("Execution failed,please try again.");
                                        }
                                });
                        }
                };
        }
}