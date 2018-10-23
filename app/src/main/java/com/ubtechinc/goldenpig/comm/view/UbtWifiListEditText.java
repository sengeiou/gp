package com.ubtechinc.goldenpig.comm.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ubtechinc.commlib.utils.ToastUtils;
import com.ubtechinc.commlib.view.UbtClearableEditText;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.pigmanager.SetPigNetWorkActivity;
import com.ubtechinc.nets.utils.WifiControl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static android.content.Context.WIFI_SERVICE;

/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :组合下拉,wifi输入控件，集成wifi编辑，wifi下拉显示列表功能
 * @time :2018/8/30 14:48
 * @change :
 * @changetime :2018/8/30 14:48
 */
public class UbtWifiListEditText extends RelativeLayout implements View.OnClickListener {
    private UbtClearableEditText mWifiNameEdt; ///wifi输入编辑框
    private ImageButton mDropBtn;

    private ArrayList<ScanResult> mWifiList; ///扫描到的wifi信息列表
    private RecyclerView mWifiRyc;   //wifi列表
    private UbtWifiListAdapter mWifiListAdapter;
    private PopupWindow window;
    private String cType; ///保存网络加密方式

    public UbtWifiListEditText(Context context) {
        this(context, null);
    }

    public UbtWifiListEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UbtWifiListEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        LayoutInflater.from(context).inflate(R.layout.layout_ubt_wifi_list_edittext, this, true);

        mWifiNameEdt = (UbtClearableEditText) findViewById(R.id.ubt_clearedt_wifi_name);
        mWifiNameEdt.setFocusable(true);
        mWifiNameEdt.setFocusableInTouchMode(true);
        mWifiNameEdt.requestFocus();

        mDropBtn = (ImageButton) findViewById(R.id.ubt_btn_drop_wifilist);
        mDropBtn.setOnClickListener(this);
    }

    public String getText() {
        if (mWifiNameEdt != null) {
            return mWifiNameEdt.getText().toString();
        }
        return "";
    }

    public void setText(String text) {
        if (mWifiNameEdt != null && !TextUtils.isEmpty(text)) {
            mWifiNameEdt.setText(text.replace("\"", ""));
            scanWifiInfo();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ubt_btn_drop_wifilist:
                if (WifiControl.get(getContext().getApplicationContext()).isWifiConnect()) {
                    mDropBtn.setImageResource(R.drawable.ic_takeup);
                    scanWifiInfo();
                    showWifiListPopWindow();
                } else {
                    ToastUtils.showLongToast(getContext(), getContext().getString(R.string.ubt_open_wifif_tips));
                    getContext().startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
                break;
            default:
                break;
        }
    }

    /*****显示wifi列表****/
    private void showWifiListPopWindow() {
        if (window == null) {
            int parentWidth = getWidth();
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
                }
            });
            initWifiList(contentView);
        }
        // 显示PopupWindow，其中：
        // 第一个参数是PopupWindow的锚点，第二和第三个参数分别是PopupWindow相对锚点的x、y偏移
        window.showAsDropDown(this, 0, 0);
    }

    private void initWifiList(View contentView) {
        if (mWifiRyc == null) {
            mWifiRyc = (RecyclerView) contentView.findViewById(R.id.ubt_wifi_list_ryc);
            mWifiRyc.setLayoutManager(new WrapContentLinearLayoutManager(getContext()));
            if (mWifiListAdapter == null) {
                mWifiListAdapter = new UbtWifiListAdapter(getContext(), mWifiList);
                mWifiListAdapter.setOnItemListener(new OnUbtWifiListItemClickListener() {
                    @Override
                    public void onClick(View view, ScanResult result) {
                        if (result != null) {
                            mWifiNameEdt.setText(result.SSID.trim());
                            cType = result.capabilities;
                            window.dismiss();
                        }

                    }
                });
            }
            mWifiRyc.setAdapter(mWifiListAdapter);
        } else {
            mWifiList.clear();
        }
    }

    public void setWifiList(ArrayList<ScanResult> results) {
        this.mWifiList = results;
        if (mWifiListAdapter != null) {
            mWifiListAdapter.notifyDataSetChanged();
        }
    }

    public void addWifi2List(ScanResult result) {
        if (mWifiList == null) {
            mWifiList = new ArrayList<>();
        }
        mWifiList.add(result);
        if (mWifiListAdapter != null) {
            mWifiListAdapter.notifyItemInserted(mWifiList.size() - 1);
        }
    }

    public void addWifiLsit2List(List<ScanResult> results) {
        if (mWifiList == null) {
            mWifiList = new ArrayList<>();
        } else {
            mWifiList.clear();
        }
        if (results == null || results.size() == 0) {
            return;
        }

        //TODO 去重
        Set<ScanResult> set = new TreeSet<>((o1, o2) -> o1.SSID.compareTo(o2.SSID));
        set.addAll(results);
        mWifiList.addAll(set);

//        mWifiList.add(results.get(0));
//        for (int i = 1; i < results.size(); i++) {
//            for (int j = 0; j < mWifiList.size(); j++) {
//                if (mWifiList.get(j).SSID.equals(results.get(i).SSID)) {
//                    break;
//                }
//                if (j == mWifiList.size() - 1) {
//                    mWifiList.add(results.get(i));
//                    break;
//                }
//            }
//        }
        if (mWifiListAdapter != null) {
            mWifiListAdapter.notifyItemRangeInserted(mWifiList.size() - results.size(), results.size());
        }
    }

    private void scanWifiInfo() {
        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiManager.startScan(); //启动扫描
        List<ScanResult> scanResults = wifiManager.getScanResults();//搜索到的设备列表
        Iterator<ScanResult> iterator = scanResults.iterator();
        String phoneSsid = wifiManager.getConnectionInfo().getSSID().replace("\"", "");
        while (iterator.hasNext()) {
            ScanResult scanResult = iterator.next();
            String ssid = scanResult.SSID.replace("\"", "");
            if (TextUtils.isEmpty(ssid)) {
                iterator.remove();
            }
            if (!TextUtils.isEmpty(ssid) && ssid.equals(phoneSsid)) {
                cType = scanResult.capabilities;
            }
        }
//        final int size = scanResults.size();
//        for (int index = size - 1; index >= 0; index--) {
//            if (TextUtils.isEmpty(scanResults.get(index).SSID)) {
//                scanResults.remove(scanResults.get(index));
//            }
//        }
        if (scanResults == null || scanResults.isEmpty()) {
            LocationManager locManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(getContext(), "无法扫描到WI-FI信息，请打开GPS定位", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        addWifiLsit2List(scanResults);
    }

    public void destroy() {

        if (window != null) {
            window.dismiss();
            window = null;
        }
        if (mWifiList != null) {
            mWifiList.clear();
        }
    }

    public String getcType() {
        return cType;
    }
}
