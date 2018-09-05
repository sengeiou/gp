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

    @Url("/robot/common/queryRobotList")
    @Keep
    public static class Request {
        private String robotUserId;

        public String getRobotUserId() {
            return robotUserId;
        }

        public void setRobotUserId(String robotUserId) {
            this.robotUserId = robotUserId;
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
        private String userImage;

        private String userName;

        private String nickName;

        private int userId;

        private int upUser;

        private String relationDate;

        public String getRelationDate() {
            return relationDate;
        }

        public void setRelationDate(String relationDate) {
            this.relationDate = relationDate;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }
        public int getUpUser() {
            return upUser;
        }

        public void setUpUser(int upUser) {
            this.upUser = upUser;
        }

        public String getUserImage() {
            return userImage;
        }

        public void setUserImage(String userImage) {
            this.userImage = userImage;
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

        @Override
        public String toString() {
            return "User{" +
                    "userImage='" + userImage + '\'' +
                    ", userName='" + userName + '\'' +
                    ", nickName='" + nickName + '\'' +
                    ", userId=" + userId +
                    ", upUser=" + upUser +
                    '}';
        }


        @Override
        public int compareTo(@NonNull User user) {
            int upUser = getUpUser();
            int compareUpUser = user.getUpUser();
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
