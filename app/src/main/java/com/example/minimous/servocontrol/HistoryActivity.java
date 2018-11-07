package com.example.minimous.servocontrol;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.minimous.servocontrol.Models.History;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    ListView historyList;
    ArrayList<History> arrayHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyList = (ListView)findViewById(R.id.historyList);
        arrayHistory = new ArrayList<History>();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Json().execute("http://gotothetop.tk/infokey/");
            }
        });
    }

    class Json extends AsyncTask<String,Integer,String>
    {
        @Override
        protected String doInBackground(String... params) {
            return getContent_Url(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONArray jsonArray=new JSONArray(s);
                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    Log.i("abc","xyz");
                    arrayHistory.add(new History(jsonObject.getString("username"),jsonObject.getString("action"),jsonObject.getString("time")));
                }
                ListAdapter listAdapter=new ListAdapter(getApplicationContext(),R.layout.activity_custom_layout,arrayHistory);
                historyList.setAdapter(listAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private static String getContent_Url(String theUrl)
    {
        StringBuilder content = new StringBuilder();

        // many of these calls can throw exceptions, so i've just
        // wrapped them all in one try/catch statement.
        try
        {
            // create a url object
            URL url = new URL(theUrl);

            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();

            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;

            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null)
            {
                content.append(line + "\n");
            }
            bufferedReader.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return content.toString();
    }
}
