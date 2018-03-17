package com.atik_faysal.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.AllMemberList;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.MemberDetails;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.MemberModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by USER on 2/1/2018.
 */

public class AdapterMemberList extends BaseAdapter
{

        private List<MemberModel>memberList;
        private Context context;

        private SharedPreferenceData sharedPreferenceData;
        private AlertDialogClass dialogClass;
        private DatabaseBackgroundTask backgroundTask;
        private CheckInternetIsOn internetIsOn;


        private View view;
        private TextView txtName,txtPhone,txtUserName,txtTaka,txtType,txtDate;
        private Button bRemove,bDetails;
        private Activity activity;

        private final static String URL = "http://192.168.56.1/remove_member.php";
        private final static String FILE_URL = "http://192.168.56.1/adminSetting.php";
        private static String DATA ;
        private String classType;
        private String currentUser;
        private String removeUserName;


        public AdapterMemberList(Context context,List<MemberModel>memberList)
        {
                this.context = context;
                activity = (Activity)context;
                this.memberList = memberList;
                sharedPreferenceData = new SharedPreferenceData(context);
                dialogClass = new AlertDialogClass(context);
                internetIsOn = new CheckInternetIsOn(context);

                this.classType = classType;
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

        @Override
        public View getView(final int position, final View convertView, ViewGroup parent)
        {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.member_model, parent, false);

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

                //onButtonClickListener(memberList.get(position).getUserName(),memberList.get(position).getType());

                //show all information about this user
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
                                removeUserName = memberList.get(position).getUserName();
                                readyToRemove(memberList.get(position).getUserName());
                        }
                });

                return view;
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
                                backgroundTask.execute(URL,DATA);
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
                                                        context.startActivity(new Intent(context, AllMemberList.class));
                                                        activity.finish();
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
