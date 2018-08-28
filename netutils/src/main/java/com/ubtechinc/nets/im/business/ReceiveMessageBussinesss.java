package com.ubtechinc.nets.im.business;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.TIMCallBack;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMImage;
import com.tencent.TIMImageElem;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMTextElem;
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
        TIMManager.getInstance().addMessageListener(onRevieveMessageListenenr);
        receiveMsgThreadPool = Executors.newCachedThreadPool();
    }

    @Override
    public void setIMsgDispatcher(ImMsgDispathcer msgDispathcer){
        IMsgHandleEngine.getInstance().setIMsgDispatcher(msgDispathcer);
    }

    private TIMMessageListener onRevieveMessageListenenr = new TIMMessageListener() {
        @Override
        public boolean onNewMessages(List<TIMMessage> list) {
            LogUtils.d("onNewMessages---");
            handleReceivedMessage(list);
            return true;
        }
    };

    /**
     * 处理接收到的IM消息
     * 从后台推送的消息是Json格式(IM给后台提供的接口不支持传byte[]),客户端与机器人互传的是byte[]格式，可转成Protobuffer
     * byte[] ---> String --->byte[]并不一定是等价的，会存在数据丢失
     * @param list
     */
    private void handleReceivedMessage(List<TIMMessage> list) {
        LogUtils.d(TAG,"handleReceivedMessage");
        for(TIMMessage timMessage :list){
            for(int i=0;i<timMessage.getElementCount();i++){

                String peer = timMessage.getConversation().getPeer();
                //发送已读回执
                TIMManager.getInstance().getConversation(TIMConversationType.C2C,peer).setReadMessage(timMessage);//set has readable!!

                TIMElem elem =timMessage.getElement(i);
                TIMElemType elemType= elem.getType();

                if(elemType == TIMElemType.Text){ //来自后台的IM消息和心跳包
                    final TIMTextElem textElem =(TIMTextElem)elem;
                    LogUtils.v("收到消息:"+textElem.getText());
                    String messageStr= textElem.getText();
                    if(messageStr.startsWith("alpha_heart_beat")) {
                        LogUtils.d("心跳包： "+messageStr);
                        //TODO:心跳包最好也改成Protobuffer格式
                        ReceiveHeartBeatEvent event = new ReceiveHeartBeatEvent();
                        event.message = messageStr;
                        NotificationCenter.defaultCenter().publish(event);
                    } else { //从后台
                        MsgHandleTask parserTask = new MsgHandleTask(messageStr,peer);
                        receiveMsgThreadPool.execute(parserTask);
                    }
                } else if (elemType == TIMElemType.Custom){
                    final TIMCustomElem customElem = (TIMCustomElem)elem;
                    byte[] data = customElem.getData();

                    LogUtils.v(TAG,"收到消息--type : custom,msg:"+data);
                    MsgHandleTask parserTask = new MsgHandleTask(data,peer);
                    receiveMsgThreadPool.execute(parserTask);
                }

                else if (elemType==TIMElemType.Image){
                    final TIMImageElem fileElem = (TIMImageElem)elem;
                    if(fileElem.getLevel()==1){
                        final TIMImage timImage = fileElem.getImageList().get(1);
                        Log.d(TAG,timImage.getUuid());
                        final String fileName = fileElem.getPath().substring(fileElem.getPath().lastIndexOf('/')+1);
                        final String path = mThumbPath + fileName;
                        FileUtils.createOrExistsFile(path);
                        final Uri uri = Uri.parse(timImage.getUrl());
                        HttpDNSUtil.getIPByHost(uri.getHost(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                downloadThumbWithUrl(timImage.getUrl(),fileName, path, timImage);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String originIP;
                                if (response.isSuccessful()) {
                                    String body = response.body().string();
                                    String[] ips = body.split(";");
                                    if (ips != null && ips.length > 0) {
                                        originIP = ips[0];
                                        String ipUrl = HttpDNSUtil.getIpUrl(timImage.getUrl(), uri.getHost(), originIP);
                                        downloadThumbWithUrl(ipUrl,fileName, path, timImage);
                                    }else{
                                        downloadThumbWithUrl(timImage.getUrl(),fileName, path, timImage);
                                    }
                                }else{
                                    downloadThumbWithUrl(timImage.getUrl(),fileName, path, timImage);
                                }
                            }
                        });
                        return ;
                    }
                    if(fileElem.getLevel()==0){
                        final TIMImage timImageOrigin= fileElem.getImageList().get(0);
                        final String fileName  = fileElem.getPath().substring(fileElem.getPath().lastIndexOf('/')+1);
                        final String pathOrigin = mOriginPath + fileName;
                        if (FileUtils.isFileExists(pathOrigin)){
                            FileUtils.deleteFile(pathOrigin);
                        }
                        FileUtils.createOrExistsFile(pathOrigin);
                        final Uri uri = Uri.parse(timImageOrigin.getUrl());
                        HttpDNSUtil.getIPByHost(uri.getHost(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                downloadOriginImgageWithUrl(timImageOrigin.getUrl(),fileName, pathOrigin, timImageOrigin);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String originIP;
                                if (response.isSuccessful()) {
                                    String body = response.body().string();
                                    String[] ips = body.split(";");
                                    if (ips != null && ips.length > 0) {
                                        originIP = ips[0];
                                        String ipUrl = HttpDNSUtil.getIpUrl(timImageOrigin.getUrl(), uri.getHost(), originIP);
                                        downloadOriginImgageWithUrl(ipUrl,fileName, pathOrigin, timImageOrigin);
                                    }else{
                                        downloadOriginImgageWithUrl(timImageOrigin.getUrl(),fileName, pathOrigin, timImageOrigin);
                                    }
                                }else{
                                    downloadOriginImgageWithUrl(timImageOrigin.getUrl(),fileName, pathOrigin, timImageOrigin);
                                }
                            }
                        });
                    }
                    if (fileElem.getLevel() == 2){
                        final TIMImage hdImage = fileElem.getImageList().get(2);
                        final String fileName  = fileElem.getPath().substring(fileElem.getPath().lastIndexOf('/')+1);
                        final String pathHD = mHDPath + fileName;
                        FileUtils.createOrExistsFile(pathHD);
                        final Uri uri = Uri.parse(hdImage.getUrl());
                        HttpDNSUtil.getIPByHost(uri.getHost(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                downloadHDImgageWithUrl(hdImage.getUrl(),fileName, pathHD, hdImage);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String originIP;
                                if (response.isSuccessful()) {
                                    String body = response.body().string();
                                    String[] ips = body.split(";");
                                    if (ips != null && ips.length > 0) {
                                        originIP = ips[0];
                                        String ipUrl = HttpDNSUtil.getIpUrl(hdImage.getUrl(), uri.getHost(), originIP);
                                        downloadHDImgageWithUrl(ipUrl,fileName, pathHD, hdImage);
                                    }else{
                                        downloadHDImgageWithUrl(hdImage.getUrl(),fileName, pathHD, hdImage);
                                    }
                                }else{
                                    downloadHDImgageWithUrl(hdImage.getUrl(),fileName, pathHD, hdImage);
                                }
                            }
                        });
                    }

                }
            }
        }
    }

    public void downloadThumbWithUrl(final String ipUrl, final String fileName, final String cachePath, TIMImage timImageOrigin){
        DownloadManager.getInstance().download(ipUrl, cachePath, timImageOrigin.getSize(), new DownloadManager.DownloadListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted(DownloadInfo info) {
                Log.i("Thumb", "im donwload onCompleted : " + fileName);
                if (downloadListener != null){
                    downloadListener.onSuccess(fileName);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i("Thumb", "im donwload onError : " + fileName);
                if (downloadListener != null){
                    downloadListener.onError(fileName, -1);
                }
                FileUtils.deleteFile(cachePath);
            }

            @Override
            public void onProcess(int progress) {

            }
        });
    }

    public void downloadHDImgageWithUrl(final String ipUrl, final String fileName, final String cachePath, TIMImage timImageOrigin){
        DownloadManager.getInstance().download(ipUrl, cachePath, timImageOrigin.getSize(), new DownloadManager.DownloadListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted(DownloadInfo info) {
                Log.i("hdImage", "download message onCompleted :" + fileName);
                if (mDownloadHDPicListener != null){
                    mDownloadHDPicListener.onHDSuccess(fileName);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i("hdImage", "onError : " + fileName);
                if (mDownloadHDPicListener != null){
                    mDownloadHDPicListener.onHDError(fileName, -1);
                }
                FileUtils.deleteFile(cachePath);
            }

            @Override
            public void onProcess(int progress) {

            }
        });
    }



    private void downloadOriginImgageWithUrl(final String ipUrl, final String fileName, final String cachePath, TIMImage timImageOrigin){
        DownloadManager.getInstance().download(ipUrl, cachePath, timImageOrigin.getSize(), new DownloadManager.DownloadListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted(DownloadInfo info) {
                Log.i("originImage", "downoad origin Image onCompleted : " + fileName);
                if (mDownloadOriginListener != null){
                    mDownloadOriginListener.onORiginSuccess(fileName, ipUrl);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i("originImage", "downoad origin Image onError : " + fileName);
                if (mDownloadOriginListener != null){
                    mDownloadOriginListener.onORiginError(fileName,e);
                }
                FileUtils.deleteFile(cachePath);
            }

            @Override
            public void onProcess(int progress) {
                if (mDownloadOriginListener != null){
                    mDownloadOriginListener.onProgress(fileName,progress);
                }
            }
        });
    }
}
