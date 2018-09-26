package com.ubtechinc.goldenpig.route;

import android.app.Activity;
import android.content.Intent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ActivityRoute {

    /**
     *@auther        :hqt
     *@description   :无参数数据的Activity之间的跳转
     *@param         :activity 源页面
     *@param         :cls 目的页面
     *@return        :
     *@exception     :
     */
    public static void toAnotherActivity(Activity activity,
                                           Class<? extends Activity> cls,boolean closeSlf) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
        if (closeSlf) {
            activity.finish();
        }
    }

    /**
     *@auther        :hqt
     *@description   :带数据的Activity之间的跳转
     *@param         :activity 源页面
     *@param         :cls 目的页面
     *@param         :hashMap 参数容器
     *@return        :
     *@exception     :
    */
    public static void toAnotherActivity(Activity activity,
                                           Class<? extends Activity> cls,
                                           HashMap<String, ? extends Object> hashMap,
                                           boolean closeSlf) {
        Intent intent = new Intent(activity, cls);
        Iterator<?> iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            @SuppressWarnings("unchecked")
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator
                    .next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                intent.putExtra(key, (String) value);
            }else if (value instanceof Boolean) {
                intent.putExtra(key, (boolean) value);
            }else if (value instanceof Integer) {
                intent.putExtra(key, (int) value);
            }else if (value instanceof Float) {
                intent.putExtra(key, (float) value);
            }
            else if (value instanceof Double) {
                intent.putExtra(key, (double) value);
            }else {
                intent.putExtra(key, (Serializable) value);
            }
        }
        activity.startActivity(intent);
        if (closeSlf){
            activity.finish();
        }
    }
}
