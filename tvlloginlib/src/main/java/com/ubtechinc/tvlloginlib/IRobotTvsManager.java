package com.ubtechinc.tvlloginlib;

public interface IRobotTvsManager {

    public void getProductId(GetRobotTvsProductIdListener listener);

    public void sendAccessToken(String accessToken, String freshToken, Long expireTime, String clientId, SendTvsAccessTokenListener listener);


    public interface GetRobotTvsProductIdListener {

        public void onSuccess(String productId, String dsn);

        public void onError();
    }

    public interface SendTvsAccessTokenListener {

        public void onSuccess();

        public void onError();
    }

}
