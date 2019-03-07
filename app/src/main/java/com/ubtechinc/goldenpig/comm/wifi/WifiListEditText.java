package com.ubtechinc.goldenpig.comm.wifi;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.ubtechinc.commlib.view.UbtClearableEditText;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.comm.view.WrapContentLinearLayoutManager;
import com.ubtechinc.nets.utils.WifiControl;

import java.util.ArrayList;

/**
 * @auther :zzj
 * @email :zhijun.zhou@ubtrobot.com
 * @Description: :组合下拉,wifi输入控件，集成wifi编辑，wifi下拉显示列表功能
 * @time :2019/1/8 16:36
 * @change :
 * @changetime :2019/1/8 16:36
 */
public class WifiListEditText extends RelativeLayout implements View.OnClickListener {
    private UbtClearableEditText mWifiNameEdt; ///wifi输入编辑框
    private ImageButton mDropBtn;
    private View mClearLine;

    private ArrayList<UbtWifiInfo> mWifiList; ///扫描到的wifi信息列表
    private RecyclerView mWifiRyc;   //wifi列表
    private View mWifiLoading;
    private View mTvWifiRetry;
    private WifiListAdapter mWifiListAdapter;
    private PopupWindow window;

    private IWifiListCallback wifiCallback;

    private String mCurrSsid;

    private String mCurrCtype;

    public WifiListEditText(Context context) {
        this(context, null);
    }

    public WifiListEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WifiListEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        LayoutInflater.from(context).inflate(R.layout.layout_ubt_wifi_list_edittext, this, true);

        mWifiNameEdt = findViewById(R.id.ubt_clearedt_wifi_name);
        mWifiNameEdt.setFocusable(true);
        mWifiNameEdt.setFocusableInTouchMode(true);
        mWifiNameEdt.requestFocus();

        mClearLine = findViewById(R.id.view_clear_line);
        mWifiNameEdt.setClearLine(mClearLine);

        mDropBtn = findViewById(R.id.ubt_btn_drop_wifilist);
        mDropBtn.setOnClickListener(this);
//        String defaultSsid = com.ubtechinc.nets.utils.WifiControl.get(context).getConnectInfo().getSSID();
//        setWifi(defaultSsid);
    }

    public String getSsid() {
        return mCurrSsid;
    }

    public String getCtype() {
        return mCurrCtype;
    }

    public void setWifi(String wifiSsid) {
        if (mWifiNameEdt != null) {
            if (!TextUtils.isEmpty(wifiSsid)) {
                mWifiNameEdt.setText(wifiSsid.replace("\"", ""));
                mCurrSsid = wifiSsid;
                mCurrCtype = WifiControl.get(getContext()).getCType();
            } else {
                mWifiNameEdt.setText("");
                mCurrSsid = null;
                mCurrCtype = null;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ubt_btn_drop_wifilist:
                mDropBtn.setImageResource(R.drawable.ic_dropup);
                showWifiListPopWindow();
                break;
            default:
                break;
        }
    }

    /*****显示wifi列表****/
    private void showWifiListPopWindow() {
        if (window == null) {
            int parentWidth = getWidth();
//            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//            parentWidth = displayMetrics.widthPixels;
            View contentView = LayoutInflater.from(getContext()).inflate(R.layout.popu_wifilist, null, false);
            window = new PopupWindow(contentView, parentWidth, LayoutParams.WRAP_CONTENT, true);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setOutsideTouchable(true);
            // 设置PopupWindow是否能响应点击事件
            window.setTouchable(true);
            window.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    mDropBtn.setImageResource(R.drawable.ic_drop_down);
                    if (wifiCallback != null) {
                        wifiCallback.onShow(false);
                    }
                }
            });
            initWifiList(contentView);
        }
        // 显示PopupWindow，其中：
        // 第一个参数是PopupWindow的锚点，第二和第三个参数分别是PopupWindow相对锚点的x、y偏移
        window.showAsDropDown(this, 0, 1);
        if (wifiCallback != null) {
            wifiCallback.onShow(true);
        }
    }

    private void initWifiList(View contentView) {
        if (mWifiRyc == null) {
            mWifiRyc = contentView.findViewById(R.id.ubt_wifi_list_ryc);
            mWifiRyc.setLayoutManager(new WrapContentLinearLayoutManager(getContext()));
            mWifiLoading = contentView.findViewById(R.id.ubt_wifi_loading);
            mTvWifiRetry = contentView.findViewById(R.id.tv_wifi_retry);
            mWifiLoading.setVisibility(View.VISIBLE);
            mWifiRyc.setVisibility(View.GONE);
            mTvWifiRetry.setVisibility(View.GONE);
            if (mWifiListAdapter == null) {
                mWifiListAdapter = new WifiListAdapter(getContext(), mWifiList);
                mWifiListAdapter.setOnItemListener((view, result) -> {
                    if (result != null) {
                        mCurrSsid = result.getSsid().trim();
                        mCurrCtype = result.getEncryptionKey();
                        mWifiNameEdt.setText(result.getSsid().trim());
                        if (wifiCallback != null) {
                            wifiCallback.onChange(result.getSsid().trim());
                        }
                        window.dismiss();
                    }

                });
            }
            mWifiRyc.setAdapter(mWifiListAdapter);
        } else {
//            mWifiList.clear();
        }
        if (mWifiList != null && !mWifiList.isEmpty()) {
            mWifiLoading.setVisibility(View.GONE);
            mWifiRyc.setVisibility(View.VISIBLE);
            mWifiListAdapter.updateList(mWifiList);
        } else {
            mWifiLoading.setVisibility(View.VISIBLE);
            mWifiRyc.setVisibility(View.GONE);
        }

    }

    public void setWifiCallback(IWifiListCallback wifiCallback) {
        this.wifiCallback = wifiCallback;
    }

    public void addWifiLsit2List(UbtWifiInfo wifiInfo) {
        if (mWifiList == null) {
            mWifiList = new ArrayList<>();
        }
        if (mWifiLoading != null) {
            mWifiLoading.setVisibility(View.GONE);
        }
        if (mTvWifiRetry != null) {
            mTvWifiRetry.setVisibility(View.GONE);
        }
        if (mWifiRyc != null) {
            mWifiRyc.setVisibility(View.VISIBLE);
        }
        if (isHasWifi(wifiInfo) < 0) {
            if (TextUtils.isEmpty(mCurrSsid)) {
                mCurrSsid = wifiInfo.getSsid();
                mCurrCtype = wifiInfo.getEncryptionKey();
                if (mWifiNameEdt != null) {
                    if(TextUtils.isEmpty(mWifiNameEdt.getText().toString())){
                        mWifiNameEdt.setText(mCurrSsid);
                    }
                }
            }
            mWifiList.add(wifiInfo);
            if (mWifiListAdapter != null) {
                mWifiListAdapter.updateList(mWifiList);
                mWifiListAdapter.notifyItemInserted(mWifiList.size());
            }
        }
    }

    public void fetchWifiFailured(IRetryCallback callback) {
        if (mWifiList == null || mWifiList.isEmpty()) {
            //TODO 超时重试
            mTvWifiRetry.setVisibility(View.VISIBLE);
            mWifiLoading.setVisibility(View.GONE);
            mTvWifiRetry.setOnClickListener(v -> {
                if (callback != null) {
                    if (mWifiLoading != null) {
                        mWifiLoading.setVisibility(View.VISIBLE);
                    }
                    if (mTvWifiRetry != null) {
                        mTvWifiRetry.setVisibility(View.GONE);
                    }
                    callback.doRetry();
                }
            });
        }
    }

    private int isHasWifi(UbtWifiInfo ubtWifiInfo) {
        if (mWifiList == null) {
            return -1;
        }
        final int devLen = mWifiList.size();
        for (int index = 0; index < devLen; index++) {
            if (mWifiList.get(index).getSsid().equals(ubtWifiInfo.getSsid())) {
                return index;
            }
        }
        return -1;
    }

    public ArrayList<UbtWifiInfo> getWifiList() {
        return mWifiList;
    }

    public interface IWifiListCallback {

        void onChange(String ssid);

        void onShow(boolean isShow);
    }

    public interface IRetryCallback {
        void doRetry();
    }
}
