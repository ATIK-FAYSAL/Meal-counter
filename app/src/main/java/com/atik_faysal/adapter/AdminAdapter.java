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
import android.widget.Toast;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.PostData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.mealcounter.AdminPanel;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.MemberDetails;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.MemberModel;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by USER on 2/21/2018.
 */

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.ViewHolder>
{
        private List<MemberModel> memberList;
        private Context context;
        private Activity activity;
        private LayoutInflater inflater;

        //private final static String FILE_URL = "http://192.168.56.1/adminSetting.php";
        private String currentUser;

        public AdminAdapter(Context context,List<MemberModel>models)
        {
                this.context = context;
                this.memberList = models;
                this.activity = (Activity)context;
                this.inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = inflater.inflate(R.layout.member_model,parent,false);
                return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position)
        {
                MemberModel current = memberList.get(position);
                holder.setData(current,position);
                holder.setListener();

        }

        @Override
        public int getItemCount() {
                return memberList.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {

                private AlertDialogClass dialogClass;
                private DatabaseBackgroundTask backgroundTask;
                private CheckInternetIsOn internetIsOn;
                private NeedSomeMethod someMethod;
                private MemberModel model;
                private SharedPreferenceData sharedPreferenceData;
                private int position;
                private TextView txtName,txtPhone,txtUserName,txtTaka,txtType,txtDate;
                private ImageView bRemove;
                private String action,string;


                @SuppressLint("SetTextI18n")
                public ViewHolder(View view) {
                        super(view);
                        internetIsOn = new CheckInternetIsOn(context);
                        dialogClass = new AlertDialogClass(context);
                        sharedPreferenceData = new SharedPreferenceData(context);
                        someMethod = new NeedSomeMethod(context);
                        txtName = view.findViewById(R.id.txtName);
                        txtUserName = view.findViewById(R.id.txtUserName);
                        txtPhone = view.findViewById(R.id.txtPhone);
                        txtDate = view.findViewById(R.id.txtDate);
                        txtType = view.findViewById(R.id.txtType);
                        txtTaka = view.findViewById(R.id.txtTaka);
                        txtTaka.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_taka, 0, 0, 0);

                        currentUser = sharedPreferenceData.getCurrentUserName();

                        bRemove = view.findViewById(R.id.bRemove);
                        //bDetails = view.findViewById(R.id.bDetails);
                        //bDetails.setText("Details");
                }


                @SuppressLint("SetTextI18n")
                private void setData(MemberModel current, int pos)
                {
                        this.model = current;
                        this.position = pos;

                        txtName.setText(model.getName());
                        txtUserName.setText(model.getUserName());
                        txtPhone.setText(model.getPhone());
                        txtTaka.setText(model.getTaka());
                        txtType.setText(model.getType());
                        txtDate.setText(model.getDate());

                        /*if(memberList.get(position).getType().equals("member"))
                                bRemove.setText("Make admin");
                        else
                                bRemove.setText("Remove admin");*/

                        if(sharedPreferenceData.getUserType().equals("admin"))
                        {
                                if(model.getType().equals("admin"))
                                        string = "remove";
                                else
                                {
                                        string = "make";
                                        bRemove.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_admin));
                                }
                        }else
                        {
                                bRemove.setImageDrawable(null);
                                bRemove.setEnabled(false);
                        }
                }

                private void setListener()
                {
                        bRemove.setOnClickListener(AdminAdapter.ViewHolder.this);
                        //bDetails.setOnClickListener(AdminAdapter.ViewHolder.this);
                        txtUserName.setOnClickListener(AdminAdapter.ViewHolder.this);
                }

                @Override
                public void onClick(View view) {
                        switch (view.getId())
                        {
                                case R.id.bRemove:
                                        if(sharedPreferenceData.getUserType().equals("admin"))
                                        {
                                                dialogClass.onSuccessListener(anInterface);
                                                dialogClass.warning("Do you want to "+string+" this admin?");
                                        }else dialogClass.error("Only admin can remove or make new admin.you are not an admin..");
                                        break;
                                case R.id.bDetails:
                                        if(internetIsOn.isOnline())
                                        {
                                               Intent page = new Intent(context,MemberDetails.class);
                                               page.putExtra("userName",model.getUserName());
                                               context.startActivity(page);
                                        }else dialogClass.noInternetConnection();
                                        break;
                                case R.id.txtUserName:
                                        if(internetIsOn.isOnline())
                                        {
                                                Intent page = new Intent(context,MemberDetails.class);
                                                page.putExtra("userName",memberList.get(position).getUserName());
                                                context.startActivity(page);
                                        }else dialogClass.noInternetConnection();
                                        break;
                        }
                }



                //make new admin
                private void adminAction(String userName)
                {
                        //String postData;
                        if (internetIsOn.isOnline())
                        {
                                Map<String,String> map = new HashMap<>();
                                map.put("userName",userName);
                                map.put("type",action);
                                PostData postData = new PostData(context,onAsyncTaskInterface);
                                postData.InsertData(context.getResources().getString(R.string.adminSetting),map);
                                /*try {
                                        postData = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8")+"&"
                                                +URLEncoder.encode("type","UTF-8")+"="+URLEncoder.encode(action,"UTF-8");

                                        backgroundTask = new DatabaseBackgroundTask(context);
                                        backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                        backgroundTask.execute(context.getResources().getString(R.string.adminSetting),postData);
                                } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                }*/
                        }else dialogClass.noInternetConnection();
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
                                                                someMethod.progress("Working on it....","user type change,please reload the current page.");
                                                                break;

                                                        default://remove or make admin
                                                                dialogClass.error("Failed to execute operation.Please try again.");
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
                                                                        if(model.getType().equals("member"))
                                                                        {
                                                                                action = "make";
                                                                                adminAction(model.getUserName());
                                                                        }
                                                                        else if(model.getType().equals("admin")) {
                                                                                action = "remove";
                                                                                if (currentUser.equals(model.getUserName()))
                                                                                        dialogClass.error("You can not remove your own membership.");
                                                                                else
                                                                                        adminAction(model.getUserName());
                                                                        }
                                                                }else dialogClass.noInternetConnection();

                                                                break;
                                                }
                                        }
                                });
                        }
                };
        }

}
