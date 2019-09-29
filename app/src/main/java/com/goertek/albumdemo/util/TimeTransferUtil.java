package com.goertek.albumdemo.util;

/**
 * Created by clara.tong on 2019/4/23
 */
public class TimeTransferUtil {
    /**
     * 根据秒数,获得格式为00:00的时间
     *
     * @param timems 单位ms
     * @return
     */
    public static String getTimeStrByMin(int timems) {
        int time = timems/1000;
        if (time <= 0) {
            return "00:00";
        }
        String strMin = "";
        String strSec = "";
        int min = (time / 60);
        int sec = (time % 60);
        if (min < 10) {
            strMin = "0" + min;
        } else {
            strMin = "" + min;
        }
        if (sec < 10) {
            strSec = "0" + sec;
        } else {
            strSec = "" + sec;
        }
        return strMin + ":" + strSec;
    }

    public static String getTimeStrByMin(long timems) {
        long time = timems/1000;
        if (time <= 0) {
            return "00:00";
        }
        String strMin = "";
        String strSec = "";
        long min = (time / 60);
        long sec = (time % 60);
        if (min < 10) {
            strMin = "0" + min;
        } else {
            strMin = "" + min;
        }
        if (sec < 10) {
            strSec = "0" + sec;
        } else {
            strSec = "" + sec;
        }
        return strMin + ":" + strSec;
    }

}
