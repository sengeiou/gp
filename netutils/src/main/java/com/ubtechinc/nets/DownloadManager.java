package com.ubtechinc.nets;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.ubtech.utilcode.utils.CloseUtils;
import com.ubtech.utilcode.utils.FileUtils;
import com.ubtech.utilcode.utils.SDCardUtils;
import com.ubtech.utilcode.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @desc : 下载管理,支持断点续传
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/6/19
 * @modifier:
 * @modify_time:
 */

public final class DownloadManager {
    private static final AtomicReference<DownloadManager> INSTANCE = new AtomicReference<>();
    private static final String CACHE_DIR = "cache/download";
    private HashMap<String, Call> downCalls = new HashMap<>();
    private OkHttpClient mClient;

    public static DownloadManager getInstance() {
        for (;;) {
            DownloadManager current = INSTANCE.get();
            if (current != null) {
                return current;
            }
            current = new DownloadManager();
            if (INSTANCE.compareAndSet(null, current)) {
                return current;
            }
        }
    }

    private DownloadManager() {
        downCalls = new HashMap<>();
        mClient = new OkHttpClient.Builder().build();
    }


    public void download(String url, final DownloadListener listener){
        download(url, null, 0, listener);
    }


    public void download(final String url, final String filePath, final long size, final DownloadListener listener) {
        if(listener != null){
            listener.onStart();
        }

        Observable.just(url)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return !downCalls.containsKey(url);
                    }
                })
                .flatMap(new Function<String, ObservableSource<DownloadInfo>>() {
                    @Override
                    public ObservableSource<DownloadInfo> apply(String s) {
                        if (size == 0) {
                            return Observable.just(createDownInfo(url));
                        } else {
                            return Observable.just(createDownInfo(url, size));
                        }
                    }
                })

                .map(new Function<DownloadInfo, DownloadInfo>() {
                    @Override
                    public DownloadInfo apply(DownloadInfo downloadInfo) {
                        if (!TextUtils.isEmpty(filePath)){
                            appointPath(downloadInfo, filePath);
                        }
                        return setDownloadFilePath(downloadInfo);
                    }
                })
                .flatMap(new Function<DownloadInfo, ObservableSource<DownloadInfo>>() {
                    @Override
                    public ObservableSource<DownloadInfo> apply(DownloadInfo downloadInfo) throws Exception {
                        return Observable.create(transform(downloadInfo));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber(listener));
    }

    @NonNull
    private ObservableOnSubscribe<DownloadInfo> transform(final DownloadInfo info) {
        return new ObservableOnSubscribe<DownloadInfo>() {
            @Override
            public void subscribe(ObservableEmitter<DownloadInfo> emitter) {
                String url = info.getUrl();
                long curLength = info.getLength();

                if (info.getTotal() == DownloadInfo.TOTAL_ERROR){
                    emitter.onError(new IOException("不能获取文件长度..."));
                    return;
                }
                emitter.onNext(info);
                Request request = new Request.Builder()

                        .url(url)
                        .build();
                Call call = mClient.newCall(request);
                downCalls.put(url, call);
                Response response;

                try {
                    response = call.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    emitter.onError(e);
                    return;
                }

                File file = new File(info.getFilePath());
                InputStream is = null;
                FileOutputStream fileOutputStream = null;
                try {
                    is = response.body().byteStream();
                    fileOutputStream = new FileOutputStream(file, true);
                    byte[] buffer = new byte[1024 * 300];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                        curLength += len;
                        info.setLength(curLength);
                        emitter.onNext(info);
                    }
                    fileOutputStream.flush();
                    downCalls.remove(url);
                } catch (IOException e) {
                    e.printStackTrace();
                    emitter.onError(e);
                    return;
                } finally {
                    CloseUtils.closeIOQuietly(fileOutputStream, is);
                }
                emitter.onComplete();
            }

            /*@Override
            public void call(rx.Subscriber subscriber) {
                String url = info.getUrl();
                long curLength = info.getLength();

                if (info.getTotal() == DownloadInfo.TOTAL_ERROR){
                    subscriber.onError(new IOException("不能获取文件长度..."));
                    return;
                }
                subscriber.onNext(info);
                Request request = new Request.Builder()

                        .url(url)
                        .build();
                Call call = mClient.newCall(request);
                downCalls.put(url, call);
                Response response;

                try {
                    response = call.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                    return;
                }

                File file = new File(info.getFilePath());
                InputStream is = null;
                FileOutputStream fileOutputStream = null;
                try {
                    is = response.body().byteStream();
                    fileOutputStream = new FileOutputStream(file, true);
                    byte[] buffer = new byte[1024 * 300];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                        curLength += len;
                        info.setLength(curLength);
                        subscriber.onNext(info);
                    }
                    fileOutputStream.flush();
                    downCalls.remove(url);
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                    return;
                } finally {
                    CloseUtils.closeIOQuietly(fileOutputStream, is);
                }
                subscriber.onCompleted();
            }*/
        };
    }

    public void cancel(String url) {
        Call call = downCalls.get(url);
        if (call != null) {
            call.cancel();
        }
        downCalls.remove(url);
    }

    private class Subscriber implements Observer<DownloadInfo> {
        private volatile boolean completed;
        private final DownloadListener listener;

        public Subscriber(DownloadListener listener) {
            this.listener = listener;
        }

        @Override
        public void onComplete() {

        }

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onError(Throwable e) {
            completed = true;
            if (listener != null)
                listener.onError(e);
        }

        @Override
        public void onNext(DownloadInfo downloadInfo) {
            if (listener == null) return;
            listener.onProcess((int) ((downloadInfo.getLength() * 1.0 / downloadInfo.getTotal()) * 100));
            if (downloadInfo.getTotal() <= downloadInfo.getLength() && !completed) {
                completed = true;
                listener.onCompleted(downloadInfo);
            }
        }
    }


    public void appointPath(DownloadInfo downloadInfo, String filePath){
        if (downloadInfo != null && !TextUtils.isEmpty(filePath)){
            downloadInfo.setFilePath(filePath);
        }
    }

    private DownloadInfo createDownInfo(String url, long size) {
        DownloadInfo downloadInfo = new DownloadInfo(url);
        long contentLength;
        if (size != 0) {
            contentLength = size;
        } else {
            contentLength = getContentLength(url);
        }
        downloadInfo.setTotal(contentLength);
        String fileName = url.substring(url.lastIndexOf("/"));
        downloadInfo.setFilePath(getCacheDir() + fileName);
        return downloadInfo;
    }

    private DownloadInfo createDownInfo(String url) {
        DownloadInfo downloadInfo = new DownloadInfo(url);
        long contentLength = getContentLength(url);
        downloadInfo.setTotal(contentLength);
        String fileName = url.substring(url.lastIndexOf("/"));
        downloadInfo.setFilePath(getCacheDir()+ fileName);
        return downloadInfo;
    }

    private long getContentLength(String downloadUrl){
        Request request = new Request.Builder()
                .url(downloadUrl)
                .method("HEAD", null)//只获取文件大小
                .build();
        Response response = null;
        try {
            response = mClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength == 0 ? DownloadInfo.TOTAL_ERROR : contentLength;
        }
        return DownloadInfo.TOTAL_ERROR;
    }

    private DownloadInfo setDownloadFilePath(DownloadInfo downloadInfo) {
        String filePath = downloadInfo.getFilePath();
        long downloadLength = 0, contentLength = downloadInfo.getTotal();
        String base = getCacheDir();
        File file = new File(filePath);
        String fileName = file.getName();
        if (FileUtils.createOrExistsFile(file)) {
            downloadLength = file.length();
        }
        int i = 1;
        while (downloadLength >= contentLength) {//重新下载
            int dotIndex = fileName.lastIndexOf(".");
            String fileNameOther;
            if (dotIndex == -1) {
                fileNameOther = fileName + "(" + i + ")";
            } else {
                fileNameOther = fileName.substring(0, dotIndex)
                        + "(" + i + ")" + fileName.substring(dotIndex);
            }
            File newFile = new File(base, fileNameOther);
            file = newFile;
            downloadLength = newFile.length();
            i++;
        }
        downloadInfo.setLength(downloadLength);
        downloadInfo.setFilePath(file.getAbsolutePath());
        return downloadInfo;
    }

    private String getCacheDir() {
        return SDCardUtils.isSDCardEnable()? SDCardUtils.getSDCardPath() + CACHE_DIR  : Utils.getContext().getCacheDir().getAbsolutePath() + "/" + CACHE_DIR ;
    }

    public interface DownloadListener {
        void onStart();
        void onCompleted(DownloadInfo info);
        void onError(Throwable e);
        void onProcess(int progress);
    }
}
