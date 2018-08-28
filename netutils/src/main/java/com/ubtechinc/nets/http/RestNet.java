package com.ubtechinc.nets.http;

import android.content.Context;
import android.util.Log;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.SDCardUtils;
import com.ubtechinc.nets.http.cookie.CookieCache;
import com.ubtechinc.nets.http.cookie.CookieManager;

import org.reactivestreams.Subscription;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import io.reactivex.Observable;

/**
 * @desc : 默认Gson+RxJava
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/5/15
 * @modifier:
 * @modify_time:
 */

public final class RestNet {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final Retrofit retrofit;
    private final OkHttpClient client;
    private final RestApi mRestApi;
    private final UploadApi mUploadApi;
    private final Context mContext;
    private CookieManager cookieMgr;

    RestNet(Context context, Retrofit.Builder retroBuilder, OkHttpClient.Builder clientBuilder, boolean cookiable) {
        this.mContext = context.getApplicationContext();
        if (cookiable) {
            cookieMgr = new CookieManager(new CookieCache(mContext));
            clientBuilder.cookieJar(cookieMgr);
        }
        this.client = clientBuilder.build();
        retroBuilder.client(client);
        this.retrofit = retroBuilder.build();
        this.mRestApi = retrofit.create(RestApi.class);
        this.mUploadApi = retrofit.create(UploadApi.class);
    }

    public void doPostWithForm(String url, HashMap<String, String> maps, RawCallback callback) {
        mRestApi.doPostWithForm(url, maps)
                .compose(schedulersTransformer)
                .compose(exceptionTransformer)
                .subscribe(new InnerSubscriber(callback));
    }

    public void doPostWithJson(String url, String json, RawCallback callback) {
        mRestApi.doPostWithJson(url, RequestBody.create(JSON, json.getBytes()))
                .compose(schedulersTransformer)
                .compose(exceptionTransformer)
                .subscribe(new InnerSubscriber(callback));
    }

    public void doGet(String url, HashMap<String, String> maps, RawCallback callback) {
        mRestApi.doGet(url, maps)
                .compose(schedulersTransformer)
                .compose(exceptionTransformer)
                .subscribe(new InnerSubscriber(callback));
    }

    public void doDelete(String url, HashMap<String, String> maps, RawCallback callback) {
        mRestApi.doDelete(url, maps)
                .compose(schedulersTransformer)
                .compose(exceptionTransformer)
                .subscribe(new InnerSubscriber(callback));
    }

    public void doDelete(String url, RawCallback callback) {
        mRestApi.doDelete(url)
                .compose(schedulersTransformer)
                .compose(exceptionTransformer)
                .subscribe(new InnerSubscriber(callback));
    }

    public void doPutWithForm(String url, Map<String, String> maps, RawCallback callback) {
        mRestApi.doPutWithForm(url, maps)
                .compose(schedulersTransformer)
                .compose(exceptionTransformer)
                .subscribe(new InnerSubscriber(callback));
    }

    public void doPutWithJson(String url, String json, RawCallback callback) {
        mRestApi.doPutWithJson(url, RequestBody.create(JSON, json.getBytes()))
                .compose(schedulersTransformer)
                .compose(exceptionTransformer)
                .subscribe(new InnerSubscriber(callback));
    }

    public void upLoadImage(String url, String description, RequestBody image, RawCallback callback) {
        mUploadApi.upLoadImage(url, description, image)
                .compose(schedulersTransformer)
                .compose(exceptionTransformer)
                .subscribe(new InnerSubscriber(callback));
    }

    public void upLoadFile(String url, String description, RequestBody file, RawCallback callback) {
        mUploadApi.upLoadFile(url, description, file)
                .compose(schedulersTransformer)
                .compose(exceptionTransformer)
                .subscribe(new InnerSubscriber(callback));
    }


    public void doPostWithJsonAndHeader(String url, String json, Map<String, String> headers, RawCallback callback) {
        mRestApi.doPostWithJsonAndHeader(url, RequestBody.create(JSON, json.getBytes()), headers)
                .compose(schedulersTransformer)
                .compose(exceptionTransformer)
                .subscribe(new InnerSubscriber(callback));
    }

    public void doPutWithJsonAndHeader(String url, String json, Map<String, String> headers, RawCallback callback) {
        mRestApi.doPutWithJsonAndHeader(url, RequestBody.create(JSON, json.getBytes()), headers)
                .compose(schedulersTransformer)
                .compose(exceptionTransformer)
                .subscribe(new InnerSubscriber(callback));
    }


    public void doGetWithHeader(String url, HashMap<String, String> maps,Map<String,String> headers, RawCallback callback) {
        mRestApi.doGetWithHeader(url, maps,headers)
                .compose(schedulersTransformer)
                .compose(exceptionTransformer)
                .subscribe(new InnerSubscriber(callback));
    }

    public void doPatch(String url, String json,  RawCallback callback){
        mRestApi.doPatch(url, RequestBody.create(JSON, json.getBytes()))
                .compose(schedulersTransformer)
                .compose(exceptionTransformer)
                .subscribe(new InnerSubscriber(callback));
    }


    public void doPatchWithJsonAndHeaders(String url, String json, Map<String, String> maps,RawCallback callback){
        mRestApi.doPatchWithJsonAndHeaders(url, RequestBody.create(JSON, json.getBytes()), maps)
                .compose(schedulersTransformer)
                .compose(exceptionTransformer)
                .subscribe(new InnerSubscriber(callback));
    }


    private final ObservableTransformer schedulersTransformer = new ObservableTransformer() {
        /*@Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }*/

        @Override
        public ObservableSource apply(Observable upstream) {
            return ((Observable) upstream).subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    private final ObservableTransformer exceptionTransformer = new ObservableTransformer() {

        @Override
        public ObservableSource apply(Observable upstream) {
            return ((Observable) upstream).onErrorResumeNext(new HttpResponseFunc());
        }

        /*@Override
        public Object call(Object observable) {
            return ((Observable) observable).onErrorResumeNext(new HttpResponseFunc());
        }*/
    };

    private static class HttpResponseFunc implements Function<Throwable, Observable> {
        /*@Override
        public Observable call(Throwable t) {
            return Observable.error(ErrorUtil.handleException(t));
        }*/

        @Override
        public Observable apply(Throwable throwable) {
            return Observable.error(ErrorUtil.handleException(throwable));
        }
    }

    private static class InnerSubscriber implements Observer<ResponseBody> {

        private final RawCallback callback;

        public InnerSubscriber(RawCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onComplete() {
            LogUtils.w("onCompleted");
        }

        @Override
        public void onError(Throwable e) {
            LogUtils.w("onError");
            if (callback != null) {
                if (e instanceof ThrowableWrapper) {
                    callback.onError((ThrowableWrapper) e);
                } else {
                    callback.onError(ErrorUtil.handleException(e));
                }
            }
        }

        @Override
        public void onNext(ResponseBody responseBody) {
            LogUtils.w("onNext--callback = " + callback);
            if (callback == null) return;
            try {
                byte[] rawBytes = responseBody.bytes();
                callback.onSuccess(rawBytes);
            } catch (IOException e) {
                callback.onError(ErrorUtil.handleException(e));
            } finally {
                responseBody.close();
            }
        }
    }

    public static final class Builder {
        private static final long CACHE_SIZE = 10 * 1024 * 1024;
        private Context cxt;

        //for retrofit
        private final Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        private String baseUrl;
        private Converter.Factory converterFactory;
        private CallAdapter.Factory callAdapterFactory;
        private Cache cache;

        //for okhttp
        private final OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();
        private HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

        //for RestNet
        private boolean cookiable = false;
        private boolean cacheable = false;
        private String cacheDirName = null;

        public Builder(Context context) {
            this.cxt = context;
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            okhttpBuilder.addNetworkInterceptor(loggingInterceptor);
        }

        //////////////okhttpclient////////////
        public Builder connectTimeout(int timeout, TimeUnit unit) {
            okhttpBuilder.connectTimeout(timeout, unit);
            return this;
        }

        public Builder writeTimeout(int timeout, TimeUnit unit) {
            okhttpBuilder.writeTimeout(timeout, unit);
            return this;
        }

        public Builder readTimeout(int timeout, TimeUnit unit) {
            okhttpBuilder.readTimeout(timeout, unit);
            return this;
        }

        public Builder proxy(Proxy proxy) {
            okhttpBuilder.proxy(Utils.checkNotNull(proxy, "proxy == null"));
            return this;
        }

        public Builder connectPool(ConnectionPool pool) {
            okhttpBuilder.connectionPool(Utils.checkNotNull(pool, "pool == null"));
            return this;
        }

        public Builder addHeaders(Map<String, String> headers) {
            okhttpBuilder.addInterceptor(new HeaderInterceptor(Utils.checkNotNull(headers, "header == null")));
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            okhttpBuilder.addInterceptor(Utils.checkNotNull(interceptor, "interceptor == null"));
            return this;
        }

        public Builder addNetworkInterceptor(Interceptor interceptor) {
            okhttpBuilder.addNetworkInterceptor(interceptor);
            return this;
        }

        /////////////retrofit////////////
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        //for restnet
        public Builder cookiable(boolean enable) {
            this.cookiable = enable;
            return this;
        }

        public Builder cacheable(boolean enable) {
            this.cacheable = enable;
            return this;
        }

        public Builder loggable(boolean enable) {
            if (enable) {
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            } else {
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            }
            return this;
        }

        public Builder cacheDirName(String cacheDirName) {
            this.cacheDirName = cacheDirName;
            return this;
        }


        public RestNet build() {
            if (baseUrl == null) {
                throw new IllegalStateException("Base URL required.");
            }
            retrofitBuilder.baseUrl(baseUrl);

            if (converterFactory == null) {
                converterFactory = GsonConverterFactory.create();
            }
            retrofitBuilder.addConverterFactory(converterFactory);

            if (callAdapterFactory == null) {
                callAdapterFactory = RxJava2CallAdapterFactory.create();
            }
            retrofitBuilder.addCallAdapterFactory(callAdapterFactory);

            if (cacheable) {
                String cachePath = SDCardUtils.isSDCardEnable() ? SDCardUtils.getSDCardPath() : cxt.getCacheDir().getAbsolutePath() + "/";
                cachePath += cacheDirName == null ? "cache_http" : cacheDirName;
                if (cache == null) {
                    cache = new Cache(new File(cachePath), CACHE_SIZE);
                }
            }
            if (cache != null) {
                okhttpBuilder.cache(cache);
            }
            return new RestNet(cxt, retrofitBuilder, okhttpBuilder, cookiable);
        }
    }

    public void addInterceptor(Interceptor interceptor) {
        if (client != null) {
            client.interceptors().add(interceptor);
        }
    }
}
