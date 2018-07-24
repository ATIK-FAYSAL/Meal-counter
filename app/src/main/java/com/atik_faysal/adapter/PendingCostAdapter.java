package com.atik_faysal.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.mealcounter.R;
import com.atik_faysal.model.CostModel;

import java.util.List;

public class PendingCostAdapter extends RecyclerView.Adapter<PendingCostAdapter.ViewHolder>
{

     private Context context;
     private List<CostModel>pendingCost;
     private Activity activity;
     private LayoutInflater inflater;

     public PendingCostAdapter(Context context,List<CostModel>models)
     {
          this.activity = (Activity)context;
          this.pendingCost = models;
          this.inflater = LayoutInflater.from(context);
     }

     @NonNull
     @Override
     public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
     {
          View view = inflater.inflate(R.layout.approve_balance,parent,false);
          return new ViewHolder(view);
     }

     @Override
     public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
          CostModel current = pendingCost.get(position);
          holder.setData(current,position);
          holder.setListener();
     }

     @Override
     public int getItemCount() {
          return pendingCost.size();
     }


     public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
     {

          private TextView txtId,txtStatus,txtDate,txtTaka,txtName;
          private Button bDone,bCancel;
          private CostModel costModel;
          private int position;

          public ViewHolder(View itemView) {
               super(itemView);
               bCancel = itemView.findViewById(R.id.bCancel);
               bDone = itemView.findViewById(R.id.bApprove);
               txtDate = itemView.findViewById(R.id.txtDate);
               txtId = itemView.findViewById(R.id.txtId);
               txtName = itemView.findViewById(R.id.txtName);
               txtTaka = itemView.findViewById(R.id.txtTaka);
               txtStatus = itemView.findViewById(R.id.txtStatus);
          }

          @Override
          public void onClick(View view) {
               bDone.setOnClickListener(PendingCostAdapter.ViewHolder.this);
               bCancel.setOnClickListener(PendingCostAdapter.ViewHolder.this);
          }

          protected void setData(CostModel object,int pos)
          {
               this.position = pos;
               this.costModel = object;
               txtId.setText(costModel.getId());
               txtStatus.setText(costModel.getStatus());
               txtName.setText(costModel.getName());
               txtTaka.setText(costModel.getTaka());
               txtDate.setText(costModel.getDate());
          }

          protected void setListener()
          {
               switch (itemView.getId())
               {
                    case R.id.bApprove:
                         Toast.makeText(context,"balance approve",Toast.LENGTH_LONG).show();
                         break;
                    case R.id.bCancel:
                         Toast.makeText(context,"balance cancel",Toast.LENGTH_LONG).show();
                         break;
               }
          }
     }

}
