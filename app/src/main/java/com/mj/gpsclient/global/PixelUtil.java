package com.mj.gpsclient.global;

import android.content.Context;
import android.content.res.Resources;

/**
 * dp和像素点转换
 *
 * @author MarkMjw
 */
public class PixelUtil {
	
    /**
     * dpתpx.
     *
     * @param value   the value
     * @param context the context
     * @return the int
     */
    public static float dp2px(float value, Context context) {
        final float scale = context.getResources().getDisplayMetrics().densityDpi;
        return value * (scale / 160) + 0.5f;
    }

    /**
     * pxתdp.
     *
     * @param value   the value
     * @param context the context
     * @return the int
     */
    public static float px2dp(float value, Context context) {
        final float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (value * 160) / scale + 0.5f;
    }

    /**
     * spתpx.
     *
     * @param value   the value
     * @param context the context
     * @return the int
     */
    public static float sp2px(float value, Context context) {
        Resources r;
        if (context == null) {
            r = Resources.getSystem();
        } else {
            r = context.getResources();
        }
        float spvalue = value * r.getDisplayMetrics().scaledDensity;
        return spvalue + 0.5f;
    }


    /**
     * pxתsp.
     *
     * @param value   the value
     * @param context the context
     * @return the int
     */
    public static float px2sp(float value, Context context) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return value / scale + 0.5f;
    }

}
