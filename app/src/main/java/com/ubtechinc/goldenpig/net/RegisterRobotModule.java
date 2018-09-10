package com.ubtechinc.goldenpig.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

/**
 * @author：wululin
 * @date：2017/10/30 10:01
 * @modifier：ubt
 * @modify_date：2017/10/30 10:01
 * [A brief description]
 * version
 */
@Keep
public class RegisterRobotModule {

    @Url("/user-service-rest/v2/robot/common/binding")
    @Keep
    public class Request {

        String userName;
        String userId;
        String roleType="0";
        String serialNumber="";
        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userOnlyId) {
            this.userId = userOnlyId;
        }

        public String getRoleType() {
            return roleType;
        }

        public void setRoleType(String roleType) {
            this.roleType = roleType;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }
    }

    @Keep
    public static class Response extends BaseResponse {

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

    }
}
