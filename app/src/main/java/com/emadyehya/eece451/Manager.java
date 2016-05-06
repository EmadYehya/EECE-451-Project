package com.emadyehya.eece451;

/**
 * Created by Emad Yehya on 4/9/2016.
 */
public class    Manager {
    private static Manager _instance = null;
    public DeviceManager DM;
    public String MAC;

    protected Manager() {
        DM = new DeviceManager();
    }

    public static Manager getInstance(){
        if(_instance == null) _instance = new Manager();
        return _instance;
    }

}	