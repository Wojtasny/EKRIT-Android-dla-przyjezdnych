package com.eestec_ekrit.wojtek.eestec;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.clans.fab.FloatingActionMenu;

public class MainActivity extends Activity {
    Button Map_button;
//    FloatingActionMenu FAM;
//    com.github.clans.fab.FloatingActionButton FABloc1,FABloc2,FABloc3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setContentView(R.layout.floating_action_menu);
//        FAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
//        FABloc1 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
//        FABloc2 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);
//        FABloc3 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item3);


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
