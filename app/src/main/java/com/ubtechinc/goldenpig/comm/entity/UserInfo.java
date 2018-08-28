package com.ubtechinc.goldenpig.comm.entity;

import android.text.TextUtils;

/**
 * @author tanghongyu
 * @ClassName UserInfo
 * @date 6/6/2017
 * @Description 用户个人信息
 * @modifier
 * @modify_time
 */
public class UserInfo {

    private String countryCode;
    private String countryName;
    private int emailVerify;
    private String userBirthday;
    private String userEmail;
    private int userGender;
    private String userId;
    private String userImage;
    private String userName;
    private String userPhone;
    private String nickName;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getPhoneNumer(){//去掉前面的地区号
        if (TextUtils.isEmpty(userPhone)){
            return null;
        }
        if (userPhone.length() == 11){
            return userPhone;
        }

        if (userPhone.length() == 13){
            return userPhone.substring(2,13);
        }else{
            return null;
        }
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public int getUserGender() {
        return userGender;
    }

    public void setUserGender(int userGender) {
        this.userGender = userGender;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getEmailVerify() {
        return emailVerify;
    }

    public void setEmailVerify(int emailVerify) {
        this.emailVerify = emailVerify;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "Result{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", userImage='" + userImage + '\'' +
                ", userGender=" + userGender +
                ", countryCode='" + countryCode + '\'' +
                ", userBirthday='" + userBirthday + '\'' +
                '}';
    }
}



