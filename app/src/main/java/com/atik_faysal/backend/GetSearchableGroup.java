package com.atik_faysal.backend;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.atik_faysal.model.SearchableModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;

import interfaces.SearchInterfaces;

import static android.content.ContentValues.TAG;
import android.content.Context;
/**
 * Created by USER on 1/25/2018.
 */

public class GetSearchableGroup extends AsyncTask<String,Void,Void>
{


        SearchInterfaces searchInterfaces;
        private Context context;

        String ResultHolder;

        ArrayList<String> subjectsList;


        public void setOnResultListener(SearchInterfaces searchInterfaces)
        {
                if(searchInterfaces !=null)
                        this.searchInterfaces = searchInterfaces;
        }

        public GetSearchableGroup(Context context)
        {
                this.context = context;
        }

        @Override
        protected Void doInBackground(String... strings) {

                HttpServicesClass httpServiceObject = new HttpServicesClass("http://192.168.56.1/getGroupName.php");
                try
                {
                        String SubjectName;
                        httpServiceObject.ExecutePostRequest();

                        if(httpServiceObject.getResponseCode() == 200)
                        {
                                ResultHolder = httpServiceObject.getResponse();

                                if(ResultHolder != null)
                                {
                                        JSONArray jsonArray;

                                        try {
                                                jsonArray = new JSONArray(ResultHolder);

                                                JSONObject jsonObject;

                                                subjectsList = new ArrayList<>();

                                                for(int i=0; i<jsonArray.length(); i++)
                                                {
                                                        jsonObject = jsonArray.getJSONObject(i);
                                                        SubjectName = jsonObject.getString("groupID");
                                                        subjectsList.add(SubjectName);
                                                }

                                                searchInterfaces.onResultSuccess(subjectsList);
                                        }
                                        catch (JSONException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                        }
                                }
                        }
                        else
                        {
                                Toast.makeText(context, httpServiceObject.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        }
                }
                catch (Exception e)
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                return null;
        }


}
