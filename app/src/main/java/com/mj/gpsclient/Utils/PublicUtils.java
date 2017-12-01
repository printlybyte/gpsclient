package com.mj.gpsclient.Utils;

import com.mj.gpsclient.model.Devices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ${王sir} on 2017/8/30.
 * application
 */

public class PublicUtils {

    /**
     * 将设备按在线 不在线 排序
     *
     * @param allDeviceList
     * @return
     */
    public static List<Devices> SetOrderForDevices(List<Devices> allDeviceList) {

        List<Devices> arraysOn = new ArrayList<Devices>();
        List<Devices> arraysOff = new ArrayList<Devices>();
        for (Devices bean : allDeviceList) {
            if (bean.getLineStatus().equals("在线")) {
                arraysOn.add(bean);
            } else {
                arraysOff.add(bean);
            }
        }
        arraysOn.addAll(arraysOff);
        return arraysOn;
    }
    /**
     * 获取当前时间
     * @return
     */
    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        return time;
    }
}
