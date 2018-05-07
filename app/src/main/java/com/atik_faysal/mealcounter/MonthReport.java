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
import java.util.ArrayList;
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

        private List<CostModel>modelList;

        private String monthlyTaka ,monthlyCost,monthlyMeal,remain,mealRate;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.report_list);
                initComponent();
                setToolbar();
        }


        //initialize all component
        @SuppressLint("SetTextI18n")
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
                CheckInternetIsOn internetIsOn = new CheckInternetIsOn(this);
                someMethod = new NeedSomeMethod(this);
                DatabaseBackgroundTask backgroundTask = new DatabaseBackgroundTask(this);

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
                }
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

                        monthlyTaka = detailObject.getString("monthlyTaka");
                        monthlyCost = detailObject.getString("monthlyCost");
                        monthlyMeal = detailObject.getString("monthlyMeal");
                        remain = detailObject.getString("remaining");
                        mealRate = detailObject.getString("mealRate");

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
                                        meal = object.getString("meal");
                                        taka = object.getString("taka");
                                        cost = object.getString("cost");
                                        status = object.getString("status");
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
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                iOSDialogBuilder builder = new iOSDialogBuilder(MonthReport.this);

                builder.setTitle("Want to close this session ?")
                        .setSubtitle("It will remove all previous data,like meal,cost,taka etc.")
                        .setBoldPositiveLabel(true)
                        .setCancelable(false)
                        .setPositiveListener("Yes",new iOSDialogClickListener() {
                                @Override
                                public void onClick(iOSDialog dialog) {
                                        someMethod.progress("Removing data","Current session close successfully");
                                        createPdf();
                                        dialog.dismiss();
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
}
