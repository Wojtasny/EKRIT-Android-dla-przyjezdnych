package com.eestec_ekrit.wojtek.eestec;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button Map_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map_button = (Button) findViewById(R.id.activity_map);
        Map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent map_activity_intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(map_activity_intent);
            }
        });
    }
}
