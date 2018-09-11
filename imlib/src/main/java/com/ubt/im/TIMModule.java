package com.ubt.im;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

public class TIMModule {
    @Url("/im/getInfo")
    @Keep
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
    public class Response{
        private String Code;
        private String message;

        public String getCode() {
            return Code;
        }

        public void setCode(String code) {
            Code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
