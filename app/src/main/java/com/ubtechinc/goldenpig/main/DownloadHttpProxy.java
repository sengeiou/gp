package com.ubtechinc.goldenpig.main;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.ubtech.utilcode.utils.JsonUtils;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.goldenpig.net.BaseHttpProxy;
import com.ubtechinc.nets.BuildConfig;
import com.ubtechinc.tvlloginlib.utils.SharedPreferencesUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class DownloadHttpProxy extends BaseHttpProxy {

    public void getData(final Context context,final String url, final String destFileDir, final String destFileName, final DownloadCallBack callback) {

        OkHttpClient okHttpClient = getHttpClient();

     /*   Map<String, Object> map = new HashMap<>();
        map.put("md5_category", category);
        map.put("md5_statement", statement);
        map.put("version", "V" + com.ubtechinc.goldenpig.BuildConfig.VERSION_NAME);
        map.put("clientType", 1);
        String content = JsonUtils.map2Json(map);
        RequestBody body = RequestBody.create(JSON, content);*/

        final Request okrequest = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;

                //储存下载文件的目录
                File dir = new File(destFileDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, destFileName);

                try {

                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        //下载中更新进度条
                        callback.onProgress(progress);
                    }
                    fos.flush();
                    //下载完成
                    callback.onSuccess();
                } catch (Exception e) {
                    callback.onError(e.getMessage());
                }finally {

                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
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
