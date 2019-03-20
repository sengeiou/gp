package com.ubtechinc.goldenpig.main;

public class UpdateInfoModel {

    private String version;
    private String versionInfo;
    private String url;
    private String updateType;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionInfo() {
        return versionInfo;
    }

    public void setVersionInfo(String versionInfo) {
        this.versionInfo = versionInfo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    @Override
    public String toString() {
        return "UpdateInfoModel{" +
                "version='" + version + '\'' +
                ", versionInfo='" + versionInfo + '\'' +
                ", url='" + url + '\'' +
                ", updateType='" + updateType + '\'' +
                '}';
    }
}
