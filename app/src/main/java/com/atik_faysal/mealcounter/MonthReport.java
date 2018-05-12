package com.atik_faysal.mealcounter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.adapter.ReportAdapter;
import com.atik_faysal.backend.DatabaseBackgroundTask;
import com.atik_faysal.backend.SharedPreferenceData;
import com.atik_faysal.interfaces.OnAsyncTaskInterface;
import com.atik_faysal.model.CostModel;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownServiceException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MonthReport extends AppCompatActivity
{
        private TextView txtTotalTaka;
        private TextView txtTotalCost;
        private TextView txtTotalMeal;
        private TextView txtMealRate;
        private TextView txtRemaining;
        private ListView listView;

        private SharedPreferenceData sharedPreferenceData;
        private NeedSomeMethod someMethod;
        private SimpleDateFormat format;
        private DatabaseBackgroundTask backgroundTask;
        private AlertDialogClass dialogClass;
        private CheckInternetIsOn internetIsOn;
        private List<CostModel>modelList;

        private String monthlyTaka ,monthlyCost,monthlyMeal,remain,mealRate;
        private String reportPath;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.report_list);
                initComponent();
                setToolbar();
        }


        //initialize all component
        @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
        private void initComponent()
        {
                //initialize all design component
                txtTotalTaka = findViewById(R.id.totalTaka);
                txtTotalCost = findViewById(R.id.totalCost);
                txtTotalMeal = findViewById(R.id.totalMeal);
                txtMealRate = findViewById(R.id.mealRate);
                txtRemaining = findViewById(R.id.remaining);
                TextView txtMonth = findViewById(R.id.txtMonth);
                listView = findViewById(R.id.list);


                sharedPreferenceData = new SharedPreferenceData(this);
                internetIsOn = new CheckInternetIsOn(this);
                someMethod = new NeedSomeMethod(this);
                backgroundTask = new DatabaseBackgroundTask(this);
                dialogClass = new AlertDialogClass(this);
                format = new SimpleDateFormat("MMMM-dd-yyyy");
                modelList = new ArrayList<>();
                txtMonth.setText("#"+sharedPreferenceData.getmyCurrentSession());

                if(internetIsOn.isOnline())
                {
                        try {
                                String data = URLEncoder.encode("group","UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getMyGroupName(),"UTF-8")+"&"
                                        +URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getCurrentUserName(),"UTF-8");
                                backgroundTask.setOnResultListener(asyncTaskInterface);
                                backgroundTask.execute(getResources().getString(R.string.report),data);

                        } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                        }
                }else dialogClass.noInternetConnection();
        }

        //set a toolbar,above the page
        protected void setToolbar()
        {
                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                toolbar.setTitleTextColor(getResources().getColor(R.color.white));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                toolbar.setNavigationIcon(R.drawable.icon_back);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                finish();
                        }
                });
        }

        //process json data ,set in listview
        @SuppressLint("SetTextI18n")
        private void processJsonData(String report)
        {
                try {
                        JSONObject mainObject = new JSONObject(report);
                        JSONArray reportArray = mainObject.optJSONArray("report");

                        JSONObject jsonIndex = reportArray.getJSONObject(0);
                        JSONArray detailArray = jsonIndex.getJSONArray("detail");
                        JSONObject detailObject = detailArray.getJSONObject(0);

                        if(!detailObject.getString("monthlyTaka").equals("0"))
                                monthlyTaka = detailObject.getString("monthlyTaka");
                        else monthlyTaka = "0";

                        if(!detailObject.getString("monthlyCost").equals("0"))
                                monthlyCost = detailObject.getString("monthlyCost");
                        else monthlyCost = "0";

                        if(!detailObject.getString("monthlyMeal").equals("0"))
                                monthlyMeal = detailObject.getString("monthlyMeal");
                        else monthlyMeal = "0";

                        if(!detailObject.getString("remaining").equals("0"))
                                remain = detailObject.getString("remaining");
                        else remain = "0";

                        if(!detailObject.getString("mealRate").equals("0"))
                                mealRate = detailObject.getString("mealRate");
                        else mealRate = "0";

                        String name,meal,taka,cost,status;

                        for(int i=1;i<reportArray.length();i++)
                        {
                                int count=0;
                                JSONObject index = reportArray.getJSONObject(i);
                                JSONArray infoArray = index.getJSONArray("info");
                                while(count<infoArray.length())
                                {
                                        JSONObject object = infoArray.getJSONObject(count);

                                        name = object.getString("name");
                                        if(!object.getString("meal").equals("null"))
                                                meal = object.getString("meal");
                                        else meal = "0";
                                        if(!object.getString("taka").equals("null"))
                                                taka = object.getString("taka");
                                        else taka = "0";
                                        if(!object.getString("cost").equals("null"))
                                                cost = object.getString("cost");
                                        else cost = "0";
                                        if(!object.getString("status").equals("null"))
                                                status = object.getString("status");
                                        else status = "0";
                                        modelList.add(new CostModel(name,meal,taka,cost,status));
                                        count++;
                                }
                        }

                        ReportAdapter adapter = new ReportAdapter(this, modelList, mealRate, sharedPreferenceData.getmyCurrentSession());
                        listView.setAdapter(adapter);

                        txtTotalTaka.setText(monthlyTaka);txtTotalMeal.setText(monthlyMeal);txtTotalCost.setText(monthlyCost);
                        txtRemaining.setText(remain);txtMealRate.setText(mealRate+" per meal");

                }catch (JSONException e)
                {
                        Log.d("Exception ",e.toString());
                }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                MenuInflater menuInflater = getMenuInflater();
                menuInflater.inflate(R.menu.finish_menue, menu);
                return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
                closeCurrentSession();
                return true;
        }

        //current session close
        private void closeCurrentSession()
        {
                Calendar calendar = Calendar.getInstance();
                final String currentDate = format.format(calendar.getTime());

                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                iOSDialogBuilder builder = new iOSDialogBuilder(MonthReport.this);

                builder.setTitle("Want to close this session ?")
                        .setSubtitle("It will remove all previous data,like meal,cost,taka etc.")
                        .setBoldPositiveLabel(true)
                        .setCancelable(false)
                        .setPositiveListener("Yes",new iOSDialogClickListener() {
                                @Override
                                public void onClick(iOSDialog dialog) {
                                        if(internetIsOn.isOnline())
                                        {
                                                try {
                                                        String data = URLEncoder.encode("user","UTF-8")+"="+URLEncoder.encode(sharedPreferenceData.getCurrentUserName(),"UTF-8")+"&"
                                                                +URLEncoder.encode("date","UTF-8")+"="+URLEncoder.encode(currentDate,"UTF-8");
                                                        backgroundTask = new DatabaseBackgroundTask(MonthReport.this);
                                                        backgroundTask.setOnResultListener(anInterface);
                                                        backgroundTask.execute(getResources().getString(R.string.dateInterval),data);
                                                } catch (UnsupportedEncodingException e) {
                                                        e.printStackTrace();
                                                }
                                                dialog.dismiss();
                                        }else dialogClass.noInternetConnection();
                                }
                        })
                        .setNegativeListener("No", new iOSDialogClickListener() {
                                @Override
                                public void onClick(iOSDialog dialog) {
                                        dialog.dismiss();
                                }
                        })
                        .build().show();
        }

        //convert monthly report to pdf
        private void createPdf()
        {
                Document document = new Document();
                try {
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Meal Counter";
                        File directory = new File(path);
                        if(!directory.exists())
                                directory.mkdir();

                        File file = new File(directory,sharedPreferenceData.getmyCurrentSession()+".pdf");
                        reportPath = file.getAbsolutePath();
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        PdfWriter.getInstance(document,fileOutputStream);
                        document.setPageSize(PageSize.A4);
                        document.open();
                        Paragraph paragraph;
                        Font smallFont = new Font(Font.FontFamily.TIMES_ROMAN,22.0f,Font.NORMAL, BaseColor.BLACK);
                        Font largeFont = new Font(Font.FontFamily.TIMES_ROMAN,30.0f,Font.BOLD, BaseColor.BLACK);
                        paragraph = new Paragraph("Session : #"+sharedPreferenceData.getmyCurrentSession()+"\n\n"+
                                "Total Taka ------ "+monthlyTaka+"\n"+
                                "Total Meal ------ "+monthlyMeal+"\n"+
                                "Total Cost ------ "+monthlyCost+"\n"+
                                "Remain     ------ "+remain+"\n"+
                                "Meal rate  ------ "+mealRate+"\n\n\n",largeFont);
                        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
                        //document.add(paragraph);

                        float[] columnWidth = {6f,9f,9f,9f,9f,9f};
                        PdfPTable table = new PdfPTable(columnWidth);
                        table.setWidthPercentage(90f);

                        insertDataIntoTable(table,"Serial", Element.ALIGN_CENTER,1,smallFont);
                        insertDataIntoTable(table,"Name", Element.ALIGN_CENTER,1,smallFont);
                        insertDataIntoTable(table,"Taka", Element.ALIGN_CENTER,1,smallFont);
                        insertDataIntoTable(table,"Meal", Element.ALIGN_CENTER,1,smallFont);
                        insertDataIntoTable(table,"Cost", Element.ALIGN_CENTER,1,smallFont);
                        insertDataIntoTable(table,"Status", Element.ALIGN_CENTER,1,smallFont);
                        table.setHeaderRows(1);

                        for(int i=0;i<modelList.size();i++)
                        {
                                insertDataIntoTable(table,String.valueOf(i+1),Element.ALIGN_CENTER,1,smallFont);
                                insertDataIntoTable(table,modelList.get(i).getId(),Element.ALIGN_CENTER,1,smallFont);
                                insertDataIntoTable(table,modelList.get(i).getTaka(),Element.ALIGN_CENTER,1,smallFont);
                                insertDataIntoTable(table,modelList.get(i).getName(),Element.ALIGN_CENTER,1,smallFont);
                                insertDataIntoTable(table,modelList.get(i).getDate(),Element.ALIGN_CENTER,1,smallFont);
                                insertDataIntoTable(table,modelList.get(i).getStatus(),Element.ALIGN_CENTER,1,smallFont);
                        }

                        paragraph.add(table);
                        document.add(paragraph);
                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                } catch (DocumentException e) {
                        e.printStackTrace();
                }finally {
                        document.close();

                }
        }

        //for row and column
        private void insertDataIntoTable(PdfPTable table,String text,int align,int colspan,Font font)
        {
                PdfPCell cell = new PdfPCell(new Phrase(text.trim(),font));
                cell.setHorizontalAlignment(align);
                cell.setColspan(colspan);
                if(text.trim().equals(""))
                        cell.setMinimumHeight(10f);
                table.addCell(cell);
        }

        //get monthly report
        private OnAsyncTaskInterface asyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        processJsonData(message);
                                }
                        });
                }
        };

        //check this session is expire or not
        private OnAsyncTaskInterface anInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        if(message.equals("success"))
                                        {
                                                //someMethod.progress("Closing current session","Current session close successfully");
                                                createPdf();
                                                someMethod.closeSessionAlert("Closing current session",reportPath);
                                        }
                                        else
                                        {
                                               dialogClass.onSuccessListener(taskInterface);
                                               dialogClass.warning("Some days still available in this session,do you really want to close this session?It will remove all current information.");
                                        }
                                }
                        });
                }
        };


        //get warning return message
        private OnAsyncTaskInterface taskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        if(message.equals("yes"))
                                        {
                                                //someMethod.progress("Working on it","Current session close successfully");
                                                createPdf();
                                                someMethod.closeSessionAlert("Closing current session",reportPath);
                                        }
                                }
                        });
                }
        };
}
