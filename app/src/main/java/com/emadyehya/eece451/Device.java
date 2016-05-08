package com.emadyehya.eece451;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Emad Yehya on 4/9/2016.
 */
public class Device {

    private String MAC_address, phone_number, nickname;
    private String name; //takes the value of nickname, phone number, or MAC address in that order

    private int nb_of_times_detected; //total number of times seen
    private long last_detected_time;  //in ms
    private int detection_range;      //how long the current detection has been going on, in ms
    private int total_detected_range; //how long we've detected it, over all time

    private boolean detected_in_cycle;

    //region GETTERS
    public String getMAC_address(){return name;}
    public String getLast_detected_time(){return new SimpleDateFormat("KK:mm.ss a").format(new Date(last_detected_time));}
    public String getNb_of_times_detected(){return String.valueOf(nb_of_times_detected);}
    public int getDetection_range() {return detection_range;}
    public void setDetection_range(int detection_range) {this.detection_range = detection_range;}
    public int getTotal_detected_range() {return total_detected_range;}
    public void setTotal_detected_range(int total_detected_range) {this.total_detected_range = total_detected_range;}

    public boolean equals(Device d1) {
        if (this.name.equals( d1.name))
            return true;
        return false;
    }

    public Device(String MAC_address, String phone_number, String nickname){
        this.MAC_address = MAC_address;
        this.phone_number = phone_number;
        this.nickname = nickname;

        nb_of_times_detected = 0;
        last_detected_time = System.currentTimeMillis();
        detection_range = 0;
        total_detected_range = 0;

        if(nickname != "") name = nickname;
        else{
            if(phone_number != "") name = phone_number;
            else name = MAC_address;
        }
    }


    /***
     * Called when a device that has previously been detected is detected again.
     * @param was_previous is set to true if it were detected in the last detection, false if it were
     *                     detected in a previous one (ie disconnect then connect)
     */
    public void NewDetection(boolean was_previous){
        nb_of_times_detected++;
        long curr_time = System.currentTimeMillis();
        if(was_previous){
            total_detected_range += (curr_time - last_detected_time);
            detection_range += (curr_time - last_detected_time);
        } else {
            total_detected_range = 0;
        }
        last_detected_time = curr_time;
    }


    public void NotDetected(){
        long curr_time = System.currentTimeMillis();
        total_detected_range += (curr_time - last_detected_time);
    }

    public String toString() {
        String s;
        s ="Name " + name + " Mac_Address " + MAC_address + " Number " + phone_number + "Nick Name " + nickname;
        return s;
    }




}
