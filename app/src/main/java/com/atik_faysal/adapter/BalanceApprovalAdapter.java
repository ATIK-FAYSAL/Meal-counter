package com.atik_faysal.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.ApproveBalance;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.CostModel;
import com.atik_faysal.others.NoResultFound;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class BalanceApprovalAdapter extends BaseAdapter
{
        private Context context;
        private List<CostModel>modelList;
        private AlertDialogClass dialogClass;
        private DatabaseBackgroundTask backgroundTask;
        private CheckInternetIsOn internetIsOn;
        private NeedSomeMethod someMethod;
        private NoResultFound noResultFound;
        private SharedPreferenceData sharedPreferenceData;
        private Activity activity;
        private static String data;

        public BalanceApprovalAdapter(Context context,List<CostModel>modelList)
        {
                this.context = context;
                this.modelList = modelList;
                dialogClass = new AlertDialogClass(context);
                internetIsOn = new CheckInternetIsOn(context);
                someMethod = new NeedSomeMethod(context);
                noResultFound = new NoResultFound(context);
                sharedPreferenceData = new SharedPreferenceData(context);
                activity = (Activity)context;
        }

        @Override
        public int getCount() {
                return modelList.size();
        }

        @Override
        public Object getItem(int i) {
                return modelList.get(i);
        }

        @Override
        public long getItemId(int position) {
                return position;
        }

        @Override
        public View getView(final int i, View v, ViewGroup viewGroup)
        {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.approve_balance, viewGroup, false);
                TextView txtDate,txtName,txtStatus,txtId ;
                EditText txtTaka;
                Button bCancel,bApprv;

                txtName = view.findViewById(R.id.txtName);
                txtDate = view.findViewById(R.id.txtDate);
                txtStatus = view.findViewById(R.id.txtStatus);
                txtTaka = view.findViewById(R.id.txtTaka);
                txtId = view.findViewById(R.id.txtId);
                bCancel = view.findViewById(R.id.bCancel);
                bApprv = view.findViewById(R.id.bApprove);

                txtStatus.setText(modelList.get(i).getStatus());
                txtName.setText(modelList.get(i).getName());
                txtDate.setText(modelList.get(i).getDate());
                txtTaka.setText(modelList.get(i).getTaka());
                txtId.setText("#XY-10500"+modelList.get(i).getId());

                bCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                dialogClass.onSuccessListener(onAsyncTaskInterface);
                                dialogClass.warning("Do you want to remove this ?");
                                try {
                                        data = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(modelList.get(i).getId(),"UTF-8")+"&"
                                                +URLEncoder.encode("check","UTF-8")+"="+URLEncoder.encode("remove","UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                }
                        }
                });

                bApprv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                if(sharedPreferenceData.getUserType().equals("admin"))
                                {
                                        dialogClass.onSuccessListener(onAsyncTaskInterface);
                                        dialogClass.warning("Do you want to approve this ?");
                                        try {
                                                data = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(modelList.get(i).getId(),"UTF-8")+"&"
                                                        +URLEncoder.encode("check","UTF-8")+"="+URLEncoder.encode("approve","UTF-8")+"&"
                                                        +URLEncoder.encode("taka","UTF-8")+"="+URLEncoder.encode(modelList.get(i).getTaka(),"UTF-8")+"&"
                                                        +URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(modelList.get(i).getName(),"UTF-8");
                                        } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                        }
                                }else dialogClass.error("Only admin can approved balance.you are not an admin..");
                        }
                });

                return view;
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
                                                               backgroundTask = new DatabaseBackgroundTask(context);
                                                               backgroundTask.setOnResultListener(anInterface);
                                                               backgroundTask.execute(file,data);
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
                                                        someMethod.progressDialog("Working on it....");
                                                        //context.startActivity(new Intent(context, ApproveBalance.class));
                                                        noResultFound.checkValueIsExist(sharedPreferenceData.getCurrentUserName(),ApproveBalance.class,"approval");
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
