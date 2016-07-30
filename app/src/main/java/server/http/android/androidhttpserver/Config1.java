package server.http.android.androidhttpserver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Config1 extends Activity implements View.OnClickListener {


    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config1);
        next = (Button) findViewById(R.id.button);
        next.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, Config2.class);
        startActivity(intent);

    }
}
