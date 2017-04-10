package com.leador.xcjly.jni;

/**
 * Created by a on 2017/1/19.
 */

public class MatchJni {

    static {
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("nonfree");
        System.loadLibrary("opencv_java");
        System.loadLibrary("match");
    }

    // 设置匹配影像
    public static native int setPointMatch(String a, String b, int c);
    // 查找同名点
    public static native int[] findLPointMatch(int x, int y, int c);
    // 释放资源
    public static native void release();

}
