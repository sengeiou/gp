package com.ubtechinc.goldenpig.net;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.ubtechinc.nets.http.Url;

import java.io.Serializable;
import java.util.List;

/**
 * @Date: 2017/10/27.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */
@Keep
public class CheckBindRobotModule {

    @Url("/user-service-rest/v2/robot/common/queryMemberList")
    @Keep
    public static class Request {
        private String serialNumber;
        private String isAdmin="1";

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public void setIsAdmin(String isAdmin) {
            this.isAdmin = isAdmin;
        }

        public String getIsAdmin() {
            return isAdmin;
        }
    }

    @Keep
    public class Response extends BaseResponse {

        private Data data;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }
    }

    @Keep
    public class Data {
        private List<User> result;

        public List<User> getResult() {
            return result;
        }

        public void setResult(List<User> result) {
            this.result = result;
        }
    }
    @Keep
    public class User implements Serializable, Comparable<User>{
        private String iconPath;

        private String userName;

        private String nickname;

        private int userId;
        private int roleType;

        private int isAdmin;

        private String relationDate;

        public String getRelationDate() {
            return relationDate;
        }

        public void setRelationDate(String relationDate) {
            this.relationDate = relationDate;
        }

        public String getNickName() {
            return nickname;
        }

        public void setNickName(String nickName) {
            this.nickname = nickName;
        }
        public int getRoleType() {
            return roleType;
        }

        public void setRoleType(int roleType) {
            this.roleType = roleType;
        }

        public String getUserImage() {
            return iconPath;
        }

        public void setUserImage(String userImage) {
            this.iconPath = userImage;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public void setIsAdmin(int isAdmin) {
            this.isAdmin = isAdmin;
        }

        public int getIsAdmin() {
            return isAdmin;
        }

        @Override
        public String toString() {
            return "User{" +
                    "iconPath='" + iconPath + '\'' +
                    ", userName='" + userName + '\'' +
                    ", nickName='" + nickname + '\'' +
                    ", userId=" + userId +
                    ", roleType=" + roleType +
                    '}';
        }


        @Override
        public int compareTo(@NonNull User user) {
            int upUser = getRoleType();
            int compareUpUser = user.getRoleType();
            if (upUser == compareUpUser){
                return 0;
            }else if (upUser > compareUpUser){
                return 1;
            }else{
                return -1;
            }
        }

    }
}
