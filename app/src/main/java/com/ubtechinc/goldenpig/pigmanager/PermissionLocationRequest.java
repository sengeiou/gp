package com.ubtechinc.goldenpig.pigmanager;

import android.content.Context;
import android.support.annotation.NonNull;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.Arrays;
import java.util.List;

/**
 * @author：liuhai
 * @date：2017/8/12 9:34
 * @modifier：ubt
 * @modify_date：2017/8/12 9:34
 * 定位权限设置
 * version
 */

public class PermissionLocationRequest {

    private int REQUEST_CODE_PERMISSION_SINGLE = 1000;
    private Context mContext;
    private PermissionLocationCallback mLocationCallback;


    public PermissionLocationRequest(Context context) {
        mContext = context;
    }

    /**
     * 请求授权
     *
     * @param callback 回调结果
     */
    public void request(PermissionLocationCallback callback) {
        this.mLocationCallback = callback;
//        boolean isFirstLocation = PreferencesManager.getInstance(mContext).get(Constants.PREF_PERMISSION_LOCATION, false);
        if (AndPermission.hasAlwaysDeniedPermission(mContext, Arrays.asList(Permission.LOCATION))) {
            mLocationCallback.onRationSetting();
        } else {
            AndPermission.with(mContext)
                    .requestCode(REQUEST_CODE_PERMISSION_SINGLE)
                    .permission(Permission.LOCATION)
                    .callback(permissionListener)
                    // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                    // 这样避免用户勾选不再提示，导致以后无法申请权限。
                    // 你也可以不设置。
//                    .rationale(new RationaleListener() {
//                        @Override
//                        public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
//                            rationale.resume();
//                        }
//                    })
                    .start();
        }

    }

    /**
     * 回调监听。
     */
    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            mLocationCallback.onSuccessful();

        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            mLocationCallback.onFailure();
        }
    };

    public interface PermissionLocationCallback {
        /**
         * 授权成功
         */
        void onSuccessful();

        /**
         * 授权失败
         */
        void onFailure();

        /**
         * 已经勾选拒绝过
         */
        void onRationSetting();
    }
}
