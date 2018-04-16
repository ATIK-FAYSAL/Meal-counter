package com.atik_faysal.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.MealClass;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.MealModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by USER on 3/20/2018.
 */

public class MealAdapter extends BaseAdapter
{
        private List<MealModel>mealList;
        private Context context;
        private SharedPreferenceData sharedPreferenceData;
        private AlertDialog alertDialog;
        private CheckInternetIsOn internetIsOn;
        private DatabaseBackgroundTask backgroundTask;
        private Activity activity;
        private AlertDialogClass dialogClass;

        public MealAdapter(Context context,List<MealModel>modelList)
        {
                this.context = context;
                this.mealList = modelList;
                this.sharedPreferenceData = new SharedPreferenceData(context);
                this.internetIsOn = new CheckInternetIsOn(context);
                this.activity = (Activity)context;
                this.dialogClass = new AlertDialogClass(context);
        }

        @Override
        public int getCount() {
                return mealList.size();
        }

        @Override
        public Object getItem(int position) {
                return mealList.get(position);
        }

        @Override
        public long getItemId(int position) {
                return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.meal_model, parent, false);
                TextView txtDate,txtName,txtBreakfast,txtDinner,txtLunch,txtTotal;
                ImageView imgEdit;

                txtDate = view.findViewById(R.id.txtDate);
                txtName = view.findViewById(R.id.txtName);
                txtBreakfast = view.findViewById(R.id.eBreakfast);
                txtDinner = view.findViewById(R.id.eDinner);
                txtLunch = view.findViewById(R.id.eLunch);
                txtTotal = view.findViewById(R.id.eTotal);
                imgEdit = view.findViewById(R.id.bEdit);


                txtDate.setText(mealList.get(position).getDate());
                txtName.setText(mealList.get(position).getName());
                txtBreakfast.setText(mealList.get(position).getBreakfast());
                txtDinner.setText(mealList.get(position).getDinner());
                txtLunch.setText(mealList.get(position).getLunch());
                txtTotal.setText(mealList.get(position).getTotal());

                imgEdit.setEnabled(false);
                imgEdit.setImageDrawable(null);

                if(sharedPreferenceData.getUserType().equals("admin"))
                {
                        imgEdit.setEnabled(true);
                        imgEdit.setBackgroundResource(R.drawable.edit);
                }

                imgEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                               editMeal(mealList.get(position).getName(),
                                       mealList.get(position).getDate(),
                                       mealList.get(position).getBreakfast(),
                                       mealList.get(position).getLunch(),
                                       mealList.get(position).getDinner());
                        }
                });

                return view;
        }


        private void editMeal(String userName,String date,String breakfast,String lunch,String dinner)
        {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.edit_meal,null);
                builder.setView(view);
                builder.setCancelable(false);

                final TextView txtName,txtDate;
                final EditText eBreakfast,eDinner,eLaunch;
                Button cancel,done;

                txtName = view.findViewById(R.id.txtName);
                txtDate = view.findViewById(R.id.txtDate);
                eBreakfast = view.findViewById(R.id.eBreakfast);
                eDinner = view.findViewById(R.id.eDinner);
                eLaunch = view.findViewById(R.id.eLunch);

                cancel = view.findViewById(R.id.bCancel);
                done = view.findViewById(R.id.bDone);

                txtName.setText(userName);
                txtDate.setText(date);
                eBreakfast.setText(breakfast);
                eDinner.setText(dinner);
                eLaunch.setText(lunch);

                alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();

                cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                alertDialog.dismiss();
                        }
                });

                done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                backgroundTask = new DatabaseBackgroundTask(context);
                                //String url = "http://192.168.56.1/editMeal.php";
                                String breakfast = eBreakfast.getText().toString();
                                String lunch = eLaunch.getText().toString();
                                String dinner = eDinner.getText().toString();

                                if(breakfast.isEmpty())
                                {
                                        eBreakfast.setError("Invalid");
                                        return;
                                }
                                if(lunch.isEmpty())
                                {
                                        eLaunch.setError("Invalid");
                                        return;
                                }
                                if(dinner.isEmpty())
                                {
                                        eDinner.setError("Invalid");
                                        return;
                                }

                                double totalMeal=0;

                                try {
                                        totalMeal = Double.parseDouble(breakfast)+Double.parseDouble(lunch)+Double.parseDouble(dinner);
                                }catch (NumberFormatException e)
                                {
                                        e.printStackTrace();
                                }

                                if(internetIsOn.isOnline())
                                {
                                        try {
                                                String data = URLEncoder.encode("breakfast","UTF-8")+"="+URLEncoder.encode(breakfast,"UTF-8")+"&"
                                                        +URLEncoder.encode("lunch","UTF-8")+"="+URLEncoder.encode(lunch,"UTF-8")+"&"
                                                        +URLEncoder.encode("dinner","UTF-8")+"="+URLEncoder.encode(dinner,"UTF-8")+"&"
                                                        +URLEncoder.encode("total","UTF-8")+"="+URLEncoder.encode(String.valueOf(totalMeal),"UTF-8")+"&"
                                                        +URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(txtName.getText().toString(),"UTF-8")+"&"
                                                        +URLEncoder.encode("date","UTF-8")+"="+URLEncoder.encode(txtDate.getText().toString(),"UTF-8");

                                                backgroundTask.setOnResultListener(onAsyncTaskInterface);
                                                backgroundTask.execute(context.getResources().getString(R.string.mealEdit),data);
                                        } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                        }
                                }
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
                                                case "success":
                                                        alertDialog.dismiss();
                                                        Toast.makeText(context, "Meal updated successfully.", Toast.LENGTH_SHORT).show();
                                                        context.startActivity(new Intent(context, MealClass.class));
                                                        activity.finish();
                                                        break;
                                                default:
                                                        alertDialog.dismiss();
                                                        dialogClass.error("Execution failed.please try again.");
                                                        break;
                                        }
                                }
                        });
                }
        };
}
