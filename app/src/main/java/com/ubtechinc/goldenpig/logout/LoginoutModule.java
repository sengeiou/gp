package com.ubtechinc.goldenpig.logout;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

@Keep
public class LoginoutModule {
    @Url("/user-service-rest/v2/user/logout")
    @Keep
    public class Request {
    }

    @Keep
    public class Response {

    }

}
