package server.http.android.androidhttpserver;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Devices extends Activity implements View.OnClickListener {
    Button refbut;
    String host;
    ListView listView;
    ArrayList<String> raw = new ArrayList<String>();
    ArrayList<String> ip = new ArrayList<String>();
    ArrayList<String> hostname = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        refbut = (Button) findViewById(R.id.refdevice);
        refbut.setOnClickListener(this);
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ipAddress = Formatter.formatIpAddress(ip);
        host = ipAddress.substring(0, ipAddress.lastIndexOf(".") + 1);
        listView = (ListView) findViewById(R.id.devlist);
        refbut.setOnClickListener(this);

    }

    protected void refresh() {

        new Task().execute("2", "20");
        new Task().execute("21", "40");
        new Task().execute("41", "60");
        new Task().execute("61", "80");
        new Task().execute("81", "100");
        new Task().execute("101", "120");
        new Task().execute("121", "140");
        new Task().execute("141", "160");
        new Task().execute("161", "180");
        new Task().execute("181", "200");
        new Task().execute("201", "220");
        new Task().execute("221", "240");
        new Task().execute("241", "255");
    }

    @Override
    public void onClick(View v) {
        raw.clear();
        refresh();
        refbut.setEnabled(false);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refbut.setEnabled(true);
            }
        }, 5000);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    // Basic sanity check
                    String mac = splitted[3];
                    if (mac.matches("..:..:..:..:..:..") && !mac.equals("00:00:00:00:00:00")) {
                        System.out.println(mac + "  " + splitted[0]);
                        raw.add(splitted[0]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        listView.setAdapter(null);
        /*for(int i=0;i<raw.size();i++){
            String res=send(raw.get(i));
            if(res.equals("hello")){
                try {
                    hostname.add(InetAddress.getByName(raw.get(i)).getHostName());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ip.add(raw.get(i));
            }
        }
        */
        Set<String> set = new HashSet<String>();
        set.addAll(raw);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("config", 1);
        editor.putStringSet("iplist", set);
        editor.commit();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, raw);
        listView.setAdapter(dataAdapter);


    }

    public String send(String... params) {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + params[0] + "/?req=hello";
        final String[] res = new String[1];
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        res[0] = response;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
        return res[0];
    }

    class Task extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            for (int i = Integer.parseInt(params[0]); i <= Integer.parseInt(params[1]); i++) {
                InetAddress byName = null;
                Boolean b = false;
                try {
                    byName = InetAddress.getByName(host + i);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                try {
                    b = (byName.isReachable(50));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (b == true)
                    System.out.println(byName);
            }
            return "";
        }

    }
}
