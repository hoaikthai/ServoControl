package com.example.minimous.servocontrol;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.minimous.servocontrol.Models.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_SIGNUP = 0;
    EditText userText;
    EditText passwordText;
    Button loginButton;
    CheckBox remember;
    ProgressDialog waiting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponent();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Jsondemo().execute("http://gotothetop.tk/loginkey/");
            }
        });
    }

    private void initComponent(){
        userText = (EditText) findViewById(R.id.usernameText);
        passwordText = (EditText) findViewById(R.id.passText);
        loginButton = (Button) findViewById(R.id.loginButton);
        remember = (CheckBox) findViewById(R.id.rememberCheck);
        waiting = new ProgressDialog(LoginActivity.this, R.style.AppTheme);

    }

    public class Jsondemo extends AsyncTask<String,Integer,String>
    {
        @Override
        protected String doInBackground(String... strings) {
            return getContent_Url(strings[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            // ArrayList<String> name=new ArrayList<>();
            JSONArray jsonArray= null;
            try {
                jsonArray = new JSONArray(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                final JSONArray finalJsonArray = jsonArray;
                loginButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        boolean check = false;
                        for (int i = 0; i < finalJsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = finalJsonArray.getJSONObject(i);
                                if (userText.getText().toString().equals(jsonObject.getString("username")) && passwordText.getText().toString().equals(jsonObject.getString("password"))) {
                                    check = true;
                                    onLoginSuccess();
                                    Intent intent = new Intent(LoginActivity.this, DeviceList.class);
                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (check == false) {
                            passwordText.setText("");
                            Toast.makeText(getApplicationContext(), "Username or Password are incorrect!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getContent_Url(String theUrl)
    {
        StringBuilder content = new StringBuilder();
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

    public void savingPreferences()
    {
        SharedPreferences pre = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        String user = userText.getText().toString();
        String pass = passwordText.getText().toString();

        boolean rememberChecked = remember.isChecked();
        if(!rememberChecked)
        {
            editor.clear();
        }
        else
        {
            editor.putString("user", user);
            editor.putString("pwd", pass);
            editor.putBoolean("rememberMe", rememberChecked);
        }
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onLoginSuccess(){
        //savingPreferences();
        Intent intent = new Intent(LoginActivity.this, DeviceList.class);
        Bundle bundle = new Bundle();
        bundle.putString("username", userText.getText().toString());
        intent.putExtra("myPacket", bundle);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed(){
        Toast.makeText(getBaseContext(), "Login Failed", Toast.LENGTH_SHORT).show();
        loginButton.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
