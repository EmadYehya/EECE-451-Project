package com.emadyehya.eece451;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //region GLOBAL_VARIABLES
    Context context;
    EditText editTextDialogUserInput;
    String url = "http://emadyehya.com/add_nb";
    int phone_number;
    String response;
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

        findViewById(R.id.graph_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GraphActivity.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.prompts);
                //dialog.setTitle("Title...");
                editTextDialogUserInput = (EditText) dialog.findViewById(R.id.editTextDialogUserInput);

                Button dialogButton = (Button) dialog.findViewById(R.id.button_ok);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            phone_number = Integer.parseInt(editTextDialogUserInput.getText().toString());
                            new SendPhoneNumber().execute();
                        }
                        catch(Exception E) {
                            dialog.dismiss();
                            final Dialog newDialog = new Dialog(context);
                            newDialog.setContentView(R.layout.custom);
                            Button button = (Button) newDialog.findViewById(R.id.button_ok);
                            newDialog.show();
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    newDialog.dismiss();
                                }
                            });
                        }
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        init();

        //endregion
    }

    //region CHANGE_FUNCTIONS

    //change_wifi sets the wifi to on or off based on the value of set
    private void change_wifi(boolean set){

        WifiManager wifiManager = (WifiManager)this.context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(set);
        String toast_txt = "WiFi has been turned ";
        toast_txt += set? "on" : "off";
        Toast.makeText(context, toast_txt, Toast.LENGTH_SHORT).show();
    }

    private void change_bluetooth(boolean set) {


        String toast_txt = "Bluetooth has been turned ";
        toast_txt += set? "on" : "off";
        Toast.makeText(context, toast_txt, Toast.LENGTH_SHORT).show();

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (set && !isEnabled) {
            bluetoothAdapter.enable();
        }
        else if(!set && isEnabled) {
            bluetoothAdapter.disable();
        }



    }
    //endregion

    private void init(){

        boolean wifi_status = false, bluetooth_status = false, cellular_status = false;
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifi_status = wifi.isWifiEnabled();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetooth_status =bluetoothAdapter != null && bluetoothAdapter.isEnabled();

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        cellular_status = activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected();

        //GHAZI code goes here

        ((Switch) findViewById(R.id.main_switch_bluetooth)).setChecked(bluetooth_status);
        ((Switch) findViewById(R.id.main_switch_wifi)).setChecked(wifi_status);
        ((Switch) findViewById(R.id.main_swtich_cell)).setChecked(cellular_status);
    }

    private class SendPhoneNumber extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected String doInBackground(String... urls) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add (new BasicNameValuePair("nb", "" + phone_number ));
            response = sh.makeServiceCall(url,ServiceHandler.POST,params);
            return response;

        }
        protected void onPostExecute(String result) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}