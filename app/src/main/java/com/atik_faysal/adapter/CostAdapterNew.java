package com.atik_faysal.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.backend.PostData;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.CheckInternetIsOn;
import com.atik_faysal.mealcounter.MemberDetails;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.CostModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CostAdapterNew extends RecyclerView.Adapter<CostAdapterNew.ViewHolder>
{

     private List<CostModel>costModels;
     private Context context;
     private LayoutInflater inflater;
     private Activity activity;
     private SharedPreferenceData sharedPreferenceData;

     public CostAdapterNew(Context context,List<CostModel> list)
     {
          this.costModels = list;
          this.context = context;
          activity = (Activity)context;
          this.inflater = LayoutInflater.from(context);
          sharedPreferenceData = new SharedPreferenceData(context);
     }

     @NonNull
     @Override
     public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
     {
          View view = inflater.inflate(R.layout.cost_model,parent,false);
          return new ViewHolder(view);
     }

     @Override
     public void onBindViewHolder(@NonNull ViewHolder holder, int position)
     {
          CostModel current = costModels.get(position);
          holder.setData(current,position);
          holder.onClickListener();
     }

     @Override
     public int getItemCount() {
          return costModels.size();
     }

     protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
     {
          private AlertDialogClass dialogClass;
          private CheckInternetIsOn internetIsOn;
          private NeedSomeMethod someMethod;
          private CostModel model;
          private int position;
          private AlertDialog alertDialog;

          TextView txtName,txtDate,txtTaka,txtId,txtStatus;
          ImageView bEdit,bDone,bCancel;

          public ViewHolder(View view) {
               super(view);
               dialogClass = new AlertDialogClass(context);
               internetIsOn = new CheckInternetIsOn(context);
               someMethod = new NeedSomeMethod(context);

               txtName = view.findViewById(R.id.txtName);
               txtDate = view.findViewById(R.id.txtDate);
               txtTaka = view.findViewById(R.id.txtTaka);
               txtId = view.findViewById(R.id.txtId);
               bEdit = view.findViewById(R.id.bEdit);
               txtStatus = view.findViewById(R.id.txtStatus);
               bDone = view.findViewById(R.id.imgDone);
               bCancel = view.findViewById(R.id.imgCancel);
          }

          @SuppressLint("SetTextI18n")
          private void setData(CostModel costModel, int pos)
          {
               this.model = costModel;
               this.position = pos;
               txtName.setText(costModel.getName());
               txtId.setText("ID-10005"+costModel.getId());
               txtDate.setText(costModel.getDate());
               txtTaka.setText(costModel.getTaka());
               if(costModel.getStatus().equals("pending"))
                    txtStatus.setText(costModel.getStatus());

               bEdit.setEnabled(false);
               bEdit.setImageDrawable(null);
               bCancel.setEnabled(false);
               bDone.setEnabled(false);
               bDone.setImageDrawable(null);
               bCancel.setImageDrawable(null);


               if(sharedPreferenceData.getUserType().equals("admin")&&
                    (sharedPreferenceData.getMyGroupType().equals("close")||sharedPreferenceData.getMyGroupType().equals("secret")
                         ||sharedPreferenceData.getMyGroupType().equals("public")))
               {
                    bEdit.setEnabled(true);
                    bEdit.setBackgroundResource(R.drawable.icon_edit_blue);
                    if(model.getStatus().equals("pending"))
                    {
                         bCancel.setEnabled(true);
                         bDone.setEnabled(true);
                         bDone.setBackgroundResource(R.drawable.icon_done);
                         bCancel.setBackgroundResource(R.drawable.icon_delete);
                    }
               }else if((sharedPreferenceData.getMyGroupType().equals("public"))&&
                    (sharedPreferenceData.getUserType().equals("member")))
               {
                    if(sharedPreferenceData.getCurrentUserName().equals(costModel.getName())) {
                         bEdit.setEnabled(true);
                         bEdit.setBackgroundResource(R.drawable.icon_edit_blue);
                    }
               }
          }

          private void onClickListener()
          {
               bDone.setOnClickListener(CostAdapterNew.ViewHolder.this);
               bCancel.setOnClickListener(CostAdapterNew.ViewHolder.this);
               bEdit.setOnClickListener(CostAdapterNew.ViewHolder.this);
               txtName.setOnClickListener(CostAdapterNew.ViewHolder.this);
          }

          @Override
          public void onClick(View view) {
               switch (view.getId())
               {
                    case R.id.bEdit:
                         editShoppingCost(model.getId(),model.getDate(),model.getTaka());
                         break;
                    case R.id.txtName:
                         Intent page = new Intent(context,MemberDetails.class);
                         page.putExtra("userName",model.getName());
                         context.startActivity(page);
                         break;
                    case R.id.imgCancel:
                         modifyCost("cancel",model.getId());
                         break;
                    case R.id.imgDone:
                         modifyCost("accept",model.getId());
                         break;
               }
          }

          private void modifyCost(String action,String id)
          {
               PostData postData = new PostData(context,asyncTaskInterface);
               Map<String,String>maps = new HashMap<>();
               maps.put("action",action);
               maps.put("id",id);
               postData.InsertData(context.getResources().getString(R.string.shoppingCost),maps);
          }

          //it will show an alertDialog and you can icon_edit_blue shopping cost from here
          @SuppressLint("SetTextI18n")
          private void editShoppingCost(final String id, final String date, String taka)
          {
               AlertDialog.Builder builder = new AlertDialog.Builder(context);
               View view = LayoutInflater.from(context).inflate(R.layout.edit_cost,null);
               builder.setView(view);
               builder.setCancelable(false);

               final TextView txtDate,txtId;
               final EditText txtTaka;
               Button cancel,done;

               txtDate = view.findViewById(R.id.txtDate);
               txtId = view.findViewById(R.id.txtId);
               txtTaka = view.findViewById(R.id.txtTaka);

               txtId.setText("#ID-10005"+id);
               txtDate.setText(date);
               txtTaka.setText(taka);

               cancel = view.findViewById(R.id.bCancel);
               done = view.findViewById(R.id.bDone);

               alertDialog = builder.create();
               alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
               alertDialog.show();

               done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         //String url = "http://192.168.56.1/costEdit.php";
                         String taka = txtTaka.getText().toString();
                         if(TextUtils.isEmpty(taka))
                         {
                              txtTaka.setError("Invalid taka");
                              return;
                         }

                         if (internetIsOn.isOnline())
                         {
                              Map<String,String> map = new HashMap<>();
                              map.put("id",id);
                              map.put("taka",taka);
                              PostData postData = new PostData(context,onAsyncTaskInterface);
                              postData.InsertData(context.getResources().getString(R.string.costEdit),map);
                         }else dialogClass.noInternetConnection();
                    }
               });

               cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         alertDialog.dismiss();
                    }
               });
          }

          //edit cost
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
                                        someMethod.progress("Working on it....","Cost updated successfully,please reload this page.");
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

          private OnAsyncTaskInterface asyncTaskInterface = new OnAsyncTaskInterface() {
               @Override
               public void onResultSuccess(final String message) {
                    activity.runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                              if(message.equals("dSuccess"))
                              {
                                   someMethod.progress("Working on it....","Cost deleted successfully.");
                                   costModels.remove(position);
                                   notifyItemRemoved(position);
                                   notifyItemRangeChanged(position, costModels.size());
                              }
                              else if(message.equals("aSuccess"))
                                   someMethod.progress("Working on it....","Cost added successfully,please reload this page.");
                              else dialogClass.error("Execution failed,please try again.");
                         }
                    });
               }
          };

     }

}
