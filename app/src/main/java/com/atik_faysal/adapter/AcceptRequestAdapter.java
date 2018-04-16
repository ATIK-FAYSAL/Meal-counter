package com.atik_faysal.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.MemberModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by USER on 2/11/2018.
 */

public class AcceptRequestAdapter extends BaseAdapter
{

        Context context;
        Activity activity;
        List<MemberModel>memberModelList;
        View view;
        TextView txtUserName,txtName,txtPhone,txtDate,txtStatus,txtId;
        Button bAccept,bCancel;

        private DatabaseBackgroundTask backgroundTask;
        private AlertDialogClass dialogClass;
        private CheckInternetIsOn internetIsOn;

        //static final String FILE_URL = "http://192.168.56.1/requestsAction.php";
        static String POST;

        public AcceptRequestAdapter(Context context,List<MemberModel>memberModels)
        {
                this.context = context;
                activity = (Activity)context;
                this.memberModelList = memberModels;
                dialogClass = new AlertDialogClass(context);
                internetIsOn = new CheckInternetIsOn(context);
        }


        @Override
        public int getCount() {
                return memberModelList.size();
        }

        @Override
        public Object getItem(int position) {
                return memberModelList.get(position);
        }

        @Override
        public long getItemId(int position) {
                return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.member_model, parent, false);

                txtName = view.findViewById(R.id.txtName);
                txtUserName = view.findViewById(R.id.txtName);
                txtPhone = view.findViewById(R.id.txtPhone);
                txtId = view.findViewById(R.id.txtType);
                txtStatus = view.findViewById(R.id.txtTaka);
                txtDate = view.findViewById(R.id.txtDate);

                txtName.setText(memberModelList.get(position).getName());
                txtUserName.setText(memberModelList.get(position).getUserName());
                txtPhone.setText(memberModelList.get(position).getPhone());
                txtId.setText(memberModelList.get(position).getType());
                txtStatus.setText(memberModelList.get(position).getTaka());
                txtDate.setText(memberModelList.get(position).getDate());

                bAccept = view.findViewById(R.id.bRemove);
                bCancel = view.findViewById(R.id.bDetails);

                bAccept.setText("Accept");
                bCancel.setText("Cancel");
                bAccept.setTextColor(ContextCompat.getColor(context, R.color.green));
                bCancel.setTextColor(ContextCompat.getColor(context, R.color.red));

                bAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                if(internetIsOn.isOnline())
                                        requestAction(memberModelList.get(position).getUserName(),"accept",memberModelList.get(position).getGroup());
                                else dialogClass.noInternetConnection();
                        }
                });

                bCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                if(internetIsOn.isOnline())
                                        requestAction(memberModelList.get(position).getUserName(),"cancel",memberModelList.get(position).getGroup());
                                else dialogClass.noInternetConnection();
                        }
                });

                return view;
        }


        //admin take action about join request.accept or cancel request
        private void requestAction(String userName,String action,String group)
        {
                try {
                        POST = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8")+"&"
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
                                        Toast.makeText(context,"result : "+result,Toast.LENGTH_SHORT).show();
                                        switch(result)
                                        {
                                                case "accepted":
                                                        Toast.makeText(context,"Request accepted.Please reload your current page.",Toast.LENGTH_SHORT).show();
                                                        break;
                                                case "deleted":
                                                        Toast.makeText(context,"Request canceled.Please reload your current page.",Toast.LENGTH_SHORT).show();
                                                        break;
                                                case "member":
                                                        dialogClass.alreadyMember("You are already member.");
                                                        break;
                                                default:
                                                        dialogClass.error("Execution failed.Please try again.");
                                                        break;
                                        }
                                }
                        });
                }
        };

}
