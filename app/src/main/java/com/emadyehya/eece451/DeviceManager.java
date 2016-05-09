package com.emadyehya.eece451;

import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Emad Yehya on 4/9/2016.
 *
 * Used to simplify the code in DetectActivity
 */
public class DeviceManager {
    private List<Device> previous;
    private List<Device> old;
    private List<Boolean> used;
    private Context context;

    private boolean blocked = false;

    String m_Text = ""; //exists because ghazi doesn't know how to write code...


    public DeviceManager(){
        previous = new ArrayList<Device>(); //list of devices detected in last session
        old = new ArrayList<Device>();      //list of devices detected in this session
        used = new ArrayList<Boolean>();    //used to check if old devices were redetected
        new IntializeSessionTask().execute();

    }

    public void SetContext(Context context){
        this.context = context;
    }

    /**
     * called when we have the entire list. Called once per detection cycle.
     * When Session is done, have to call EndSession()
     */
    public void StartSession(){
        for(int i = 0; i < previous.size(); i++) used.add(false);
    }

    /***
     * Called when we are done with the detection cycle. Has to come after a StartSession()
     * Updates previous and old to make them ready for next cycle
     */
    public void EndSession(){
        List<Integer> remove = new ArrayList<Integer>();
        for(int i = 0; i < used.size(); i++) {
            if(!used.get(i)) remove.add(i);
        }
        for(int i = remove.size()-1; i >= 0; i--){
            previous.get(i).NotDetected();
            old.add(previous.get(i));
            previous.remove(i);
        }
        used.clear();
    }

    /***
     * Called when a device is detected within a session. The three parameters passed are to identify
     * the device if it existed previously or to define it otherwise.
     * @param MAC_address
     * @param phone_number
     * @param nickname
     */
    public void DeviceDetected(String MAC_address, String phone_number, String nickname){
        /**
         * Check if this device exists in previous. If it does:
         *                                  - Call NewDetectino(true) on that device
         *                                  - Set used[i] = true where the device is previous[i]
         * If not:
         *                                  - Create a new device
         *                                  - Add it to the end of pervious
         *                                  - Call NewDetection(false)
         */
        Device d1;
        d1 = new Device(MAC_address,phone_number,nickname);
        //previous
        for (int j = 0; j < previous.size(); j++) {
            if (d1.equals(previous.get(j))) {
                d1 = previous.get(j);
                d1.NewDetection(true);
                used.set(j, true);
                return;
            }
        }

        for(int j = 0; j < old.size(); j++){
            if(d1.equals(old.get(j))){
                d1 = old.get(j);
                d1.NewDetection(false);
                old.remove(j);
                previous.add(d1);
                return;
            }
        }

        blocked = true;

        //nickname = NewUserDialog(MAC_address);
        Device d2 = new Device (MAC_address, phone_number, nickname);
        previous.add(d2);
        d2.NewDetection(false);

    }

    ///*
    class compareFunction_Mac implements Comparator<Device> {

        public int compare(Device d1, Device d2) {
            //System.out.println();
            //Log.d("", d1.getMAC_address() + " " + d2.getMAC_address() + " " +d1.getMAC_address().compareTo(d2.getMAC_address()));



            return d1.getMAC_address().compareTo(d2.getMAC_address()) ;

        }

    }


    class compareFunction_Times_Detected implements Comparator<Device> {

        public int compare(Device d1, Device d2) {

            return d1.getNb_of_times_detected().compareTo(d2.getNb_of_times_detected()) ;

        }

    }

    class compareFunction_detection_range implements Comparator<Device> {

        public int compare(Device d1, Device d2) {
            return d1.getDetection_range() - d2.getDetection_range() ;

        }
    }


    class compareFunction_last_time implements Comparator<Device> {

        public int compare(Device d1, Device d2) {

            return d1.getLast_detected_time().compareTo(d2.getLast_detected_time()) ;

        }

    }

    class compareFunction_total_time implements Comparator<Device> {

        public int compare(Device d1, Device d2) {

            return d1.getTotal_detected_range() - d2.getTotal_detected_range() ;

        }

    }

    //*/

    public ArrayList<Device> GetPrintList(String S){

        //String S = "MAC Address";
        ArrayList<Device> ret = new ArrayList<>();

        //TODO: Emad, fix it so that it sorts before printing.
        for(int i = 0; i < previous.size(); i++){
            ret.add(previous.get(i));
        }
        for(int i = 0; i < old.size(); i++) {
            ret.add(old.get(i));
        }

        ///*
        if(S.equals("MAC Address"))
        {

            Collections.sort(ret, new compareFunction_Mac());

            //String S2= "A";
        }



        else if (S.equals("Total Detection Range"))
        {

            Collections.sort(ret, new compareFunction_total_time());
        }


        else if (S.equals("Detection Range"))
        {

            Collections.sort(ret, new compareFunction_detection_range());

        }

        else if(S.equals("Last detected time"))
        {

            Collections.sort(ret, new compareFunction_last_time());

        }


        else if (S.equals("Nb of times detected"))
        {

            Collections.sort(ret, new compareFunction_Times_Detected());

        }
        //*/
        return ret;
    }

    //region DIALOGS
    public String NewUserDialog (String MAC_address) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Do you want to add a nickname for " + MAC_address + "?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NewDeviceNick();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();

        return m_Text;
    }
    public String NewDeviceNick () {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Nickname");


        final EditText input = new EditText(context);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                Log.d("Displaythingngng", m_Text);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

        return m_Text;

    }
    public void DeviceDialog (Device d1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(d1.toString());
        builder.setTitle("Device Info");

        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });


    }

    //endregion

    private class IntializeSessionTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected String doInBackground(String... urls) {
            String response;
            String url = "http://emadyehya.com/init_session";
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            String mac_address = info.getMacAddress();
            Manager.getInstance().MAC=mac_address;

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add (new BasicNameValuePair("mac", mac_address));
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
