package com.emadyehya.eece451;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //region GLOBAL_VARIABLES
    Context context;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region INTILIZATIONS
        context = this;

        ((Switch) findViewById(R.id.main_switch_wifi)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                change_wifi(isChecked);
            }
        });

        ((Switch) findViewById(R.id.main_switch_bluetooth)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                change_bluetooth(isChecked);
            }
        });

        findViewById(R.id.main_btn_begin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetectActivity.class);
                startActivity(intent);
            }
        });

        init();

        //endregion
    }

    //region CHANGE_FUNCTIONS

    //change_wifi sets the wifi to on or off based on the value of set
    private void change_wifi(boolean set){


        String toast_txt = "WiFi has been turned ";
        toast_txt += set? "on" : "off";
        Toast.makeText(context, toast_txt, Toast.LENGTH_SHORT).show();
    }

    private void change_bluetooth(boolean set) {

        String toast_txt = "Bluetooth has been turned ";
        toast_txt += set? "on" : "off";
        Toast.makeText(context, toast_txt, Toast.LENGTH_SHORT).show();

    }
    //endregion

    private void init(){
        //TODO: Ghazi, find the values of the below variables and fill them
        boolean wifi_status = false, bluetooth_status = false, cellular_status = false;

        //GHAZI code goes here

        ((Switch) findViewById(R.id.main_switch_bluetooth)).setChecked(bluetooth_status);
        ((Switch) findViewById(R.id.main_switch_wifi)).setChecked(wifi_status);
        ((Switch) findViewById(R.id.main_swtich_cell)).setChecked(cellular_status);
    }
}