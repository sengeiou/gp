package com.ubtechinc.goldenpig.pigmanager.adpater;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubt.improtolib.UserContacts;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.comm.img.GlideCircleTransform;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.net.CheckBindRobotModule;
import com.ubtechinc.goldenpig.pigmanager.widget.PigListAdapter;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

public class PigMemberAdapter extends RecyclerView.Adapter<PigMemberAdapter.MemberHolder>{

    private ArrayList<CheckBindRobotModule.User> mUserList;
    private boolean isAdminUser;
    private SoftReference<Activity> activityRefer;
    public PigMemberAdapter(Activity context, ArrayList<CheckBindRobotModule.User>userList){
        this.mUserList=userList;
        activityRefer=new SoftReference<>(context);
    }
    @NonNull
    @Override
    public MemberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LinearLayout.inflate(parent.getContext(), R.layout.item_pig_member,null);
        return new MemberHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberHolder holder, int position) {
        if(mUserList!=null&&mUserList.size()>position&&position>=0) {
            CheckBindRobotModule.User user = mUserList.get(position);
            if (user!=null){
                holder.userNameTv.setText(user.getNickName());
                if (activityRefer!=null&&activityRefer.get()!=null) {
                    Glide.with(activityRefer.get())
                            .load(user.getUserImage())
                            .asBitmap()
                            .centerCrop()
                            .transform(new GlideCircleTransform(activityRefer.get()))
                            .into(holder.userPoto);
                }
                if (user.getIsAdmin()==1){
                    holder.adminFlageView.setVisibility(View.VISIBLE);
                }else {
                    holder.adminFlageView.setVisibility(View.INVISIBLE);
                }
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

    @Override
    public int getItemViewType(int position) {
        return position==0?1:2;
    }

    public boolean isAdminUser() {
        return isAdminUser;
    }

    public void setAdminUser(boolean adminUser) {
        isAdminUser = adminUser;
    }

    protected  static  class MemberHolder extends RecyclerView.ViewHolder{
        public TextView userNameTv;
        public ImageView userPoto;
        public View adminFlageView;
        public MemberHolder(View itemView) {
            super(itemView);
            userNameTv=itemView.findViewById(R.id.ubt_tv_member_name);
            userPoto=itemView.findViewById(R.id.ubt_img_member_photo);
            adminFlageView=itemView.findViewById(R.id.ubt_tv_admin);
        }
    }
}
