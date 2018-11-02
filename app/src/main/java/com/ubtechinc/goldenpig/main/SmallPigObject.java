package com.ubtechinc.goldenpig.main;

import android.app.Activity;
import android.content.Context;
import android.webkit.JavascriptInterface;

import com.ubtechinc.goldenpig.route.ActivityRoute;

import java.util.HashMap;

public class SmallPigObject {

    private Context mContext;

    public SmallPigObject(Context context) {
        this.mContext = context;
    }

    @JavascriptInterface
    public void openNewPage(String param) {
        HashMap<String, String> map = new HashMap<>();
        map.put("url", param);
        ActivityRoute.toAnotherActivity((Activity) mContext, SkillDetailActivity.class, map, false);
    }

}
