package com.example.minimous.servocontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minimous.servocontrol.Models.History;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DeviceList extends AppCompatActivity {

    Button pairedButton;
    ListView deviceList;
    Button historyButton;

    private BluetoothAdapter bluetoothAdapter = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        //restoringPreferences();

        pairedButton = (Button)findViewById(R.id.pairedButton);
        deviceList = (ListView)findViewById(R.id.deviceList);
        historyButton = (Button)findViewById(R.id.historyButton);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this,"Bluetooth Device not available",Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            if (bluetoothAdapter.isEnabled()) {

            }
            else {
                Intent turnButton = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnButton,1);
            }
        }

        pairedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                pairedDevicesList();
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceList.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void pairedDevicesList() {
        pairedDevices = bluetoothAdapter.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bluetoothDevice : pairedDevices) {
                list.add(bluetoothDevice.getName()+"\n"+bluetoothDevice.getAddress());
            }
        }
        else {
            Toast.makeText(this, "No paired bluetooth device found", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        deviceList.setAdapter(adapter);
        deviceList.setOnItemClickListener(myListClickListener);
    }

    public void restoringPreferences()
    {
        SharedPreferences pre = getSharedPreferences("data",MODE_PRIVATE);
        boolean bchk = pre.getBoolean("rememberMe", false);
        if(!bchk)
        {
            Intent login = new Intent(this,LoginActivity.class);
            startActivity(login);
        }
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            // Make an intent to start next activity.
            Intent callerIntent = getIntent();
            Bundle packetFromCaller = callerIntent.getBundleExtra("myPacket");
            Intent i = new Intent(DeviceList.this, servoControl.class);
            //Change the activity.
            i.putExtra("myPacket", packetFromCaller);
            i.putExtra(EXTRA_ADDRESS, address); //this will be received at servoControl (class) Activity
            startActivity(i);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_list, menu);
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
}
