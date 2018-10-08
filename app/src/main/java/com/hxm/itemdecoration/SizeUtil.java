package com.hxm.itemdecoration;

/**
 * Created by hxm on 2018/10/8
 * 描述：
 */
public class SizeUtil {

    public static int dp2px(float dp){
        float scale=AppContext.instance.getResources().getDisplayMetrics().density;
        return (int) (dp*scale+0.5f);
    }
}
