package com.ubtechinc.tvlloginlib;

import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.env.ELoginPlatform;

public class WXClient extends BaseClient {

    public WXClient(LoginProxy proxy, ClientResultListener listener) {
        super(proxy, ELoginPlatform.WX, listener);
    }


    @Override
    public boolean isAppInstall() {
        return proxy.isWXAppInstalled();
    }

    @Override
    public boolean isSupportLogin() {
        return proxy.isWXAppSupportAPI();
    }
}
