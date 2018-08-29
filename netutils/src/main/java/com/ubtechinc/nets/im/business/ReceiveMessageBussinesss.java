package com.ubtechinc.nets.im.business;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.ubtech.utilcode.utils.FileUtils;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.notification.NotificationCenter;
import com.ubtechinc.alpha.event.ReceiveHeartBeatEvent;
import com.ubtechinc.alpha.im.IMsgHandleEngine;
import com.ubtechinc.alpha.im.ImMsgDispathcer;
import com.ubtechinc.nets.DownloadInfo;
import com.ubtechinc.nets.DownloadManager;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.Utils;
import com.ubtechinc.nets.im.service.MsgHandleTask;
import com.ubtechinc.nets.phonerobotcommunite.IReceiveMsg;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/25.
 */

public class ReceiveMessageBussinesss implements IReceiveMsg{

    private ExecutorService receiveMsgThreadPool;
    private DownloadListener downloadListener;
    private DownloadOriginListener mDownloadOriginListener;
    private DownloadHDPicListener mDownloadHDPicListener;
    private String mThumbPath;
    private String mOriginPath;
    private String mHDPath;

    private static ReceiveMessageBussinesss sInstance;
    private static String TAG = "ReceiveMessageBussines";
    public static ReceiveMessageBussinesss getInstance() {
        if (sInstance == null) {
            synchronized (ReceiveMessageBussinesss.class) {
                sInstance = new ReceiveMessageBussinesss();
            }
        }
        return sInstance;
    }

    private ReceiveMessageBussinesss() {
    }

    public interface DownloadHDPicListener{
        public void onHDSuccess(String fileName);
        public void onHDError(String fileName,int code);
    }

    public interface DownloadListener{
        public void onSuccess(String fileName);
        public void onError(String fileName,int code);
    }

    public interface DownloadOriginListener{
        public void onProgress(String fileName,int progress);
        public void onORiginSuccess(String fileName, String url);
        public void onORiginError(String fileName, Throwable e);
    }

    public void initStoragePath(String thumbPath, String hdPath,String mOriginPath){
        this.mThumbPath = thumbPath;
        this.mHDPath = hdPath;
        this.mOriginPath = mOriginPath;
    }


    public void setDownloadHDPicListener(DownloadHDPicListener listener){
        this.mDownloadHDPicListener = listener;
    }

    public void setDownloadListener(DownloadListener downloadListener1){
        this.downloadListener =downloadListener1 ;
    }

    public void setDownloadOriginListener(DownloadOriginListener downloadListener1){
        this.mDownloadOriginListener =downloadListener1 ;
    }

    public void removeDownloadListener(DownloadListener downloadListener) {
        if(this.downloadListener!= null && this.downloadListener == downloadListener){
            this.downloadListener = null;
        }
    }
    public void removeDownloadHDPicListener(DownloadHDPicListener downloadListener) {
        if(mDownloadHDPicListener!= null && mDownloadHDPicListener == downloadListener){
            mDownloadHDPicListener = null;
        }
    }
    public void removeDownloadOriginListener(DownloadOriginListener downloadOriginListener){
        if (mDownloadOriginListener != null && mDownloadOriginListener == downloadOriginListener){
            mDownloadOriginListener = null;
        }
    }

    @Override
    public void init() {

        receiveMsgThreadPool = Executors.newCachedThreadPool();
    }

    @Override
    public void setIMsgDispatcher(ImMsgDispathcer msgDispathcer){
        IMsgHandleEngine.getInstance().setIMsgDispatcher(msgDispathcer);
    }












}
