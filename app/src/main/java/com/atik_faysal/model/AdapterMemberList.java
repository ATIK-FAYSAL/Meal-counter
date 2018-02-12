package com.atik_faysal.model;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.atik_faysal.backend.InfoBackgroundTask;
import com.atik_faysal.backend.InfoBackgroundTask.OnAsyncTaskInterface;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.mealcounter.AdminPanel;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.AllMemberList;
import com.atik_faysal.mealcounter.MemberDetails;
import com.atik_faysal.mealcounter.R;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
        private InfoBackgroundTask backgroundTask;


        private View view;
        private TextView txtName,txtPhone,txtUserName,txtTaka,txtType,txtDate;
        private Button bRemove,bDetails;
        private Activity activity;

        private final static String URL = "http://192.168.56.1/remove_member.php";
        private final static String FILE_URL = "http://192.168.56.1/adminSetting.php";
        private final static String USER_INFO = "currentInfo";
        private static String DATA ;
        private String classType;
        private String currentUser;
        private String removeUserName;


        public AdapterMemberList(Context context,String classType,List<MemberModel>memberList)
        {
                this.context = context;
                activity = (Activity)context;
                this.memberList = memberList;
                sharedPreferenceData = new SharedPreferenceData(context);
                dialogClass = new AlertDialogClass(context);
                this.classType = classType;
                if(sharedPreferenceData.getCurrentUserName(USER_INFO)!=null)
                        currentUser = sharedPreferenceData.getCurrentUserName(USER_INFO);
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

                onButtonClickListener(memberList.get(position).getUserName(),memberList.get(position).getType());

                return view;
        }


        private void onButtonClickListener(final String userName,String type)
        {
                removeUserName = userName;

                bDetails.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Intent page = new Intent(context,MemberDetails.class);
                                page.putExtra("userName",userName);
                                context.startActivity(page);
                        }
                });


                switch (classType)
                {
                        case "memClass":

                                bRemove.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

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
                                });

                                break;

                        case "adminClass":

                                if(type.equals("member"))
                                {
                                        bRemove.setText("Make Admin");

                                        bRemove.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                        if(currentUser.equals(userName))
                                                                dialogClass.error("You can not remove your own membership.");
                                                        else
                                                                makeNewAdmin(userName);
                                                }
                                        });

                                }else if(type.equals("admin"))
                                {
                                        bRemove.setText("Remove Admin");

                                        bRemove.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                        if(currentUser.equals(userName))
                                                                dialogClass.error("You can not remove your own membership.");
                                                        else
                                                                removeAdmin(userName);
                                                }
                                        });
                                }

                                break;
                }

        }


        private void removeMember(String user)
        {
                try {
                        DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(user,"UTF-8");
                        backgroundTask = new InfoBackgroundTask(context);
                        backgroundTask.setOnResultListener(onAsyncTaskInterface);
                        backgroundTask.execute(URL,DATA);
                } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                }
        }

        private void makeNewAdmin(String userName)
        {

                String postData;
                try {
                        postData = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8")+"&"
                                +URLEncoder.encode("type","UTF-8")+"="+URLEncoder.encode("make","UTF-8");

                        backgroundTask = new InfoBackgroundTask(context);
                        backgroundTask.setOnResultListener(onAsyncTaskInterface);
                        backgroundTask.execute(FILE_URL,postData);
                } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                }
        }

        private void removeAdmin(String userName)
        {

                String postData;
                try {
                        postData = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8")+"&"
                                +URLEncoder.encode("type","UTF-8")+"="+URLEncoder.encode("remove","UTF-8");

                        backgroundTask = new InfoBackgroundTask(context);
                        backgroundTask.setOnResultListener(onAsyncTaskInterface);
                        backgroundTask.execute(FILE_URL,postData);
                } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                }
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

                                                case "failed"://remove or make admin
                                                        dialogClass.error("Failed to execute operation.Please retry after sometimes");
                                                        break;

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
