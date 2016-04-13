package com.emadyehya.eece451;

import java.util.ArrayList;
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

    public DeviceManager(){
        previous = new ArrayList<Device>(); //list of devices detected in last session
        old = new ArrayList<Device>();      //list of devices detected in this session
        used = new ArrayList<Boolean>();    //used to check if old devices were redetected
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



        Device d2 = new Device (MAC_address, phone_number, nickname);
        previous.add(d2);
        d2.NewDetection(false);





    }
}
