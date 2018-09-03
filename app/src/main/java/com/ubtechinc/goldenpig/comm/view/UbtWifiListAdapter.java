package com.ubtechinc.goldenpig.comm.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.utils.NetUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class UbtWifiListAdapter extends RecyclerView.Adapter<UbtWifiListAdapter.WifiHoler> implements View.OnClickListener{
    private ArrayList<ScanResult> mWifiList;
    private WeakReference<Context> mContent;
    private OnUbtWifiListItemClickListener mListener;
    private ScanResult currentScanResult;
    public UbtWifiListAdapter(Context context, ArrayList<ScanResult> wifiList) {
        this.mWifiList = wifiList;
        mContent = new WeakReference<>(context);
    }

    @NonNull
    @Override
    public WifiHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContent!=null&&mContent.get()!=null) {
            View view = LinearLayout.inflate(mContent.get(), R.layout.item_wifi_list, null);

            return new WifiHoler(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull WifiHoler holder, int position) {
        final ScanResult scanResult = mWifiList == null ? null : mWifiList.get(position);
        if (scanResult != null) {
            holder.mWifiTv.setText(scanResult.SSID);
            showWifiSingal(scanResult, holder);
            if (position==mWifiList.size()-1){
                holder.setDividerVisibility(false);
            }else {
                holder.setDividerVisibility(true);
            }
            holder.itemView.setOnClickListener(this);
            currentScanResult=scanResult;
        }
    }

    @Override
    public int getItemCount() {
        if (mWifiList == null) {
            return 0;
        } else {
            return mWifiList.size();
        }
    }

    private void showWifiSingal(ScanResult result, WifiHoler holer) {
        final boolean isFree = NetUtils.isFreeWifi(result);
        final Context context = mContent.get();
        if (context == null)
            return;
        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_wifi_free_full, null);
        Log.i("UbtWifiListAdapter",result.level
                +"===="+isFree);

        if (!isFree) {
            int singalLevel = WifiManager.calculateSignalLevel(result.level, 4);       ///计算wifi信号强度
            UbtLogger.i("UbtWifiListAdapter",result.level
                    +"===="+singalLevel);
            switch (singalLevel) {
                case 1:
                    drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_wifi4, null);
                    break;
                case 2:
                    drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_wifi3, null);
                    break;
                case 3:
                    drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_wifi3, null);
                    break;
                case 4:
                    drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_wifi1, null);
                    break;
                default:

                    break;
            }
        }
        holer.setmWifiImage(drawable);
    }

    public void setOnItemListener(OnUbtWifiListItemClickListener listener){
        this.mListener=listener;
    }

    @Override
    public void onClick(View v) {
        if (mListener!=null){
            mListener.onClick(v,currentScanResult);
        }
    }

    static class WifiHoler extends RecyclerView.ViewHolder {
        private TextView mWifiTv; //wifi信号名称
        private ImageView mWifiSingal;///wifi信号图标
        private View  mDivider;

        public WifiHoler(View itemView) {
            super(itemView);
            mWifiTv = (TextView) itemView.findViewById(R.id.ubt_tv_wifi_name);
            mWifiSingal = (ImageView) itemView.findViewById(R.id.ubt_img_wifi_singal);
            mDivider=itemView.findViewById(R.id.ubt_wifi_list_divider);

        }

        public void setWifiName(String wifiName) {
            if (mWifiTv != null) {
                mWifiTv.setText(wifiName);
            }
        }

        public void setmWifiImage(Drawable singal) {
            if (mWifiSingal != null) {
                mWifiSingal.setImageDrawable(singal);
            }
        }

        public void setDividerVisibility(boolean visual){
            if (mDivider!=null){
                if (visual) {
                    mDivider.setVisibility(View.VISIBLE);
                }else {
                    mDivider.setVisibility(View.INVISIBLE);
                }
            }
        }


    }
}
