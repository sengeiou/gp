package com.ubt.imlibv2.bean;

import com.ubtechinc.nets.BuildConfig;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

public class Repository {
    protected String getIMLoginUrl(HashMap<String, String> params) {
        Iterator<String> keys = params.keySet().iterator();
        Iterator<String> values = params.values().iterator();
        StringBuilder stringBuilder = new StringBuilder(BuildConfig.IM_HOST + "getInfo");
        stringBuilder.append("?");

        for (int i = 0; i < params.size(); i++) {
            String value = null;
            try {
                value = URLEncoder.encode(values.next(), "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

            stringBuilder.append(keys.next() + "=" + value);
            if (i != params.size() - 1) {
                stringBuilder.append("&");
            }

        }
        return stringBuilder.toString();
    }
}
