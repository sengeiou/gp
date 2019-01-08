package com.ubtechinc.goldenpig.pigmanager.adpater;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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

public class PigMemberAdapter extends RecyclerView.Adapter<PigMemberAdapter.MemberHolder> implements View.OnClickListener {

    private ArrayList<CheckBindRobotModule.User> mUserList;
    private boolean isAdminUser;
    private SoftReference<Activity> activityRefer;
    private boolean isAdmin;

    private OnMemberClickListener mOnMemberClickListener;

    public PigMemberAdapter(Activity context, ArrayList<CheckBindRobotModule.User> userList) {
        this.mUserList = userList;
        activityRefer = new SoftReference<>(context);
    }

    @NonNull
    @Override
    public MemberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LinearLayout.inflate(parent.getContext(), R.layout.item_pig_member, null);
        return new MemberHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberHolder holder, int position) {
        if (mUserList != null && mUserList.size() > position && position >= 0) {
            CheckBindRobotModule.User user = mUserList.get(position);
            if (user != null) {
                holder.userNameTv.setText(user.getNickName());
                if (activityRefer != null && activityRefer.get() != null) {
                    Glide.with(activityRefer.get())
                            .load(user.getUserImage())
                            .asBitmap()
                            .centerCrop()
                            .transform(new GlideCircleTransform(activityRefer.get()))
                            .placeholder(R.drawable.ic_sign_in)
                            .into(holder.userPoto);
                }
                if (user.getIsAdmin() == 1) {
                    checkCurUserIsAdmin(user.getUserId());
                    holder.adminFlagView.setVisibility(View.VISIBLE);
                } else {
                    holder.adminFlagView.setVisibility(View.GONE);
                }
                if (isSelf(user.getUserId())) {
                    holder.meFlagView.setVisibility(View.VISIBLE);
                    holder.tvExitGroup.setVisibility(View.VISIBLE);
                    holder.tvExitGroup.setTag(String.valueOf(user.getUserId()));
                    holder.tvExitGroup.setOnClickListener(this);
                    holder.ivOperMore.setVisibility(View.GONE);
                } else {
                    holder.meFlagView.setVisibility(View.GONE);
                    holder.tvExitGroup.setVisibility(View.GONE);
                    if (isAdmin) {
                        holder.ivOperMore.setVisibility(View.VISIBLE);
                        holder.ivOperMore.setTag(String.valueOf(user.getUserId()));
                        holder.ivOperMore.setOnClickListener(this);
                    } else {
                        holder.ivOperMore.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private boolean isSelf(int userId) {
        String currentUserId = AuthLive.getInstance().getUserId();
        if (!TextUtils.isEmpty(currentUserId) && currentUserId.equals(String.valueOf(userId))) {
            return true;
        } else {
            return false;
        }
    }

    private void checkCurUserIsAdmin(int userId) {
        String currentUserId = AuthLive.getInstance().getUserId();
        if (!TextUtils.isEmpty(currentUserId) && currentUserId.equals(String.valueOf(userId))) {
            isAdmin = true;
        } else {
            isAdmin = false;
        }
    }

    @Override
    public int getItemCount() {
        if (mUserList != null) {
            return mUserList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (isAdmin) {
            return position == 0 ? 1 : 2;
        } else {
            return 1;
        }
    }

    public boolean isAdminUser() {
        return isAdminUser;
    }

    public void setAdminUser(boolean adminUser) {
        isAdminUser = adminUser;
    }

    @Override
    public void onClick(View v) {
        String userId = (String) v.getTag();
        switch (v.getId()) {
            case R.id.tv_exit_group:
                if (mOnMemberClickListener != null) {
                    mOnMemberClickListener.onClickExitGroup(v, userId);
                }
                break;
            case R.id.iv_oper_more:
                if (mOnMemberClickListener != null) {
                    mOnMemberClickListener.onClickOperMore(v, userId);
                }
                break;
        }
    }

    protected static class MemberHolder extends RecyclerView.ViewHolder {
        public TextView userNameTv;
        public ImageView userPoto;
        public View adminFlagView;
        public View meFlagView;
        public View tvExitGroup;
        public View ivOperMore;

        public MemberHolder(View itemView) {
            super(itemView);
            userNameTv = itemView.findViewById(R.id.ubt_tv_member_name);
            userPoto = itemView.findViewById(R.id.ubt_img_member_photo);
            adminFlagView = itemView.findViewById(R.id.ubt_tv_admin);
            meFlagView = itemView.findViewById(R.id.ubt_tv_me);
            tvExitGroup = itemView.findViewById(R.id.tv_exit_group);
            ivOperMore = itemView.findViewById(R.id.iv_oper_more);
        }
    }

    public void setmOnMemberClickListener(OnMemberClickListener mOnMemberClickListener) {
        this.mOnMemberClickListener = mOnMemberClickListener;
    }

    public interface OnMemberClickListener {
        void onClickExitGroup(View view, String userId);
        void onClickOperMore(View view, String userId);
    }

}
