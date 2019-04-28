package com.ubt.robot.dmsdk.model;

import com.tencent.ai.tvs.core.account.UserInfoManager;

/**
 * @Description: TVS->UserInfoManager
 * @Author: zhijunzhou
 * @CreateDate: 2019/4/23 10:52
 */
public class TVSWrapUserInfo {

    private String nickname;

    /**
     * 0:男，1：女
     */
    private int gender;

    private String sGender;

    private String avatar;

    public TVSWrapUserInfo(String nickname, int gender, String avatar) {
        this.nickname = nickname;
        this.gender = gender;
        this.sGender = gender == UserInfoManager.MALE ? "男" : "女";
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getsGender() {
        return sGender;
    }

    public void setsGender(String sGender) {
        this.sGender = sGender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
