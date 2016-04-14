package com.emadyehya.eece451;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class DetectActivity extends AppCompatActivity  implements WifiP2pManager.PeerListListener {

    //region GLOBAL_VARIABLES
    private String m_Text = "";
    Context context;
    private static int deviceNumber;
    private TableLayout table;
    Handler myHandler;
    DeviceManager DM;

    private List peers = new ArrayList();
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private WifiP2pManager manager;
    private android.content.BroadcastReceiver receiver = null;
    ProgressDialog progressDialog = null;


    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);

        context = this;
        myHandler = new Handler();
        DM = Manager.getInstance().DM;
        DM.SetContext(context);

        //region TABLE_INIT
        ScrollView scrollView = new ScrollView(this);

        table = new TableLayout(this);
        table.setVerticalScrollBarEnabled(true);
        table.setStretchAllColumns(true);
        table.setShrinkAllColumns(true);
        scrollView.addView(table);

        TableRow rowTitles = new TableRow(this);
        TableRow rowLows = new TableRow(this);
        TableRow rowConditions = new TableRow(this);
        rowConditions.setGravity(Gravity.CENTER);
        TextView empty = new TextView(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.span = 6;
        rowTitles.addView(empty);
        TextView column1Title = new TextView(this);
        column1Title.setText("MAC Address");
        column1Title.setTypeface(Typeface.SERIF, Typeface.BOLD);
        rowTitles.addView(column1Title);
        TextView column2Title = new TextView(this);
        column2Title.setText("Last detected time");
        column2Title.setTypeface(Typeface.SERIF, Typeface.BOLD);
        rowTitles.addView(column2Title);
        TextView column3Title = new TextView(this);
        column3Title.setText("Nb of times detected");
        column3Title.setTypeface(Typeface.SERIF, Typeface.BOLD);
        rowTitles.addView(column3Title);
        table.addView(rowTitles);
        setContentView(scrollView);
        //endregion

        //region WIFI_DIRECT_INIT
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

//        myHandler.post(AttemptDiscover);

//        onInitiateDiscovery();

        //endregion

        //TODO: for testing. To test table functionality.
        //to test wifi functionality, comment this and uncomment myHandler.post(AttemptDisvoer) above
        TestDetect();
    }

    //region TABLE_FUNCTIONS
    public void addDevice(Device device) {
        TableRow rowNew = new TableRow(this);

        TextView rowTitle = new TextView(this);
        rowTitle.setText("Device " + deviceNumber);
        rowTitle.setTypeface(Typeface.DEFAULT_BOLD);
        rowNew.addView(rowTitle);

        deviceNumber++;

        TextView rowItem1 = new TextView(this);
        rowItem1.setText(device.getMAC_address());
        rowItem1.setGravity(Gravity.CENTER_HORIZONTAL);
        rowNew.addView(rowItem1);


        TextView rowItem2 = new TextView(this);
        rowItem2.setText("" + device.getLast_detected_time());
        rowItem2.setGravity(Gravity.CENTER_HORIZONTAL);
        rowNew.addView(rowItem2);

        TextView rowItem3 = new TextView(this);
        rowItem3.setText("" + device.getNb_of_times_detected());
        rowItem3.setGravity(Gravity.CENTER_HORIZONTAL);
        rowNew.addView(rowItem3);

        table.addView(rowNew);
    }
    //endregion

    //region WIFI_DIRECT_DETECTION
    private Runnable AttemptDiscover = new Runnable() {
        @Override
        public void run() {
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Toast.makeText(DetectActivity.this, "Discovery Initiated",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reasonCode) {
                    Toast.makeText(DetectActivity.this, "Discovery Failed : " + reasonCode,
                            Toast.LENGTH_SHORT).show();
                }

            });
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        receiver = new BroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void onInitiateDiscovery() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(this, "Press back to cancel", "finding peers", true,
                true, new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                });
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        // Out with the old, in with the new.
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        peers.clear();
        Log.e("Device", peerList.getDeviceList().toString());
        peers.addAll(peerList.getDeviceList());
        Log.d("Devices", peers.toString());
        DM.StartSession();
        for(int i = 0; i < peers.size(); i++){
            int dA_location = peers.get(i).toString().indexOf("deviceAddress");
            Log.d("Device-Singular", peers.get(i).toString().substring(dA_location+ 15, dA_location+15+17));
            DM.DeviceDetected(peers.get(i).toString().substring(dA_location+ 15, dA_location+15+17), "", "");
        }
        DM.EndSession();
        ArrayList<Device> print = DM.GetPrintList();

        table.removeAllViews();

        for(int i = 0; i < print.size(); i++){
            addDevice(print.get(i));
        }

        myHandler.post(AttemptDiscover);

//        onInitiateDiscovery();

    }

    //endregion

    //region TESTING
    private void TestDetect(){
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Vector itemsVector = new Vector();
                for(int i = 0; i < 20; i++){
                    itemsVector.add(i);
                }

                Collections.shuffle(itemsVector);
                itemsVector.setSize(7);

                DM.StartSession();
                for(int i = 0; i < 7; i++){
                    DM.DeviceDetected(String.valueOf(itemsVector.elementAt(i)), "", "");
                }

                DM.EndSession();

                ArrayList<Device> print = DM.GetPrintList();

                table.removeAllViews();

                for(int i = 0; i < print.size(); i++){
                    addDevice(print.get(i));
                }

                TestDetect();
            }
        }, 5000);
    }
    //endregion
}
