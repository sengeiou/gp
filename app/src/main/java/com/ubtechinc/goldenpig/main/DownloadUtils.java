package com.ubtechinc.goldenpig.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.base.BaseActivity;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.tvlloginlib.utils.SharedPreferencesUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.File;
import java.util.List;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.APP_UPDATE_CHECK;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.CHECK_NO_UPDATE;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.CHECK_UPDATE_ERROR;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.DOWNLOAD_APK_FAILED;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.DOWNLOAD_APK_PROGRESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.DOWNLOAD_APK_STAR;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.DOWNLOAD_APK_SUCCESS;

public class DownloadUtils {

    private static final String TAG = "DownloadUtils";

    public void doCheckUpdate(Context context){
        if (SharedPreferencesUtils.getBoolean(context, "isNotNeedShow", false)) {
            return;
        }
        UbtLogger.d(TAG, "start checkUpdate");
        new CheckUpdateHttpProxy().checkUpdate(new CheckUpdateHttpProxy.GetFunctionCallback() {
            @Override
            public void onError(String error) {
                UbtLogger.d(TAG, "updateInfoModel onError:" + error);
                Event<UpdateInfoModel> event = new Event<>(CHECK_UPDATE_ERROR);
                EventBusUtil.sendEvent(event);
            }

            @Override
            public void onSuccess(UpdateInfoModel updateInfoModel) {
                UbtLogger.d(TAG, "updateInfoModel:" + updateInfoModel.toString());
                Event<UpdateInfoModel> event = new Event<>(APP_UPDATE_CHECK);
                event.setData(updateInfoModel);
                EventBusUtil.sendEvent(event);
            }

            @Override
            public void noUpdate() {
                Event<UpdateInfoModel> event = new Event<>(CHECK_NO_UPDATE);
                EventBusUtil.sendEvent(event);
            }
        });
    }

    public void downloadApk(BaseActivity activity, final String url){
        if (Build.VERSION.SDK_INT >= 23) {
            AndPermission.with(activity)
                    .requestCode(0x1111)
                    .permission(Permission.STORAGE)
                    .callback(new PermissionListener() {
                        @Override
                        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                            //获取权限成功，下载apk
                            realDownloadApk(activity, url);

                        }

                        @Override
                        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                            activity.showPermissionDialog(Permission.STORAGE);
                        }
                    })
                    .rationale((requestCode, rationale) -> rationale.resume())
                    .start();

        } else {
            //下载apk
            realDownloadApk(activity, url);

        }
    }


    private void realDownloadApk(Context context, String url){
        UbtLogger.d(TAG, "realDownloadApk:" + url);
        if(TextUtils.isEmpty(url)){
            Event event = new Event(DOWNLOAD_APK_FAILED);
            EventBusUtil.sendEvent(event);
            return;
        }
        Event event = new Event(DOWNLOAD_APK_STAR);
        EventBusUtil.sendEvent(event);
//        url = "https://imtt.dd.qq.com/16891/386DFC640865A816A36C0D25C22BE7E5.apk?fsname=com.ubtechinc.goldenpig_1.2.1_41.apk"; //测试用的
        new DownloadHttpProxy().downloadApk(url, new DownloadHttpProxy.DownloadCallBack() {
            @Override
            public void onError(String error) {
                UbtLogger.d(TAG, "onError:" + error);
                Event event = new Event(DOWNLOAD_APK_FAILED);
                EventBusUtil.sendEvent(event);
            }

            @Override
            public void onSuccess() {
                UbtLogger.d(TAG, "onSuccess");
                Event event = new Event(DOWNLOAD_APK_SUCCESS);
                EventBusUtil.sendEvent(event);
            }

            @Override
            public void onProgress(int progress) {
//                UbtLogger.d(TAG, "onProgress:" + progress);
                Event event = new Event(DOWNLOAD_APK_PROGRESS);
                event.setData(progress);
                EventBusUtil.sendEvent(event);
            }
        });
    }


    private static String path = Environment.getExternalStorageDirectory()+ File.separator + "Download" + File.separator + "Pig.apk";

    public static  void installApk(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(path);
        UbtLogger.d(TAG,"安装路径=="+path  + file.exists());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri apkUri = FileProvider.getUriForFile(context, "com.ubtechinc.goldenpig.fileProvider", file);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        context.startActivity(intent);

    }





}
