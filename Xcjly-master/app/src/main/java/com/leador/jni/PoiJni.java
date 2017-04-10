package com.leador.jni;

/**
 * Created by Administrator on 2017/2/28.
 */

public class PoiJni {

    public static native String callPoi(String path,String gpsName,String imuName,String navName,double utc);

    static {
        System.loadLibrary("poi");
    }
}
