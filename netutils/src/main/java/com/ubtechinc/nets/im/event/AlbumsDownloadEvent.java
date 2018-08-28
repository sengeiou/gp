package com.ubtechinc.nets.im.event;

/**
 * @作者：liudongyang
 * @日期: 18/4/2 12:09
 * @描述:
 */

public class AlbumsDownloadEvent extends BaseImageEvent {

    private String fileName;

    private boolean isDownloadSuccess;

    public AlbumsDownloadEvent(String fileName, boolean isDownloadSuccess) {
        this.fileName = fileName;
        this.isDownloadSuccess = isDownloadSuccess;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isDownloadSuccess() {
        return isDownloadSuccess;
    }

    @Override
    public int getType() {
        return ALBUMS_IMAGE_DOWNLOAD_EVENT;
    }
}
