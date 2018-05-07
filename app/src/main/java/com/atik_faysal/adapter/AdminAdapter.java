package com.atik_faysal.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.mealcounter.AdminPanel;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.MemberDetails;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.MemberModel;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by USER on 2/21/2018.
 */

public class AdminAdapter extends BaseAdapter
{
        private List<MemberModel> memberList;
        private Context context;

        private AlertDialogClass dialogClass;
        private DatabaseBackgroundTask backgroundTask;
        private CheckInternetIsOn internetIsOn;
        private Activity activity;

        //private final static String FILE_URL = "http://192.168.56.1/adminSetting.php";
        private String currentUser;

        public AdminAdapter(Context context,List<MemberModel>memberList)
        {
                this.context = context;
                activity = (Activity)context;
                this.memberList = memberList;
                SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(context);
                dialogClass = new AlertDialogClass(context);
                internetIsOn = new CheckInternetIsOn(context);

                if(sharedPreferenceData.getCurrentUserName()!=null)
                        currentUser = sharedPreferenceData.getCurrentUserName();
                else Toast.makeText(context,"under construction",Toast.LENGTH_SHORT).show();
        }

        @Override
        public int getCount() {
                return memberList.size();
        }

        @Override
        public Object getItem(int position) {
                return memberList.get(position);
        }

        @Override
        public long getItemId(int position) {
                return position;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.member_model, parent, false);
                TextView txtName,txtPhone,txtUserName,txtTaka,txtType,txtDate;
                Button bRemove,bDetails;


                txtName = view.findViewById(R.id.txtName);
                txtUserName = view.findViewById(R.id.txtUserName);
                txtPhone = view.findViewById(R.id.txtPhone);
                txtDate = view.findViewById(R.id.txtDate);
                txtType = view.findViewById(R.id.txtType);
                txtTaka = view.findViewById(R.id.txtTaka);
                txtTaka.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_taka, 0, 0, 0);

                bRemove = view.findViewById(R.id.bRemove);
                bDetails = view.findViewById(R.id.bDetails);

                txtName.setText(memberList.get(position).getName());
                txtUserName.setText(memberList.get(position).getUserName());
                txtPhone.setText(memberList.get(position).getPhone());
                txtTaka.setText(memberList.get(position).getTaka());
                txtType.setText(memberList.get(position).getType());
                txtDate.setText(memberList.get(position).getDate());

                if(memberList.get(position).getType().equals("member"))
                        bRemove.setText("Make admin");
                else
                        bRemove.setText("Remove admin");

                bDetails.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Intent page = new Intent(context,MemberDetails.class);
                                page.putExtra("userName",memberList.get(position).getUserName());
                                context.startActivity(page);
                        }
                });

                bRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                warning(memberList.get(position).getUserName(),memberList.get(position).getType());
                        }
                });

                return view;
        }


        //make new admin
        private void makeNewAdmin(String userName)
        {

                String postData;
                if (internetIsOn.isOnline())
                {
                        try {
                                postData = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8")+"&"
                                        +URLEncoder.encode("type","UTF-8")+"="+URLEncoder.encode("make","UTF-8");

                                backgroundTask = new DatabaseBackgroundTask(context);
                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                backgroundTask.execute(context.getResources().getString(R.string.adminSetting),postData);
                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }
                }else dialogClass.noInternetConnection();
        }

        //remove admin
        private void removeAdmin(String userName)
        {

                String postData;
                if(internetIsOn.isOnline())
                {
                        try {
                                postData = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8")+"&"
                                        +URLEncoder.encode("type","UTF-8")+"="+URLEncoder.encode("remove","UTF-8");

                                backgroundTask = new DatabaseBackgroundTask(context);
                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                backgroundTask.execute(context.getResources().getString(R.string.adminSetting),postData);
                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }
                }else dialogClass.noInternetConnection();
        }

        //warning
        public void warning(final String userName, final String type)
        {
                String message;
                if(type.equals("member"))
                        message = "Want to make admin ?";
                else
                        message = "Want to remove admin ?";

                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                iOSDialogBuilder builder = new iOSDialogBuilder(context);

                builder.setTitle("Warning")
                        .setSubtitle(message)
                        .setBoldPositiveLabel(true)
                        .setCancelable(false)
                        .setPositiveListener("Yes",new iOSDialogClickListener() {
                                @Override
                                public void onClick(iOSDialog dialog) {
                                        if(type.equals("member"))
                                                makeNewAdmin(userName);
                                        else if(type.equals("admin")) {
                                                if (currentUser.equals(userName))
                                                        dialogClass.error("You can not remove your own membership.");
                                                else
                                                        removeAdmin(userName);
                                        }
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
                                        switch (message)
                                        {
                                                case "success"://remove or make admin
                                                        context.startActivity(new Intent(context, AdminPanel.class));
                                                        activity.finish();
                                                        break;

                                                default://remove or make admin
                                                        dialogClass.error("Failed to execute operation.Please try again.");
                                                        break;

                                        }
                                }
                        });
                }
        };

}
