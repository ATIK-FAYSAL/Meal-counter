package com.atik_faysal.adapter;

import android.app.Activity;
import android.app.AlertDialog;
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
import com.atik_faysal.mealcounter.AdminPanel;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.MemberDetails;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.MemberModel;

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

        private SharedPreferenceData sharedPreferenceData;
        private AlertDialogClass dialogClass;
        private DatabaseBackgroundTask backgroundTask;
        private CheckInternetIsOn internetIsOn;


        private View view;
        private TextView txtName,txtPhone,txtUserName,txtTaka,txtType,txtDate;
        private Button bRemove,bDetails;
        private Activity activity;

        private final static String FILE_URL = "http://192.168.56.1/adminSetting.php";
        private final static String USER_INFO = "currentInfo";
        private String classType;
        private String currentUser;

        public AdminAdapter(Context context,List<MemberModel>memberList)
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
        public View getView(final int position, View convertView, ViewGroup parent) {

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
                                backgroundTask.execute(FILE_URL,postData);
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
                                backgroundTask.execute(FILE_URL,postData);
                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }
                }else dialogClass.noInternetConnection();
        }

        //warning
        public void warning(final String userName, final String type)
        {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_warning,null);

                builder.setView(view);
                builder.setCancelable(false);

                final Button bYes,bNo;
                TextView txtWarning;

                bYes = view.findViewById(R.id.bYes);
                bNo = view.findViewById(R.id.bNo);
                txtWarning = view.findViewById(R.id.text);

                if(type.equals("member"))
                        txtWarning.setText("Want to make admin ?");
                else
                        txtWarning.setText("Want to remove admin ?");

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                bYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                if(type.equals("member"))
                                        makeNewAdmin(userName);
                                else if(type.equals("admin")) {
                                        if (currentUser.equals(userName))
                                                dialogClass.error("You can not remove your own membership.");
                                        else
                                                removeAdmin(userName);
                                }
                                alertDialog.dismiss();
                        }
                });

                bNo.setOnClickListener(new View.OnClickListener() {
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
