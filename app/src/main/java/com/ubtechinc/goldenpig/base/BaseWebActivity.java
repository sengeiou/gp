package com.ubtechinc.goldenpig.base;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.lubans.Luban;
import com.ubtechinc.goldenpig.lubans.UriTofilePath;
import com.ubtechinc.goldenpig.main.SmallPigObject;
import com.ubtechinc.goldenpig.utils.DialogUtil;
import com.ubtechinc.goldenpig.utils.ImageUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author：ubt
 * @date：2018/10/12 15:56
 * @modifier：ubt
 * @modify_date：2018/10/12 15:56
 * [A brief description]
 */

public abstract class BaseWebActivity extends BaseToolBarActivity {

    protected WebView mWebView;

    private FrameLayout root;

    private int scrollX;

    private int scrollY;

    private boolean isFirst = true;

    private boolean isGoBack = false;

    private ImageView imageView;

    private View mWebError;

    private boolean loadError;

    private Button btWebReload;

    private String TAG = BaseWebActivity.class.getSimpleName();


    /** 相册相关，待优化 */
    private Dialog picDialog;
    private View picView;

    /** 是否选择了图片 */
    private boolean isCameraOrPhone = false;

    private ValueCallback<Uri[]> mUploadMessageForAndroid5;
    private File cameraFile;
    private Uri imageUri;
    public static final int FILECHOOSER_RESULTCODE = 123;
    public static final int PHOTO_BY_SHOOT = 1001; //拍照获取照片
    public static final int PHOTO_BY_FILE = 1002;  //相册获取


    @Override
    protected int getConentView() {
        return R.layout.activity_web_common;
    }

    protected abstract String getURL();

    protected abstract @StringRes
    int getToolBarTitle();

    @Override
    protected void init(Bundle savedInstanceState) {
        mWebView = findViewById(R.id.web_common);
        root = findViewById(R.id.web_root);
        mWebError = findViewById(R.id.view_web_error_info);
        btWebReload = findViewById(R.id.bt_web_reload);

        btWebReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
//                mWebError.setVisibility(View.GONE);
//                mWebView.setVisibility(View.VISIBLE);
                loadError = false;
                mWebView.reload();
            }
        });

        setTitleBack(true);
        int titleRes = getToolBarTitle();
        if (titleRes != - 1) {
            setToolBarTitle(titleRes);
        }

        initWebView();
        initActionBar();
    }

    /**
     * 初始化actionbar
     */
    private void initActionBar() {
        if (needActionBar()) {
            showActionBar();
        } else {
            hideActionBar();
        }
    }

    protected void processWeb() {
        mWebView.addJavascriptInterface(new SmallPigObject(this, mWebView), "SmallPigObject");
    }

    private void initWebView() {
        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mWebView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setTextZoom(100);
        processWeb();

        //开发稳定后需去掉该行代码
//        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        mWebView.getSettings().setUseWideViewPort(true);  //将图片调整到适合webview的大小
//        mWebView.getSettings().setLoadWithOverviewMode(true); // 缩放至屏幕的大小
//        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

//        mWebView.getSettings().setAllowContentAccess(true);
//        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
//        mWebView.getSettings().setAppCacheEnabled(true);
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);


        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "shouldOverrideUrlLoading|url:" + url);
                if (url.startsWith("tel:")) {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
                    return true;
                } else {
                    scrollY = view.getScrollY();
                    scrollX = view.getScrollX();
                    isGoBack = false;
                    isFirst = false;
                    onGoNextWeb();
                }
                return false;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.d(TAG, "onReceivedError:" + error);
                loadError = true;
                mWebError.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.GONE);
                dismissLoadDialog();
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                gotoSelectPhoto(filePathCallback);
                return true;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.d(TAG, "onProgressChanged:" + newProgress);
                if (!loadError && newProgress == 100 && mWebError != null) {
                    mWebError.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);
                    dismissLoadDialog();
                }
                if (isFirst) {
                    return; //刚进入页面不需要模拟效果，app自己有
                }
                view.setVisibility(View.GONE);//先隐藏webview
                if (newProgress == 100) {
                    //加载完毕，显示webview 隐藏imageview
                    view.setVisibility(View.VISIBLE);
                    if (imageView != null) {
                        imageView.setVisibility(View.GONE);
                    }
                    //页面进入效果的动画
                    Animation translate_in = AnimationUtils.loadAnimation(BaseWebActivity.this, R.anim.slide_right_in);
                    Animation translate_out = AnimationUtils.loadAnimation(BaseWebActivity.this, R.anim.slide_left_out);
                    //页面退出的动画
                    if (isGoBack) {
                        translate_in = AnimationUtils.loadAnimation(BaseWebActivity.this, R.anim.slide_left_in);
                        translate_out = AnimationUtils.loadAnimation(BaseWebActivity.this, R.anim.slide_right_out);
                    }
                    translate_in.setFillAfter(true);
                    translate_in.setDetachWallpaper(true);
                    translate_out.setFillAfter(true);
                    translate_out.setDetachWallpaper(true);
//                     开启动画
                    if (null != imageView) {
                        imageView.startAnimation(translate_out);
                    }
                    view.startAnimation(translate_in);
                    //动画结束后，移除imageView
                    translate_out.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            if (null != imageView) {
                                root.removeView(imageView);
                                imageView = null;
                                isGoBack = false;
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                } else {
                    //url没加载好之前，隐藏webview，在主布局中，加入imageview显示当前页面快照
                    if (null == imageView) {
                        imageView = new ImageView(BaseWebActivity.this);
                        view.setDrawingCacheEnabled(true);
                        Bitmap bitmap = view.getDrawingCache();
                        if (null != bitmap) {
                            Bitmap b = Bitmap.createBitmap(bitmap);
                            imageView.setImageBitmap(b);
                        }
                        root.addView(imageView);
                    }
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                LogUtils.d(TAG, "onReceivedTitle:" + title);
                if (!loadError && !TextUtils.isEmpty(title) && !title.contains("https")) {
                    setToolBarTitle(title);
                }
            }
        });

        String url = getURL();
        LogUtils.d(TAG, "getURL:" + url);
        mWebView.loadUrl(url);
    }

    private void gotoSelectPhoto(ValueCallback<Uri[]> filePathCallback) {
        mUploadMessageForAndroid5 = filePathCallback;
        isCameraOrPhone = false;
        if (picDialog == null) {
            picView = LayoutInflater.from(this).inflate(
                    R.layout.dialog_album_selector, null);
            picDialog = DialogUtil.getMenuDialog(this, picView);
            picDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    //处理监听事件
                    if (!isCameraOrPhone) {
                        cancelFilePathCallback();
                    }
                }
            });
        }
        picDialog.show();
        picView.findViewById(R.id.tv_get_content).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isCameraOrPhone = true;
                picDialog.dismiss();
                openAlbum();
            }
        });
        picView.findViewById(R.id.tv_camera).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isCameraOrPhone = true;
                picDialog.dismiss();
                openCamera();
            }
        });
        picView.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                picDialog.dismiss();
            }
        });
    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        if (Build.VERSION.SDK_INT >= 23) {
            AndPermission.with(BaseWebActivity.this)
                    .requestCode(0x1111)
                    .permission(Permission.STORAGE)
                    .callback(new PermissionListener() {
                        @Override
                        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                            doOpenSystemAlbum();
                        }

                        @Override
                        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                            cancelFilePathCallback();
                            showPermissionDialog(Permission.STORAGE);
                        }
                    })
                    .rationale((requestCode, rationale) -> rationale.resume())
                    .start();

        } else {
            doOpenSystemAlbum();
        }
    }

    private void doOpenSystemAlbum() {
        Intent openAlbumIntent = new Intent(
                Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
        startActivityForResult(openAlbumIntent, PHOTO_BY_FILE);
    }

    /**
     * 打开相机
     */
    private void openCamera() {
        if (Build.VERSION.SDK_INT >= 23) {
            AndPermission.with(BaseWebActivity.this)
                    .requestCode(0x1112)
                    .permission(Permission.CAMERA, Permission.STORAGE)
                    .callback(new PermissionListener() {
                        @Override
                        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                            if (cameraIsCanUse()) {
                                doOpenSystemCamera();
                            } else {
                                cancelFilePathCallback();
                                showPermissionDialog(Permission.CAMERA);
                            }
                        }

                        @Override
                        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                            cancelFilePathCallback();
                            showPermissionDialog(Permission.CAMERA);
                        }
                    })
                    .rationale((requestCode, rationale) -> rationale.resume())
                    .start();

        } else {
            doOpenSystemCamera();
        }
    }

    private void doOpenSystemCamera() {
        File path = new File(ImageUtils.getCameraPath());
        if (!path.exists()) {
            path.mkdirs();
        }
        cameraFile = new File(path, System.currentTimeMillis() + ".jpg");
        Intent intentCamera = new Intent();
        intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            imageUri = FileProvider.getUriForFile(BaseWebActivity.this, "com.ubtechinc.goldenpig.fileProvider",
                    cameraFile);//通过FileProvider创建一个content类型的Uri
        } else {
            imageUri = Uri.fromFile(cameraFile);
            intentCamera.putExtra("return-data", true);
        }
        //将拍照结果保存至photo_file的Uri中，不保留在相册中
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intentCamera, PHOTO_BY_SHOOT);
    }

    /**
     * 取消mFilePathCallback回调
     */
    private void cancelFilePathCallback() {
        if (mUploadMessageForAndroid5 != null) {
            mUploadMessageForAndroid5.onReceiveValue(null);
            mUploadMessageForAndroid5 = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == PHOTO_BY_FILE && resultCode == RESULT_OK) {
            if (null == mUploadMessageForAndroid5)
                return;
            if (intent == null) {
                if (mUploadMessageForAndroid5 != null) {
                    mUploadMessageForAndroid5.onReceiveValue(null);
                    mUploadMessageForAndroid5 = null;
                }
                return;
            }
            if (resultCode == RESULT_CANCELED) {
                if (mUploadMessageForAndroid5 != null) {
                    mUploadMessageForAndroid5.onReceiveValue(null);
                    mUploadMessageForAndroid5 = null;
                }
                return;
            }
            ContentResolver cr = getContentResolver();
            String type = cr.getType(intent.getData());
            if (type == null) {
                return;
            }
            Uri mImageUri = intent.getData();
            String filePath = UriTofilePath.getFilePathByUri(getApplication(), mImageUri);
            if (!TextUtils.isEmpty(filePath)) {
                compressWithRx(new File(filePath));
            } else {
                ToastUtils.showShortToast("图片出错，请重试");
                cancelFilePathCallback();
            }
        } else if (requestCode == PHOTO_BY_SHOOT && resultCode == RESULT_OK) {
            compressWithRx(cameraFile);
        } else {
            if (mUploadMessageForAndroid5 != null) {
                mUploadMessageForAndroid5.onReceiveValue(null);
                mUploadMessageForAndroid5 = null;
            }
        }
    }

    /**
     * 压缩单张图片 RxJava 方式
     */
    private void compressWithRx(File file) {
        Luban.get(getApplicationContext())
                .load(file)
                .putGear(Luban.FIRST_GEAR)
                .asObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        if (mUploadMessageForAndroid5 != null) {
                            mUploadMessageForAndroid5.onReceiveValue(null);
                            mUploadMessageForAndroid5 = null;
                            ToastUtils.showShortToast("图片出错，请重试");
                        }
                    }
                })
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends File>>() {
                    @Override
                    public ObservableSource<? extends File> apply(Throwable throwable) throws Exception {
                        return Observable.empty();
                    }
                })
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        if (mUploadMessageForAndroid5 != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                                imageUri = FileProvider.getUriForFile(BaseWebActivity.this, "com.ubtechinc.goldenpig" +
                                                ".fileProvider",
                                        file);//通过FileProvider创建一个content类型的Uri
                            } else {
                                imageUri = Uri.fromFile(file);
                            }
                            Uri[] results = new Uri[]{imageUri};
                            mUploadMessageForAndroid5.onReceiveValue(results);
                            mUploadMessageForAndroid5 = null;
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            isGoBack = true;
            mWebView.goBack();
            mWebView.scrollTo(scrollX, scrollY);
            onGoBackWeb();
        } else {
            super.onBackPressed();
        }
    }

    protected void onGoNextWeb() {
        //TODO web继续深入
    }

    protected void onGoBackWeb() {
        //TODO web回退
    }

    @Override
    protected boolean isInterceptBack() {
        return true;
    }

    /**
     * 是否需要原生标题栏
     *
     * @return
     */
    protected boolean needActionBar() {
        return true;
    }
}
