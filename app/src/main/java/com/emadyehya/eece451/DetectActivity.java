package com.emadyehya.eece451;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class DetectActivity extends AppCompatActivity  implements WifiP2pManager.PeerListListener {

    //region GLOBAL_VARIABLES
    private String m_Text = "";
    Context context;
    private TableLayout table;
    Handler myHandler;
    DeviceManager DM;
    String SortingMethod = "MAC Address";


    ArrayList<String> list = new ArrayList<String>();
    private List peers = new ArrayList();
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private WifiP2pManager manager;
    private android.content.BroadcastReceiver receiver = null;
    ProgressDialog progressDialog = null;
    String url = "http://emadyehya.com/init_session";
    String url2 = "http://emadyehya.com/new_data";
    String response;
    String testMacAddress;
    String testTimes;
    String testTimeConnected;
    TableRow rowTitles;
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

        rowTitles = new TableRow(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.span = 6;
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
        TextView column4Title = new TextView(this);
        column4Title.setText("Detection Range");
        column4Title.setTypeface(Typeface.SERIF, Typeface.BOLD);
        rowTitles.addView(column4Title);
        TextView column5Title = new TextView(this);
        column5Title.setText("Total Detection Range");
        column5Title.setTypeface(Typeface.SERIF, Typeface.BOLD);
        rowTitles.addView(column5Title);
        table.addView(rowTitles);

        column1Title.setClickable(true);
        column1Title.setOnClickListener(tablerowOnClickListener1);
        column2Title.setClickable(true);
        column2Title.setOnClickListener(tablerowOnClickListener2);
        column3Title.setClickable(true);
        column3Title.setOnClickListener(tablerowOnClickListener3);
        column4Title.setClickable(true);
        column4Title.setOnClickListener(tablerowOnClickListener4);
        column5Title.setClickable(true);
        column5Title.setOnClickListener(tablerowOnClickListener5);



        setContentView(scrollView);
        //endregion

        //region WIFI_DIRECT_INIT
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        myHandler.post(AttemptDiscover);

//        onInitiateDiscovery();

        //endregion

        //TODO: for testing. To test table functionality.
        //to test wifi functionality, comment this and uncomment myHandler.post(AttemptDisvoer) above
        //TestDetect();
    }


    private View.OnClickListener tablerowOnClickListener1 = new View.OnClickListener() {
        public void onClick(View v) {
            //GET TEXT HERE
            SortingMethod = "MAC Address";

            Toast.makeText(DetectActivity.this, SortingMethod,
                    Toast.LENGTH_LONG).show();

        }

    };

    private View.OnClickListener tablerowOnClickListener2 = new View.OnClickListener() {
        public void onClick(View v) {
            //GET TEXT HERE
            SortingMethod = "Last detected time";

            Toast.makeText(DetectActivity.this, SortingMethod,
                    Toast.LENGTH_LONG).show();

        }

    };
    private View.OnClickListener tablerowOnClickListener3 = new View.OnClickListener() {
        public void onClick(View v) {
            //GET TEXT HERE
            SortingMethod = "Nb of times detected";

            Toast.makeText(DetectActivity.this, SortingMethod,
                    Toast.LENGTH_LONG).show();

        }

    };
    private View.OnClickListener tablerowOnClickListener4 = new View.OnClickListener() {
        public void onClick(View v) {
            //GET TEXT HERE
            SortingMethod = "Detection Range";

            Toast.makeText(DetectActivity.this, SortingMethod,
                    Toast.LENGTH_LONG).show();

        }

    };
    private View.OnClickListener tablerowOnClickListener5 = new View.OnClickListener() {
        public void onClick(View v) {
            //GET TEXT HERE
            SortingMethod = "Total Detection Range";

            Toast.makeText(DetectActivity.this, SortingMethod,
                    Toast.LENGTH_LONG).show();

        }

    };


    //region TABLE_FUNCTIONS
    public void addDevice(Device device) {
        TableRow rowNew = new TableRow(this);

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

        TextView rowItem4 = new TextView(this);
        rowItem4.setText("" + device.getDetection_range());
        rowItem4.setGravity(Gravity.CENTER_HORIZONTAL);
        rowNew.addView(rowItem4);

        TextView rowItem5 = new TextView(this);
        rowItem5.setText("" + device.getTotal_detected_range());
        rowItem5.setGravity(Gravity.CENTER_HORIZONTAL);
        rowNew.addView(rowItem5);

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
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
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
        //manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
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
        ArrayList<Device> print = DM.GetPrintList(SortingMethod);

        table.removeAllViews();
        table.addView(rowTitles);

        for(int i = 0; i < print.size(); i++) {
            addDevice(print.get(i));
        }

        new AddDeviceInfoToServer().execute();
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

                ArrayList<Device> print = DM.GetPrintList(SortingMethod);

                table.removeAllViews();

                for(int i = 0; i < print.size(); i++){
                    addDevice(print.get(i));
                }

                TestDetect();
            }
        }, 5000);
    }
    //endregion



    private class AddDeviceInfoToServer extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected String doInBackground(String... urls) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            //The content of the array should be the the mac address, how many times, total time connected , the values below are for testing



            ArrayList<Device> list = DM.GetPrintList("");
            JSONArray devicesJSONArray = new JSONArray();
            for (int i=0;i<list.size();i++) {
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(list.get(i).getMAC_address());
                jsonArray.put(list.get(i).getNb_of_times_detected());
                jsonArray.put(list.get(i).getTotal_detected_range());
                devicesJSONArray.put(jsonArray);
            }
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            String mac_address = info.getMacAddress();
            params.add(new BasicNameValuePair("mac",mac_address));
            params.add(new BasicNameValuePair("data", devicesJSONArray.toString()));
            response = sh.makeServiceCall(url2, ServiceHandler.POST, params);
            return response;
        }
        protected void onPostExecute(String result) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.d("Testingg",result);
        }
    }

}
