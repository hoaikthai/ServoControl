package com.example.minimous.servocontrol;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

public class servoControl extends AppCompatActivity {

    Button buttonLock, buttonUnlock, buttonDisconnect;
    BluetoothAdapter bluetoothAdapter = null;
    BluetoothSocket bluetoothSocket = null;
    String user = null;
    String address = null;
    String action = null;
    String time = null;
    boolean isLocked = false;
    boolean isUnlocked = true;
    private ProgressDialog progress;
    private boolean isConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servo_control);

        Intent newIntent = getIntent();
        address = newIntent.getStringExtra(DeviceList.EXTRA_ADDRESS);
        Bundle packetFromCaller = newIntent.getBundleExtra("myPacket");
        user = packetFromCaller.getString("username");
        setContentView(R.layout.activity_servo_control);

        buttonLock = (Button) findViewById(R.id.lockButton);
        buttonUnlock = (Button) findViewById(R.id.unlockButton);
        buttonDisconnect = (Button) findViewById(R.id.disButton);
        new ConnectBT().execute();

        buttonLock.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lock();      //method to turn on
            }
        });

        buttonUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                unlock();   //method to turn off
            }
        });

        buttonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect(); //close connection
            }
        });

    }

    private void Disconnect()
    {
        if (bluetoothSocket != null) //If the bluetoothSocket is busy
        {
            try
            {
                bluetoothSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout

    }

    private void unlock()
    {
        if (isLocked)
        {
            if (bluetoothSocket !=null)
            {
                try
                {
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    action = "Unlock";
                    isLocked = false;
                    isUnlocked = true;
                    time = dateFormat.format(date).toString();
                    bluetoothSocket.getOutputStream().write(("Unlock:" + dateFormat.format(date) + " " + address+" "+ user+".").toString().getBytes());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new Parsedata().execute("http://gotothetop.tk/insertinfo/");
                        }
                    });
                }
                catch (IOException e)
                {
                    msg("Error");
                }
            }
        }
    }

    private void lock()
    {
        if (isUnlocked)
        {
            if (bluetoothSocket !=null)
            {
                try
                {
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    action = "Lock";
                    isUnlocked = false;
                    isLocked = true;
                    time = dateFormat.format(date).toString();
                    bluetoothSocket.getOutputStream().write(("Lock:" + dateFormat.format(date) + " " + address + " " +user+ ".").toString().getBytes());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new Parsedata().execute("http://gotothetop.tk/insertinfo/");
                        }
                    });
                }
                catch (IOException e)
                {
                    msg("Error");
                }
            }
        }

    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    class Parsedata extends AsyncTask<String,Integer,String>
    {

        @Override
        protected String doInBackground(String... strings) {
            return makePostRequest(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
        }
    }

    private String makePostRequest(String s) {
        HttpClient httpClient = new DefaultHttpClient();

        // URL của trang web nhận request
        HttpPost httpPost = new HttpPost(s);

        // Các tham số truyền
        List nameValuePair = new ArrayList(4);
        nameValuePair.add(new BasicNameValuePair("act", "insert"));
        nameValuePair.add(new BasicNameValuePair("username", user));
        nameValuePair.add(new BasicNameValuePair("action", action));
        nameValuePair.add(new BasicNameValuePair("time", time));
        //Encoding POST data
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String kq = "";
        try {
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            kq = EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return kq;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_servo_control, menu);
        return true;
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

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(servoControl.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (bluetoothSocket == null || !isConnected)
                {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = bluetoothAdapter.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    bluetoothSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    bluetoothSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isConnected = true;
            }
            progress.dismiss();
        }
    }


}
