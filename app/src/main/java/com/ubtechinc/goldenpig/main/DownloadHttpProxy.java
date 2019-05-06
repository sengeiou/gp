package com.ubtechinc.goldenpig.main;

import android.os.Environment;

import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.net.BaseHttpProxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DownloadHttpProxy extends BaseHttpProxy {

    private static final String TAG = "DownloadHttpProxy";


    public void downloadApk(String url, final DownloadCallBack callback){
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;//输入流
                FileOutputStream fos = null;//输出流
                try {
                    is = response.body().byteStream();//获取输入流
                    long total = response.body().contentLength();//获取文件大小
                    if(is != null){
                        UbtLogger.d(TAG, "onResponse: 不为空");
                        File file = new File(Environment.getExternalStorageDirectory()+ File.separator + "Download","Pig.apk");// 设置路径
                        fos = new FileOutputStream(file);
                        byte[] buf = new byte[1024];
                        int ch = -1;
                        int process = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fos.write(buf, 0, ch);
                            process += ch;
                            int progress = (int) (process * 1.0f  / total *100 );
                            callback.onProgress(progress);       //这里就是关键的实时更新进度了！
                        }

                    }
                    fos.flush();
                    // 下载完成
                    if(fos != null){
                        fos.close();
                    }
                    callback.onSuccess();
                } catch (Exception e) {
                    callback.onError(e.getMessage());
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }

            }
        });

    }


    public interface DownloadCallBack {

        void onError(String error);
        void onSuccess();
        void onProgress(int progress);
    }

}
