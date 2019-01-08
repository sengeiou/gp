package com.ubtechinc.goldenpig.comm.wifi;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * @auther :zzj
 * @email :zhijun.zhou@ubtrobot.com
 * @Description: :wifi控件适配器
 * @time :2019/1/8 16:34
 * @change :
 * @changetime :2019/1/8 16:34
 */
public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.WifiHoler> {

    private ArrayList<UbtWifiInfo> mWifiList;
    private WeakReference<Context> mContent;
    private WifiListCallback mListener;

    public WifiListAdapter(Context context, ArrayList<UbtWifiInfo> wifiList) {
        this.mWifiList = wifiList;
        mContent = new WeakReference<>(context);
    }

    @NonNull
    @Override
    public WifiHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContent != null && mContent.get() != null) {
            View view = LinearLayout.inflate(mContent.get(), R.layout.item_wifi_list, null);

            return new WifiHoler(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull WifiHoler holder, int position) {
        final UbtWifiInfo wifiInfo = mWifiList == null ? null : mWifiList.get(position);
        if (wifiInfo != null) {
            holder.mWifiTv.setText(wifiInfo.getSsid());
            showWifiSingal(wifiInfo, holder);
            if (position == 0) {
                holder.setDividerVisibility(false);
            } else {
                holder.setDividerVisibility(true);
            }
            holder.itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onClick(v, wifiInfo);
                }
            });
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

    private void showWifiSingal(UbtWifiInfo result, WifiHoler holer) {
        final boolean isFree = result.isFree();
        final Context context = mContent.get();
        if (context == null)
            return;
        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_wifi001, null);
        Log.i("UbtWifiListAdapter", result.getRssi()
                + "====" + isFree);

        if (!isFree) {
            int singalLevel = WifiManager.calculateSignalLevel(result.getRssi(), 4);       ///计算wifi信号强度
            UbtLogger.i("UbtWifiListAdapter", result.getRssi()
                    + "====" + singalLevel);
            switch (singalLevel) {
                case 1:
                    drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_wifi004, null);
                    break;
                case 2:
                    drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_wifi003, null);
                    break;
                case 3:
                    drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_wifi002, null);
                    break;
                case 4:
                    drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_wifi001, null);
                    break;
                default:

                    break;
            }
        }
        holer.setmWifiImage(drawable);
    }

    public void setOnItemListener(WifiListCallback listener) {
        this.mListener = listener;
    }

    public void updateList(ArrayList<UbtWifiInfo> list) {
        this.mWifiList = list;
    }

    static class WifiHoler extends RecyclerView.ViewHolder {
        private TextView mWifiTv; //wifi信号名称
        private ImageView mWifiSingal;///wifi信号图标
        private View mDivider;

        public WifiHoler(View itemView) {
            super(itemView);
            mWifiTv = (TextView) itemView.findViewById(R.id.ubt_tv_wifi_name);
            mWifiSingal = (ImageView) itemView.findViewById(R.id.ubt_img_wifi_singal);
            mDivider = itemView.findViewById(R.id.ubt_wifi_list_divider);

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

        public void setDividerVisibility(boolean visual) {
            if (mDivider != null) {
                if (visual) {
                    mDivider.setVisibility(View.VISIBLE);
                } else {
                    mDivider.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
