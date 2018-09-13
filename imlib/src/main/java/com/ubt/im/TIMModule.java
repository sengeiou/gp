package com.ubt.im;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;
@Keep
public class TIMModule {
    @Keep
    @Url("/im/getInfo")
    public class LoginRequest {
        private String signature;
        private String time;
        private String userId;
        private  String channel;

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }
    }
    @Keep
    public class Response extends  BaseResponse{
        /*private String returnMap;
        private String returnMsg;
        private String returnCode;

        public String getReturnMap() {
            return returnMap;
        }

        public void setReturnMap(String returnMap) {
            this.returnMap = returnMap;
        }

        public String getReturnMsg() {
            return returnMsg;
        }

        public void setReturnMsg(String returnMsg) {
            this.returnMsg = returnMsg;
        }

        public String getReturnCode() {
            return returnCode;
        }

        public void setReturnCode(String returnCode) {
            this.returnCode = returnCode;
        }*/
    }
    @Keep
    public class Data{

    }
}
