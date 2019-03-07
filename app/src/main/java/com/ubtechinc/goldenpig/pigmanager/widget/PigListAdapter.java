package com.ubtechinc.goldenpig.pigmanager.widget;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubtechinc.bluetooth.UbtBluetoothDevice;
import com.ubtechinc.goldenpig.R;

import java.util.ArrayList;

/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :八戒机器人的蓝牙列表
 * @time :2018/8/25 14:14
 * @change :
 * @changetime :2018/8/25 14:14
 */
public class PigListAdapter extends RecyclerView.Adapter<PigListAdapter.PigHolder> {
    private ArrayList<UbtBluetoothDevice> mLeList;
    private OnPigListItemClickListener mItemClickListener;
    private int clickedPos = -1;
    private View mPigClose;


    public PigListAdapter(ArrayList<UbtBluetoothDevice> leList) {
        this.mLeList = leList;
    }

    @NonNull
    @Override
    public PigHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LinearLayout.inflate(parent.getContext(), R.layout.item_pig_list, null);
        return new PigHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PigHolder holder, final int position) {
        final UbtBluetoothDevice device = mLeList == null ? null : mLeList.get(position);
        if (device != null) {
            String name = device.getDevice().getName();
            holder.pigNameTv.setText(name.substring(0, name.indexOf("_") + 1) + name.substring(name.length() - 4, name.length()));
            if (clickedPos == -1) {

            } else {
                if (clickedPos == position) {
                    holder.tvBind.setVisibility(View.INVISIBLE);
                    holder.pigPressor.setVisibility(View.VISIBLE);
                } else {
                    holder.pigPressor.setVisibility(View.INVISIBLE);
                    holder.tvBind.setEnabled(false);
                }
            }

            holder.pigNameTv.setOnClickListener(v -> {
                if (mPigClose != null) {
                    mPigClose.setEnabled(false);
                }
                if (mItemClickListener != null) {
                    clickedPos = position;
                    mItemClickListener.onClick(position, device);
                    notifyDataSetChanged();
                }
            });
        }
        if (mLeList.size() - 1 == position) {
            holder.pigDivider.setVisibility(View.INVISIBLE);
        } else {
            holder.pigDivider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (mLeList == null) {
            return 0;
        } else {
            return mLeList.size();
        }
    }

    public void updateList(ArrayList<UbtBluetoothDevice> list) {
        this.mLeList = list;
    }

    public void setItemClickListener(OnPigListItemClickListener onPigListItemClickListener) {
        this.mItemClickListener = onPigListItemClickListener;
    }

    public OnPigListItemClickListener getItemClickListener() {
        return mItemClickListener;
    }

    public void setCloseView(View pigClose) {
        this.mPigClose = pigClose;
    }

    protected static class PigHolder extends RecyclerView.ViewHolder {
        public TextView pigNameTv;
        public View pigPressor;
        public View pigDivider;
        TextView tvBind;

        public PigHolder(View itemView) {
            super(itemView);
            pigPressor = itemView.findViewById(R.id.ubt_img_pig_connecting);
            pigNameTv = itemView.findViewById(R.id.ubt_tv_pig_name);
            pigDivider = itemView.findViewById(R.id.ubt_pig_dialog_divider);
            tvBind = itemView.findViewById(R.id.tv_bind);
        }

    }

}
