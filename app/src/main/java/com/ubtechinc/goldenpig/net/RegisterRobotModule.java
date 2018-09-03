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

    @Url("/robot/common/binding")
    @Keep
    public class Request {

        String userName;
        String userOnlyId;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserOnlyId() {
            return userOnlyId;
        }

        public void setUserOnlyId(String userOnlyId) {
            this.userOnlyId = userOnlyId;
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
