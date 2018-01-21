package com.atik_faysal.mealcounter;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by USER on 1/21/2018.
 */

public class AlertDialogClass extends Activity
{
        Context context;
        AlertDialog.Builder builder;
        AlertDialog alertDialog;
        public AlertDialogClass(Context context)
        {
                this.context = context;
                builder = new AlertDialog.Builder(context);
        }

        public void ifNoInternet()
        {
                builder.setTitle("Error");
                builder.setMessage("No internet ! Please check your Internet connection.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                        }
                });
                alertDialog = builder.create();
                alertDialog.show();

        }
}
