package com.ubtechinc.goldenpig.utils.share;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.ubt.robot.dmsdk.TVSWrapConstant;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;

import java.io.File;
import java.util.List;

/**
 * @auther :zzj
 * @email :zhijun.zhou@ubtrobot.com
 * @Description: :分享工具类
 * @time :2019/1/21 20:54
 * @change :
 * @changetime :2019/1/21 20:54
 */
public class ShareUtility {

    private Tencent mTencent;

    private IWXAPI mIWXAPI;

    private Context mContext;

//    private static final String MINIPROGRAM_USERNAME = "gh_0b91bfb29f60";     // 微信小程序原始id
//
//    private static final String MINIPROGRAM_PATH = "pages/main/main";  //微信小程序页面路径

    private ShareUtility() {

    }

    private static class StaticShareHolder {
        private static final ShareUtility instance = new ShareUtility();
    }

    public static ShareUtility getInstance() {
        return StaticShareHolder.instance;
    }

    private void initTencent(Context context) {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(TVSWrapConstant.APP_ID_QQ_OPEN, context);
        }
        if (mContext == null) {
            mContext = context;
        }
    }

    private void initWX(Context context) {
        if (mIWXAPI == null) {
            mIWXAPI = WXAPIFactory.createWXAPI(context, TVSWrapConstant.APP_ID_WX);
            mIWXAPI.registerApp(TVSWrapConstant.APP_ID_WX);
        }
        if (mContext == null) {
            mContext = context;
        }
    }

    /**
     * 分享短信
     *
     * @param context
     * @param message
     */
    public void doShareToSMS(Context context, String message) {
        Intent intentFinalMessage = new Intent(Intent.ACTION_VIEW);
        intentFinalMessage.setType("vnd.android-dir/mms-sms");
        intentFinalMessage.putExtra("sms_body", message);
        context.startActivity(intentFinalMessage);
    }

    /**
     * 分享二维码到微信
     *
     * @param context
     * @param filePath
     */
    public void doShareQRCodeToWX(Context context, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
//            ToastUtils.showShortToast(context, context.getString(R.string.qrcode_generate_fail));
        } else {
            Intent intent = new Intent();
            // new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity"); // QQ
            ComponentName componentName = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
            intent.setComponent(componentName);
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
//            intent.putExtra(Intent.EXTRA_TEXT, message);
//            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
            // 适配7.0
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, "com.gemvary.phone.cloudcall.fileprovider", new File(filePath)));
            context.startActivity(intent);
        }
    }

    /**
     * 分享二维码到彩信
     *
     * @param context
     * @param message
     * @param filePath
     */
    public void doShareQRCode2MMS(Context context, String message, String filePath) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.android.mms", "com.android.mms.ui.ComposeMessageActivity");
        intent.setComponent(componentName);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
        context.startActivity(intent);
    }

    /**
     * 分享网页至微信好友
     *
     * @param context
     * @param shareUrl
     * @param title
     * @param description
     * @param icon
     */
    public void shareWebPageToWxFriend(Context context, String shareUrl, String
            title, String description, Bitmap icon) {
        if (!isWeixinAvilible(context)) {
//            ToastUtils.showShortToast(context, context.getString(R.string.inVaildShare));
            return;
        }
        initWX(context);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;
        msg.setThumbImage(icon);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction();
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        mIWXAPI.sendReq(req);
    }

    /**
     * 分享网页至微信朋友圈
     *
     * @param context
     * @param shareUrl
     * @param title
     * @param description
     * @param icon
     */
    public void shareWebPageToWxTimeline(Context context, String shareUrl, String
            title, String description, Bitmap icon) {
        if (!isWeixinAvilible(context)) {
//            ToastUtils.showShortToast(context, context.getString(R.string.inVaildShare));
            return;
        }
        initWX(context);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;
        msg.setThumbImage(icon);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction();
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        mIWXAPI.sendReq(req);
    }

    /**
     * 分享文本至微信
     *
     * @param context 上下文
     * @param text    文本
     * @param scene   好友：SendMessageToWX.Req.WXSceneSession；朋友圈：SendMessageToWX.Req.WXSceneTimeline
     */
    public void shareTextToWeiXin(Context context, String text, int scene) {
        initWX(context);
        if (!isWeixinAvilible(context)) {
            ToastUtils.showShortToast("您还没有安装微信，请先安装微信客户端");
            return;
        }
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;//你要分享出去的文本
        WXMediaMessage msg = new WXMediaMessage(textObject);
        msg.description = buildDescription();

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction();// 唯一标识一个请求
        req.message = msg;
        req.scene = scene;
        mIWXAPI.sendReq(req);
    }

    /**
     * 分享至微信小程序
     * 发送到聊天界面——WXSceneSession
     * 发送到朋友圈——WXSceneTimeline
     * 添加到微信收藏——WXSceneFavorite
     */
//    public void shareToWXMiniProgram(Context context, String title, String
//            description, String params) {
//        if (!isWeixinAvilible(context)) {
//            ToastUtils.showShortToast(context, context.getString(R.string.inVaildShare));
//            return;
//        }
//        initWX(context);
//        WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
//        miniProgramObj.webpageUrl = "http://www.gemvary.com"; // 兼容低版本的网页链接
//        miniProgramObj.miniprogramType = WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE;// 正式版:0，测试版:1，体验版:2
//        miniProgramObj.userName = MINIPROGRAM_USERNAME; // 小程序原始id
//        miniProgramObj.path = MINIPROGRAM_PATH + "?" + params; //小程序页面路径
//        WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
//        msg.title = title;                    // 小程序消息title
//        msg.description = description;               // 小程序消息desc
////        msg.setThumbImage(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_gemvary));
//        msg.thumbData = getMiniThumb();                      // 小程序消息封面图片，小于128k
//
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = buildTransaction();
//        req.message = msg;
//        req.scene = SendMessageToWX.Req.WXSceneSession;  // 目前支持会话
//        mIWXAPI.sendReq(req);
//    }

    /**
     * 分享至QQ
     *
     * @param activity 上下文
     * @param title    分享标题
     * @param url      分享链接
     * @param des      分享描述
     */
    public void doShareToQQ(Activity activity, String title, String url, String des) {
        initTencent(activity);
        if(!mTencent.isQQInstalled(activity)){
            ToastUtils.showShortToast("您还没有QQ，请先安装QQ客户端");
            return;
        }
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);

        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);

        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, des);
        params.putString(QQShare.SHARE_TO_QQ_EXT_STR, des);

        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, activity.getString(R.string.app_name));
        mTencent.shareToQQ(activity, params, new IUiListener() {
            @Override
            public void onComplete(Object o) {
                Log.d("onComplete", o.toString());
            }

            @Override
            public void onError(UiError uiError) {
                Log.d("onError", uiError.toString());

            }

            @Override
            public void onCancel() {
                Log.d("onCancel", "onCancel");

            }
        });
    }

    /**
     * 分享至微信
     *
     * @param activity 上下文
     * @param title    分享标题
     * @param url      分享链接
     * @param des      分享描述
     * @param flag 好友：SendMessageToWX.Req.WXSceneSession；朋友圈：SendMessageToWX.Req.WXSceneTimeline
     */
    public void doShareToWeiXin(Activity activity, String title, String url, String des, int flag) {
        initWX(activity);
        if (!mIWXAPI.isWXAppInstalled()) {
            ToastUtils.showShortToast("您还没有安装微信，请先安装微信客户端");
            return;
        }
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;

        msg.description = des;

        Bitmap thumb = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_logo);
        msg.setThumbImage(thumb);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag;
        mIWXAPI.sendReq(req);
    }

    /**
     * 拉起微信小程序
     */
//    public void goToWXMiniProgram(Context context, String params) {
//        if (!isWeixinAvilible(context)) {
//            ToastUtils.showShortToast(context, context.getString(R.string.inVaildShare));
//            return;
//        }
//        initWX(context);
//        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
//        req.userName = MINIPROGRAM_USERNAME;
//        req.path = MINIPROGRAM_PATH + "?" + params;
//        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW;
//        api.sendReq(req);
//    }
    private static String buildDescription() {
        return "description" + String.valueOf(System.currentTimeMillis());
    }

    private static String buildTransaction() {
        return "transaction" + String.valueOf(System.currentTimeMillis());
    }

//    private byte[] getMiniThumb() {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        Bitmap bitmap = null;
//        Bitmap thumb = null;
//        try {
//            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_share_wxmini);
//            thumb = Bitmap.createScaledBitmap(bitmap, 500, 400, true);
//            thumb.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        } catch (Exception e) {
//            //TODO
//        } finally {
//            if (bitmap != null) {
//                bitmap.recycle();
//            }
//            if (thumb != null) {
//                thumb.recycle();
//            }
//        }
//        return baos.toByteArray();
//    }

    /**
     * 判断微信是否可用
     *
     * @param context
     * @return
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

}
