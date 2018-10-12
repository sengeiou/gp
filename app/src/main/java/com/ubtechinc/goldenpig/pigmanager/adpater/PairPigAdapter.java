package com.ubtechinc.goldenpig.pigmanager.adpater;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.comm.img.GlideCircleTransform;
import com.ubtechinc.goldenpig.net.CheckBindRobotModule;
import com.ubtechinc.goldenpig.pigmanager.mypig.PairPigActivity;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :
 *@time          :2018/9/28 19:29
 *@change        :
 *@changetime    :2018/9/28 19:29
*/
public class PairPigAdapter extends RecyclerView.Adapter<PairPigAdapter.PairPigHolder> implements View.OnClickListener{
    private SoftReference<Activity> activityRefer;
    private ArrayList<CheckBindRobotModule.User> mUserList;
    private OnUnpairClickListener listener;
    public  PairPigAdapter(Activity activity, ArrayList<CheckBindRobotModule.User> userList){
        this.activityRefer=new SoftReference<>(activity);
        this.mUserList=userList;
    }

    @NonNull
    @Override
    public PairPigHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=View.inflate(parent.getContext(), R.layout.item_par_pig_rcy,null);
        return new PairPigHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PairPigHolder holder, int position) {
        if (mUserList!=null&&position>=0&&position<mUserList.size()) {
            final CheckBindRobotModule.User user =mUserList.get(position);
            holder.userNameTv.setText(user.getNickName());
            if (activityRefer!=null&&activityRefer.get()!=null) {
                Glide.with(activityRefer.get())
                        .load(user.getUserImage())
                        .asBitmap()
                        .centerCrop()
                        .transform(new GlideCircleTransform(activityRefer.get()))
                        .into(holder.userPotoImg);
                holder.unPairBtn.setTag(position);
                holder.unPairBtn.setOnClickListener(this);
            }
        }
    }
    @Override
    public int getItemCount() {
        if (mUserList!=null){
            return mUserList.size();
        }
        return 0;
    }

    public OnUnpairClickListener getListener() {
        return listener;
    }

    public void setListener(OnUnpairClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag()!=null){
            if (listener!=null){
                final String userId=String.valueOf(mUserList.get((int)v.getTag()).getUserId());
                listener.onClick(userId);
            }
        }
    }

    protected static class PairPigHolder extends RecyclerView.ViewHolder{
        public ImageView userPotoImg;
        public TextView  userNameTv;
        public Button    unPairBtn;
         public PairPigHolder(View itemView) {
             super(itemView);
             userPotoImg=itemView.findViewById(R.id.ubt_img_member_photo);
             userNameTv=itemView.findViewById(R.id.ubt_tv_member_name);
             unPairBtn=itemView.findViewById(R.id.ubt_btn_unpair);
         }
     }
     public  interface  OnUnpairClickListener{
        void onClick(String userId);
     }
}
