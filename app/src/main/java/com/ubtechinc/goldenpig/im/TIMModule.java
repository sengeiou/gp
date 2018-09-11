package com.ubtechinc.goldenpig.im;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

public class TIMModule {
    @Url("/im/getInfo")
    @Keep
    public class LoginRequest {

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
