package server.http.android.androidhttpserver;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import android.widget.Toast;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import server.http.android.androidhttpserver.server.MyServer;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private MyServer server;
    String path;
    Button eqi,pp,back,next;
    int vol,bas;
    String play;
    TextView tv;
    int cur,max;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eqi=(Button)findViewById(R.id.eqi);
        pp=(Button)findViewById(R.id.play);
        next=(Button)findViewById(R.id.next);
        back=(Button)findViewById(R.id.prev);
        vol=0;
        bas=0;
        cur=0;
        play="true";
        tv=(TextView)findViewById(R.id.textView);
        path = Environment.getExternalStorageDirectory().toString()+"/music";
        File f = new File(path);
        Log.d("Files", "Path: " + path);
        List<String> list =new ArrayList<String>();
        final File file[] = f.listFiles();
        Log.d("Files", "Size: "+ file.length);
        for (int i=0; i < file.length; i++)
        {
            list.add(file[i].getName());
            Log.d("Files", "FileName:" + file[i].getName());
        }
        max=file.length;
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
        ListView listView=(ListView)findViewById(R.id.list);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(this);
        eqi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowDialog();
            }
        });
        pp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try
                {
                    if(play.equals("true")) {
                        play = "false";
                        pp.setText("PLAY");
                    }
                    else {
                        play = "true";
                        pp.setText("PAUSE");
                    }
                    new Task().execute(vol,bas,play);
                }
                catch(Exception ex)
                {
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(cur==0)
                    cur=max-1;
                else
                    cur--;
                String stuff=file[cur].getName();
                try {
                    server.PATH=path+"/"+stuff;
                    server = new MyServer(path+"/"+stuff);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(cur==max-1)
                    cur=0;
                else
                    cur++;
                String stuff=file[cur].getName();
                try {
                    server.PATH=path+"/"+stuff;
                    server = new MyServer(path+"/"+stuff);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    public void ShowDialog()
    {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        final View Viewlayout = inflater.inflate(R.layout.activity_dialog,
                (ViewGroup) findViewById(R.id.layout_dialog));

        final TextView item1 = (TextView)Viewlayout.findViewById(R.id.txtItem1); // txtItem1
        item1.setText("Volume");
        final TextView item2 = (TextView)Viewlayout.findViewById(R.id.txtItem2); // txtItem2
        item2.setText("Bass");
        popDialog.setTitle("Equalizer");
        popDialog.setView(Viewlayout);

        //  seekBar1
        SeekBar seek1 = (SeekBar) Viewlayout.findViewById(R.id.seekBar1);
        seek1.setProgress(vol);
        seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                //Do something here with new value
                vol=progress;
            }

            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                try
                {
                    new Task().execute(vol,bas,play);
                }
                catch(Exception ex)
                {
                }

            }
        });

        //  seekBar2
        SeekBar seek2 = (SeekBar) Viewlayout.findViewById(R.id.seekBar2);
        seek2.setProgress(bas);
        seek2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                //Do something here with new value
                bas=progress;
            }

            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                try
                {
                    new Task().execute(vol,bas,play);
                }
                catch(Exception ex)
                {
                }
            }
        });


        // Button OK
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
            return true;
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
    public void onDestroy(){
        if(server != null) {
            server.stop();
        }
        super.onDestroy();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        cur=position;
        String stuff=(String)parent.getItemAtPosition(position);
        try {
            server.PATH=path+"/"+stuff;
            server = new MyServer(path+"/"+stuff);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this,path+"/"+stuff,Toast.LENGTH_SHORT).show();
    }
    private class Task extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] params) {
            try {

                HttpClient Client = new DefaultHttpClient();
                String URL = "http://192.168.4.1/?volume=" + params[0] + "&bass=" + params[1] +"&play="+params[2];
                try
                {
                    String SetServerString = "";

                    // Create Request to server and get response

                    HttpGet httpget = new HttpGet(URL);
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    SetServerString = Client.execute(httpget, responseHandler);

                    Toast.makeText(getApplicationContext(), "Selected: " + SetServerString, Toast.LENGTH_LONG).show();
                }
                catch(Exception ex)
                {
                    Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();

                }
            }
            catch (Exception ex){}
            return null;
        }
    }
}
