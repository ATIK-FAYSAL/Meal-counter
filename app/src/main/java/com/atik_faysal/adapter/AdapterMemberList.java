package com.atik_faysal.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.AllMemberList;
import com.atik_faysal.mealcounter.ApproveBalance;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.MemberDetails;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.MemberModel;
import com.atik_faysal.others.NoResultFound;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by USER on 2/1/2018.
 */

public class AdapterMemberList extends RecyclerView.Adapter<AdapterMemberList.ViewHolder>
{

        private List<MemberModel>memberList;
        private Context context;
        private Activity activity;
        private LayoutInflater inflater;

        //private final static String URL = "http://192.168.56.1/remove_member.php";
        //private final static String FILE_URL = "http://192.168.56.1/adminSetting.php";
        private static String DATA ;
        private String classType;
        private String currentUser;


        public AdapterMemberList(Context context,List<MemberModel>models)
        {
                this.context = context;
                this.activity = (Activity)context;
                this.memberList = models;
                this.inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = inflater.inflate(R.layout.member_model,parent,false);
                return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

                private TextView txtName,txtPhone,txtUserName,txtTaka,txtType,txtDate;
                private ImageView bRemove;
                private SharedPreferenceData sharedPreferenceData;
                private AlertDialogClass dialogClass;
                private DatabaseBackgroundTask backgroundTask;
                private CheckInternetIsOn internetIsOn;
                private int position;
                private String removeUserName;
                private NeedSomeMethod someMethod;
                private NoResultFound noResultFound;

                @SuppressLint("SetTextI18n")
                public ViewHolder(View view) {
                        super(view);

                        sharedPreferenceData = new SharedPreferenceData(context);
                        internetIsOn = new CheckInternetIsOn(context);
                        dialogClass = new AlertDialogClass(context);
                        someMethod = new NeedSomeMethod(context);
                        noResultFound = new NoResultFound(context);


                        txtName = view.findViewById(R.id.txtName);
                        txtUserName = view.findViewById(R.id.txtUserName);
                        txtPhone = view.findViewById(R.id.txtPhone);
                        txtDate = view.findViewById(R.id.txtDate);
                        txtType = view.findViewById(R.id.txtType);
                        txtTaka = view.findViewById(R.id.txtTaka);
                        txtTaka.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_taka, 0, 0, 0);

                        bRemove = view.findViewById(R.id.bRemove);
                        //bDetails = view.findViewById(R.id.bDetails);
                        //bDetails.setText("Details");
                        currentUser = sharedPreferenceData.getCurrentUserName();

                        if(!sharedPreferenceData.getUserType().equals("admin"))
                        {
                                bRemove.setImageDrawable(null);
                                bRemove.setEnabled(false);
                        }
                }

                private void setData(MemberModel model,int pos)
                {
                        this.position = pos;

                        txtName.setText(model.getName());
                        txtUserName.setText(model.getUserName());
                        txtPhone.setText(model.getPhone());
                        txtTaka.setText(model.getTaka());
                        txtType.setText(model.getType());
                        txtDate.setText(model.getDate());
                }

                private void setListener()
                {
                        bRemove.setOnClickListener(AdapterMemberList.ViewHolder.this);
                        //bDetails.setOnClickListener(AdapterMemberList.ViewHolder.this);
                        txtUserName.setOnClickListener(AdapterMemberList.ViewHolder.this);
                }

                @Override
                public void onClick(View view) {
                        switch (view.getId())
                        {
                                case R.id.bRemove:
                                        removeUserName = memberList.get(position).getUserName();
                                        readyToRemove(memberList.get(position).getUserName());
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


                //member remove warning
                private void readyToRemove(String userName)
                {
                        if(currentUser.equals(userName))
                                dialogClass.error("You can not remove your own membership.");
                        else
                        {
                                if(sharedPreferenceData.getUserType().equals("admin"))
                                {
                                        dialogClass.onSuccessListener(taskInterface);
                                        dialogClass.warning("Really want to remove this member ?");
                                }
                                else dialogClass.error("Only admin can remove member.You are not an admin.");
                        }
                }

                //remove user
                private void removeMember(String user)
                {
                        if (internetIsOn.isOnline())
                        {
                                try {
                                        DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(user,"UTF-8");
                                        backgroundTask = new DatabaseBackgroundTask(context);
                                        backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                        backgroundTask.execute(context.getResources().getString(R.string.removeMember),DATA);
                                } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                }
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
                                                        case "error":
                                                                dialogClass.error("Failed to execute operation.Please retry after sometimes");
                                                                break;

                                                        case "successful":
                                                                someMethod.progress("Working on it....","One member removed.");
                                                                memberList.remove(position);
                                                                notifyItemRemoved(position);
                                                                notifyItemRangeChanged(position, memberList.size());
                                                                if(memberList.size()==0)
                                                                {
                                                                        noResultFound.checkValueIsExist(sharedPreferenceData.getCurrentUserName(),ApproveBalance.class,"member");
                                                                }
                                                                break;
                                                }
                                        }
                                });
                        }
                };


                private OnAsyncTaskInterface taskInterface = new OnAsyncTaskInterface() {
                        @Override
                        public void onResultSuccess(final String result) {
                                activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                                switch (result)
                                                {
                                                        case "yes":
                                                                removeMember(removeUserName);
                                                                break;

                                                }
                                        }
                                });
                        }
                };
        }


}
