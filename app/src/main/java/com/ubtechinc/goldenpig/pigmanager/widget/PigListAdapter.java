package com.ubtechinc.goldenpig.pigmanager.widget;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubtechinc.bluetooth.UbtBluetoothDevice;
import com.ubtechinc.goldenpig.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :小猪音响的蓝牙列表
 *@time          :2018/8/25 14:14
 *@change        :
 *@changetime    :2018/8/25 14:14
*/
public class PigListAdapter extends RecyclerView.Adapter<PigListAdapter.PigHolder>{
    private ArrayList<UbtBluetoothDevice> mLeList;
    private OnPigListItemClickListener mItemClickListener;
    public PigListAdapter(ArrayList<UbtBluetoothDevice> leList){
        this.mLeList=leList;
    }
    @NonNull
    @Override
    public PigHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view= LinearLayout.inflate(parent.getContext(), R.layout.item_pig_list,null);
         return new  PigHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final PigHolder holder, final int position) {
        holder.pigNameTv.setText(mLeList.get(position).getDevice().getName());
        holder.pigNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.pigPressor.setVisibility(View.VISIBLE);
                if (mItemClickListener!=null){
                    mItemClickListener.onClick(position,mLeList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mLeList==null) {
            return 0;
        }else {
            return mLeList.size();
        }
    }

    public void setItemClickListener(OnPigListItemClickListener onPigListItemClickListener){
        this.mItemClickListener=onPigListItemClickListener;
    }

    protected static  class PigHolder extends  RecyclerView.ViewHolder  {
        public TextView  pigNameTv;
        public ImageView pigPressor;
        public PigHolder(View itemView) {
            super(itemView);
            pigPressor=itemView.findViewById(R.id.ubt_img_pig_connecting);
            pigNameTv=(TextView)itemView.findViewById(R.id.ubt_tv_pig_name);

        }

    }

}
