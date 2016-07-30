package server.http.android.androidhttpserver;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import server.http.android.androidhttpserver.server.MyServer;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private MyServer server;
    Button eqi, pp, back, next;
    int vol, bas;
    String play;
    TextView tv;
    int cur, max;
    List<String> names;
    List<String> list;
    TextView textView;
    static String homeSSID;
    static String homePass;
    static String mobileIP;
    static String mobilePort;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eqi = (Button) findViewById(R.id.eqi);
        pp = (Button) findViewById(R.id.play);
        next = (Button) findViewById(R.id.next);
        back = (Button) findViewById(R.id.prev);
        vol = 0;
        bas = 0;
        cur = 0;
        textView = (TextView) findViewById(R.id.now);
        textView.setSelected(true);
        play = "true";
        textView.setText("Now Playing: Nothing. Select a song to play!");
        list = new ArrayList<String>();
        names = new ArrayList<String>();
        scanSdcard();
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(this);

        eqi.setOnClickListener(this);
        pp.setOnClickListener(this);
        back.setOnClickListener(this);
        next.setOnClickListener(this);
    }
    private void scanSdcard(){
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };
        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";

        Cursor cursor = null;
        try {
            Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            cursor = this.getContentResolver().query(uri, projection, selection, null, sortOrder);
            if( cursor != null){
                cursor.moveToFirst();
                while( !cursor.isAfterLast() ){
                    String title = cursor.getString(0);
                    String artist = cursor.getString(1);
                    String path = cursor.getString(2);
                    String displayName  = cursor.getString(3);
                    String songDuration = cursor.getString(4);
                    cursor.moveToNext();
                    if(path!=null){
                        list.add(path);
                        names.add(displayName);
                    }

                }

            }

        } catch (Exception e) {

        }finally{
            if( cursor != null){
                cursor.close();
            }
        }
    }

    public void showDialog() {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        final View Viewlayout = inflater.inflate(R.layout.activity_dialog,
                (ViewGroup) findViewById(R.id.layout_dialog));

        final TextView item1 = (TextView) Viewlayout.findViewById(R.id.txtItem1); // txtItem1
        item1.setText("Volume");
        final TextView item2 = (TextView) Viewlayout.findViewById(R.id.txtItem2); // txtItem2
        item2.setText("Bass");
        popDialog.setTitle("Equalizer");
        popDialog.setView(Viewlayout);
        SeekBar seek1 = (SeekBar) Viewlayout.findViewById(R.id.seekBar1);
        seek1.setProgress(vol);
        seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vol = progress;
            }

            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                try {
                    send(String.valueOf(vol),String.valueOf(bas),play);
                } catch (Exception ex) {
                }

            }
        });

        SeekBar seek2 = (SeekBar) Viewlayout.findViewById(R.id.seekBar2);
        seek2.setProgress(bas);
        seek2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bas = progress;
            }

            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                try {
                    send(String.valueOf(vol),String.valueOf(bas),play);
                } catch (Exception ex) {
                }
            }
        });
        popDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
        popDialog.create();
        popDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Config1.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            server = new MyServer(Environment.getExternalStorageDirectory().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        if (server != null) {
            server.stop();
        }
        super.onDestroy();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        cur = position;
        String stuff = (String) parent.getItemAtPosition(position);
        try {
            textView.setText("Now Playing: " + names.get(cur));
            MyServer.PATH = list.get(cur);
            server = new MyServer(list.get(cur));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.prev) {
            if (cur == 0)
                cur = max - 1;
            else
                cur--;
            try {
                MyServer.PATH = list.get(cur);
                server = new MyServer(list.get(cur));
                textView.setText("Now Playing: " + names.get(cur));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (v.getId() == R.id.play) {
            try {
                if (play.equals("true")) {
                    play = "false";
                    pp.setText("PLAY");
                    textView.setText("Paused");
                } else {
                    play = "true";
                    textView.setText("Now Playing: " + names.get(cur));
                    pp.setText("PAUSE");
                }
                send(String.valueOf(vol),String.valueOf(bas),play);
            } catch (Exception ex) {
            }
        } else if (v.getId() == R.id.next) {
            if (cur == max - 1)
                cur = 0;
            else
                cur++;
            try {
                MyServer.PATH = list.get(cur);
                server = new MyServer(list.get(cur));
                textView.setText("Now Playing: " + names.get(cur));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showDialog();
        }
    }
    public void send(String ...par){
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://192.168.4.1?volume="+par[0]+"&bass="+par[1]+"&play="+par[2];
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

