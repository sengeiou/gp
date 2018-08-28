package com.ubtechinc.nets;

import android.content.Context;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtech.utilcode.utils.JsonUtils;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.nets.http.RawCallback;
import com.ubtechinc.nets.http.RestNet;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.http.Utils;

import java.lang.ref.SoftReference;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Interceptor;

/**
 * @desc : http/https 网络调用
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/5/16
 * @modifier:
 * @modify_time:
 */

public final class HttpManager {
    //这个值在HeaderInterceptor内被替换掉
    public static String BASE_URL = "http://10.10.1.43:8080/";
    public static final String IM_TAG = BASE_URL + "im";
    public static final String SER_TAG = BASE_URL + "alpha2-web";
    public static final String USER_TAG = BASE_URL + "user-service-rest";
    public static final String XINGE_TAG = BASE_URL + "xinge-push-rest";
    public static final String EQUIPMENTID_TAG = BASE_URL + "equipment";
    public static final String UPDATE  = BASE_URL + "v1/upgrade-rest";
    public static final String CREATION_TAG = BASE_URL + "creation";


    private static final String CACHE_DIR = "cache/http";

    private static HttpManager instance;
    private Context mContext;

    private RestNet mRestApi;
    private static final String TAG = "HttpManager";
    //private ArrayList<Subscription> subscriptions = new ArrayList<>();

    public static List<Interceptor> interceptors = new ArrayList<>();

    private HttpManager(Context context) {
        this.mContext = context.getApplicationContext();
        initialize(mContext);
    }

    private void initialize(Context context) {
        RestNet.Builder builder = new RestNet.Builder(context);
        builder.cacheable(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .loggable(true)
                .cookiable(true)
                .addInterceptor(GenericHeaders.createGenericHeaders(mContext))
                .cacheDirName(CACHE_DIR)
                .connectPool(new ConnectionPool(3, 8, TimeUnit.SECONDS))
                .baseUrl(BASE_URL);

        if (!CollectionUtils.isEmpty(interceptors)) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }
        mRestApi = builder.build();
    }

    public static HttpManager get(Context context) {
        if (instance != null) return instance;
        synchronized (HttpManager.class) {
            if (instance == null) instance = new HttpManager(context);
        }
        return instance;
    }

    public <T> void doGet(String url, HashMap<String, String> maps, final ResponseListener<T> listener) {
        Type[] types = listener.getClass().getGenericInterfaces();
        if (Utils.hasUnresolvableType(types[0])) {
            return;
        }
        Type type = Utils.getParameterUpperBound(0, (ParameterizedType) types[0]);

        mRestApi.doGet(url, maps, new ProxyRawCallback<T>(mContext, type, listener));
    }

    public <T> void doPostWithForm(String url, HashMap<String, String> maps, final ResponseListener<T> listener) {
        Type[] types = listener.getClass().getGenericInterfaces();
        if (Utils.hasUnresolvableType(types[0])) {
            return;
        }
        Type type = Utils.getParameterUpperBound(0, (ParameterizedType) types[0]);
        mRestApi.doPostWithForm(url, maps, new ProxyRawCallback<T>(mContext, type, listener));
    }

    public <T> void doPostWithJson(String url, String jsonStr, final ResponseListener<T> listener) {
        Type[] types = listener.getClass().getGenericInterfaces();
        if (Utils.hasUnresolvableType(types[0])) {
            return;
        }

        LogUtils.d(TAG, "url=" + url);

        Type type = Utils.getParameterUpperBound(0, (ParameterizedType) types[0]);
        mRestApi.doPostWithJson(url, jsonStr, new ProxyRawCallback<T>(mContext, type, listener));
    }

    public <T> void doPutWithForm(String url, HashMap<String, String> maps, final ResponseListener<T> listener) {
        Type[] types = listener.getClass().getGenericInterfaces();
        if (Utils.hasUnresolvableType(types[0])) {
            return;
        }
        Type type = Utils.getParameterUpperBound(0, (ParameterizedType) types[0]);
        mRestApi.doPutWithForm(url, maps, new ProxyRawCallback<T>(mContext, type, listener));
    }

    public <T> void doPutWithJson(String url, String jsonStr, final ResponseListener<T> listener) {
        Type[] types = listener.getClass().getGenericInterfaces();
        if (Utils.hasUnresolvableType(types[0])) {
            return;
        }
        Type type = Utils.getParameterUpperBound(0, (ParameterizedType) types[0]);
        mRestApi.doPutWithJson(url, jsonStr, new ProxyRawCallback<T>(mContext, type, listener));
    }

    public <T> void doPatchWithJsonAndHeaders(String url, String jsonStr, Map<String, String> maps, final ResponseListener<T> listener) {
        Type[] types = listener.getClass().getGenericInterfaces();
        if (Utils.hasUnresolvableType(types[0])) {
            return;
        }
        Type type = Utils.getParameterUpperBound(0, (ParameterizedType) types[0]);
        mRestApi.doPatchWithJsonAndHeaders(url, jsonStr, maps, new ProxyRawCallback<T>(mContext, type, listener));
    }

    public <T> void doPatchWithJson(String url, String jsonStr, final ResponseListener<T> listener) {
        Type[] types = listener.getClass().getGenericInterfaces();
        if (Utils.hasUnresolvableType(types[0])) {
            return;
        }
        Type type = Utils.getParameterUpperBound(0, (ParameterizedType) types[0]);
        mRestApi.doPatch(url, jsonStr, new ProxyRawCallback<T>(mContext, type, listener));
    }

    public <T> void doDelete(String url, final ResponseListener<T> listener) {
        Type[] types = listener.getClass().getGenericInterfaces();
        if (Utils.hasUnresolvableType(types[0])) {
            return;
        }
        Type type = Utils.getParameterUpperBound(0, (ParameterizedType) types[0]);
        mRestApi.doDelete(url, new ProxyRawCallback<T>(mContext, type, listener));
    }

    public <T> void doPostWithJsonAndHeader(String url, String jsonStr, Map<String, String> headers, final ResponseListener<T> listener) {
        Type[] types = listener.getClass().getGenericInterfaces();
        if (Utils.hasUnresolvableType(types[0])) {
            return;
        }

        LogUtils.d(TAG, "url=" + url);

        Type type = Utils.getParameterUpperBound(0, (ParameterizedType) types[0]);
        if (headers == null || headers.size() == 0) {
            mRestApi.doPostWithJson(url, jsonStr, new ProxyRawCallback<T>(mContext, type, listener));
        } else {
            mRestApi.doPostWithJsonAndHeader(url, jsonStr, headers, new ProxyRawCallback<T>(mContext, type, listener));
        }
    }


    public <T> void doPutWithJsonAndHeader(String url, String jsonStr, Map<String, String> headers, final ResponseListener<T> listener) {
        Type[] types = listener.getClass().getGenericInterfaces();
        if (Utils.hasUnresolvableType(types[0])) {
            return;
        }
        Type type = Utils.getParameterUpperBound(0, (ParameterizedType) types[0]);
        if (headers == null || headers.size() == 0) {
            mRestApi.doPutWithJson(url, jsonStr, new ProxyRawCallback<T>(mContext, type, listener));
        } else {
            mRestApi.doPutWithJsonAndHeader(url, jsonStr, headers, new ProxyRawCallback<T>(mContext, type, listener));
        }
    }

    private static final class ProxyRawCallback<T> implements RawCallback {

        private ResponseListener<T> softListener;
        private SoftReference<Context> soltCxt;
        private final Type type;

        public ProxyRawCallback(Context cxt, Type type, ResponseListener<T> listener) {
            softListener = listener;
            this.soltCxt = new SoftReference<Context>(cxt);
            this.type = type;
        }

        @Override
        public void onError(ThrowableWrapper e) {
            LogUtils.w(TAG, "onError error = " + e);
            ResponseListener<T> listener = softListener;
            if (listener != null) {
                listener.onError(e);
            }
        }

        @Override
        public void onSuccess(byte[] rawBytes) {

            ResponseListener<T> listener = softListener;
            if (listener == null) return;
            final String jsonStr = new String(rawBytes);
            LogUtils.d(TAG, "onSuccess json = " + jsonStr);
            listener.onSuccess((T) JsonUtils.getObject(jsonStr, type));
        }
    }

    public <T> void doGet(String url, HashMap<String, String> maps, Map<String, String> headers, final ResponseListener<T> listener) {
        Type[] types = listener.getClass().getGenericInterfaces();
        if (Utils.hasUnresolvableType(types[0])) {
            return;
        }
        Type type = Utils.getParameterUpperBound(0, (ParameterizedType) types[0]);

        mRestApi.doGetWithHeader(url, maps, headers, new ProxyRawCallback<T>(mContext, type, listener));
    }

    public void addInterceptors(Interceptor interceptor) {
        if (mRestApi != null) {
            mRestApi.addInterceptor(interceptor);
        }
    }
}
