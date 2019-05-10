package com.ubtechinc.goldenpig.net;

import android.support.annotation.Keep;


import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.nets.http.Url;

import java.util.List;

/**
 * @ClassName GetRobotListModule
 * @date 1/11/2017
 * @author shiyi.wu
 * @Description 获取机器人列表
 * @modifier
 * @modify_time
 */
@Keep
public class GetRobotListModule {
    @Url("robot/common/queryRobotList")
    @Keep
    public static class Request {
        private String serialNumber;

        public String getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
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
        private List<PigInfo> result;

        public List<PigInfo> getResult() {
            return result;
        }

        public void setResult(List<PigInfo> result) {
            this.result = result;
        }
    }

}
