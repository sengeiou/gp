package com.ubtechinc.nets.im.business;

import android.util.Log;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpDNSUtil {
    /**
     * 转换url 主机头为ip地址
     *
     * @param url  原url
     * @param host 主机头
     * @param ip   服务器ip
     * @return
     */
    public static String getIpUrl(String url, String host, String ip) {
        if (url == null) {
            Log.e("TAG", "URL NULL");
        }
        if (host == null) {
            Log.e("TAG", "host NULL");
        }
        if (ip == null) {
            Log.e("TAG", "ip NULL");
        }
        if (url == null || host == null || ip == null) return url;
        String ipUrl = url.replaceFirst(host, ip);
        return ipUrl;
    }

    /**
     * 根据url获得ip,此方法只是最简单的模拟,实际情况很复杂,需要做缓存处理
     *
     * @param host
     * @return
     */
    public static void getIPByHost(String host, Callback callback) {
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("http")
                .host("119.29.29.29")
                .addPathSegment("d")
                .addQueryParameter("dn", host)
                .build();
        //与我们正式请求独立，所以这里新建一个OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(httpUrl)
                .get()
                .build();

        Call call = okHttpClient.newCall(request);

        call.enqueue(callback);
    }
}