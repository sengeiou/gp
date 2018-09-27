package com.ubtechinc.goldenpig.pigmanager.adpater;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.comm.img.GlideCircleTransform;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.net.CheckBindRobotModule;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

public class MemberPermissionAdapter extends RecyclerView.Adapter<MemberPermissionAdapter.MemberHolder> {
    private ArrayList<CheckBindRobotModule.User> mUserList;
    private SoftReference<Activity> activityRefer;
    private int selectedIndex=-1;
    public MemberPermissionAdapter(Activity context, ArrayList<CheckBindRobotModule.User> userLsit){
        this.mUserList=userLsit;
        activityRefer=new SoftReference<>(context);
    }

    @NonNull
    @Override
    public MemberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LinearLayout.inflate(parent.getContext(), R.layout.item_permission_member,null);
        return new MemberPermissionAdapter.MemberHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberHolder holder, int position) {
        final CheckBindRobotModule.User user=mUserList.get(position);
        if (user!=null){
                holder.userName.setText(user.getNickName());
                if (activityRefer!=null&&activityRefer.get()!=null) {
                    Glide.with(activityRefer.get())
                            .load(user.getUserImage())
                            .asBitmap()
                            .centerCrop()
                            .transform(new GlideCircleTransform(activityRefer.get()))
                            .into(holder.userPhoto);
                }
                if (position==selectedIndex){
                    holder.transferBox.setVisibility(View.VISIBLE);
                }else {
                    holder.transferBox.setVisibility(View.INVISIBLE);
                }
                holder.itemView.setTag(position);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedIndex=(int)v.getTag();
                        notifyDataSetChanged();
                    }
                });
        }
    }

    @Override
    public int getItemCount() {
        if (mUserList==null)
            return 0;
        return mUserList.size();
    }

    public int getSelectedIndex(){
        return selectedIndex;
    }
    protected static class MemberHolder extends RecyclerView.ViewHolder{
        public ImageView userPhoto; //头像
        public TextView  userName;  //用户名称
        public ImageView  transferBox; //
        public MemberHolder(View itemView) {
            super(itemView);
            userPhoto=itemView.findViewById(R.id.ubt_img_member_photo);
            userName=itemView.findViewById(R.id.ubt_tv_member_name);
            transferBox=itemView.findViewById(R.id.ubt_cbox_admin);
        }
    }
}
