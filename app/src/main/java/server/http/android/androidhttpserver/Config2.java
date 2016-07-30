package server.http.android.androidhttpserver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Config2 extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    ListView list1;
    Button next;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    WifiConfiguration wifiConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config2);
        list1 = (ListView) findViewById(R.id.list1);
        list1.setOnItemClickListener(this);
        next = (Button) findViewById(R.id.button2);
        next.setOnClickListener(this);
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);


        String buzo = mainWifi.getConnectionInfo().getSSID();
        MainActivity.homeSSID = buzo.substring(1, buzo.length() - 1);
        Toast.makeText(this, MainActivity.homeSSID,
                Toast.LENGTH_LONG).show();
        MainActivity.mobileIP = Formatter.formatIpAddress(mainWifi.getConnectionInfo().getIpAddress());
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mainWifi.startScan();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiverWifi);
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", parent.getItemAtPosition(position).toString());
        wifiConfig.preSharedKey = String.format("\"%s\"", "sparkfun");
        int netId = mainWifi.addNetwork(wifiConfig);
        mainWifi.disconnect();
        mainWifi.enableNetwork(netId, true);
        mainWifi.reconnect();

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, Config3.class);
        startActivity(intent);
    }

    class WifiReceiver extends BroadcastReceiver {

        // This method call when number of wifi connections changed
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiList = mainWifi.getScanResults();
            List<String> ssids = new ArrayList<String>();

            for (int i = 0; i < wifiList.size(); i++) {
                ssids.add(wifiList.get(i).SSID);
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, ssids);
            list1.setAdapter(dataAdapter);
        }

    }
}
