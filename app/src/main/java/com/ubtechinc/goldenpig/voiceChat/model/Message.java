package com.ubtechinc.goldenpig.voiceChat.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageStatus;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.comm.entity.UserInfo;
import com.ubtechinc.goldenpig.comm.img.GlideCircleTransform;
import com.ubtechinc.goldenpig.common.adapter.ViewHolder;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.voiceChat.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

//import com.example.live.LiveHelper;
//import com.jaronho.sdk.utils.ActivityTracker;
//import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Picasso;

/**
 * 消息数据基类
 */
public abstract class Message {

    protected final String TAG = "Message";

    public TIMMessage message;

    private boolean hasTime;

    /**
     * 消息描述信息
     */
    private String desc;


    public TIMMessage getMessage() {
        return message;
    }


    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context 显示消息的上下文
     */
    public abstract void showMessage(ViewHolder viewHolder, Context context);

    /**
     * 获取显示气泡
     *
     * @param viewHolder 界面样式
     */
    public RelativeLayout getBubbleView(final ViewHolder viewHolder){
        if(UBTPGApplication.voiceMail_debug){
            Toast.makeText(UBTPGApplication.getContext(), "hasTime "+ hasTime, Toast.LENGTH_SHORT).show();
            UbtLogger.d(TAG,"hasTime show   "+hasTime);
        }
        viewHolder.getView(R.id.systemMessage).setVisibility(hasTime? View.VISIBLE: View.GONE);
        viewHolder.setText(R.id.systemMessage, TimeUtil.getChatTimeStr(message.timestamp()));
        showDesc(viewHolder);
        if (message.isSelf()){
//                Glide.with(UBTPGApplication.getContext()).load(UbtTIMManager.avatarURL).asBitmap().placeholder(R.drawable.head_me).diskCacheStrategy(DiskCacheStrategy.ALL).into((ImageView) viewHolder.getView(R.id.rightAvatar));
            Glide.with(UBTPGApplication.getContext()).load(getHeadImageUrl()).asBitmap().placeholder(R.drawable.head_me).diskCacheStrategy(DiskCacheStrategy.ALL).into((ImageView) viewHolder.getView(R.id.rightAvatar));
            viewHolder.getView(R.id.leftPanel).setVisibility(View.GONE);
            viewHolder.getView(R.id.rightPanel).setVisibility(View.VISIBLE);
            return viewHolder.getView(R.id.rightMessage);
        }else{
            List<String> senders = new ArrayList<>();
            senders.add(getSender());
            TIMFriendshipManager.getInstance().getUsersProfile(senders, new TIMValueCallBack<List<TIMUserProfile>>() {
                @Override
                public void onError(int i, String s) {
                }
                @Override
                public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                    if (!timUserProfiles.isEmpty()) {
                        final TIMUserProfile info = timUserProfiles.get(0);
                        if (null != info) {
                            String avatar = info.getFaceUrl();
                            if (null != avatar && !avatar.isEmpty()) {
                             //   Picasso.with(UBTPGApplication.getContext()).load(avatar).into(viewHolder.leftAvatar);
                            }
                        }
                        viewHolder.getView(R.id.leftAvatar).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                if (null != ChatActivity.Instance && null != info) {
//                                    ChatActivity.Instance.onClickMember(info.getIdentifier(), info.getNickName());
//                                }
                            }
                        });
                    }
                }
            });
            viewHolder.getView(R.id.leftPanel).setVisibility(View.VISIBLE);
            viewHolder.getView(R.id.rightPanel).setVisibility(View.GONE);
            //群聊显示名称，群名片>个人昵称>identify
            if (message.getConversation().getType() == TIMConversationType.Group){
                viewHolder.getView(R.id.sender).setVisibility(View.VISIBLE);
                String name = "";
                if (message.getSenderGroupMemberProfile()!=null) {
                    name = message.getSenderGroupMemberProfile().getNameCard();
                }
                if (name.equals("")&&message.getSenderProfile()!=null) {
                    name = message.getSenderProfile().getNickName();
                }
                if (name.equals("")) {
                    name = message.getSender();
                }
                //viewHolder.sender.setText(name);
                viewHolder.setText(R.id.sender, "brian");
            }else{
                viewHolder.getView(R.id.sender).setVisibility(View.GONE);
            }
            return viewHolder.getView(R.id.leftMessage);
        }

    }

    /**
     * 显示消息状态
     *
     * @param viewHolder 界面样式
     */
    public void showStatus(ViewHolder viewHolder){
        switch (message.status()){
            case Sending:
                viewHolder.getView(R.id.sendError).setVisibility(View.GONE);
                viewHolder.getView(R.id.sending).setVisibility(View.VISIBLE);
                break;
            case SendSucc:
                viewHolder.getView(R.id.sendError).setVisibility(View.GONE);
                viewHolder.getView(R.id.sending).setVisibility(View.GONE);
                break;
            case SendFail:
                viewHolder.getView(R.id.sendError).setVisibility(View.VISIBLE);
                viewHolder.getView(R.id.sending).setVisibility(View.GONE);
                viewHolder.getView(R.id.leftPanel).setVisibility(View.GONE);
                break;
                default:
        }
    }

    /**
     * 判断是否是自己发的
     *
     */
    public boolean isSelf(){
        return message.isSelf();
    }

    /**
     * 获取消息摘要
     *
     */
    public abstract String getSummary();

    /**
     * 保存消息或消息文件
     *
     */
    public abstract void save();


    /**
     * 删除消息
     *
     */
    public void remove(){
        if (message != null){
            message.remove();
        }
    }



    /**
     * 是否需要显示时间获取
     *
     */
    public boolean getHasTime() {
        return hasTime;
    }


    /**
     * 是否需要显示时间设置
     *
     * @param message 上一条消息
     */
    public void setHasTime(TIMMessage message){
        if (message == null){
            hasTime = true;
            return;
        }
        UbtLogger.d(TAG,"current message timestamp   "+this.message.timestamp());
        UbtLogger.d(TAG,"previous message timestamp   "+message.timestamp());
        hasTime = this.message.timestamp() - message.timestamp() > 300;
    }


    /**
     * 消息是否发送失败
     *
     */
    public boolean isSendFail(){
        return message.status() == TIMMessageStatus.SendFail;
    }

    /**
     * 清除气泡原有数据
     *
     */
    protected void clearView(ViewHolder viewHolder){
        getBubbleView(viewHolder).removeAllViews();
        getBubbleView(viewHolder).setOnClickListener(null);
    }

    /**
     * 获取发送者
     *
     */
    public String getSender(){
        if (message.getSender() == null) {
            return "";
        }
        return message.getSender();
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    private void showDesc(ViewHolder viewHolder){

        if (desc == null || desc.equals("")){
            viewHolder.getView(R.id.rightDesc).setVisibility(View.GONE);
        }else{
            viewHolder.getView(R.id.rightDesc).setVisibility(View.VISIBLE);
            viewHolder.setText(R.id.rightDesc, desc);
        }
    }
    private String getHeadImageUrl(){
        AuthLive authLive = AuthLive.getInstance();
        UserInfo mUser; mUser = authLive.getCurrentUser();
        if (mUser != null) {
            if (!TextUtils.isEmpty(mUser.getUserImage())) {
                return mUser.getUserImage();
            }
        }
        return "";
    }

}
