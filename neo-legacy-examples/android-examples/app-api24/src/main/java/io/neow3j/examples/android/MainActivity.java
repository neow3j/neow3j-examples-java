package io.neow3j.examples.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonMonitor = (Button) findViewById(R.id.buttonMonitor);
        Button buttonTransfer = (Button) findViewById(R.id.buttonTransfer);

        buttonMonitor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Monitor().execute();
            }
        });

        buttonTransfer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Transfer().execute();
            }
        });

    }

}
