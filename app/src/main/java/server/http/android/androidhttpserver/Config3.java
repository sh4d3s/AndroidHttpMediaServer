package server.http.android.androidhttpserver;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class Config3 extends Activity implements View.OnClickListener {
    Button finish;
    EditText sid, pas, mip, por;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config3);
        sid = (EditText) findViewById(R.id.sid);
        pas = (EditText) findViewById(R.id.pas);
        mip = (EditText) findViewById(R.id.mip);
        por = (EditText) findViewById(R.id.por);
        finish = (Button) findViewById(R.id.button3);
        sid.setText(MainActivity.homeSSID);
        finish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        MainActivity.homePass = pas.getText().toString();
        MainActivity.mobilePort = por.getText().toString();
        HttpClient Client = new DefaultHttpClient();
        String URL = "http://192.168.4.1/?ssid=" + MainActivity.homeSSID + "&pass=" + MainActivity.homePass + "&mobileip=" + MainActivity.mobileIP
                + "&port=8080";
        try {
            send(MainActivity.homeSSID, MainActivity.homePass, MainActivity.mobileIP, "8080");
        } catch (Exception ex) {
        }

    }

    public void send(String... params) {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.4.1/?ssid=" + params[0] + "&pass=" + params[1] + "&mobileip=" + params[2]
                + "&port=8080";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }
}
