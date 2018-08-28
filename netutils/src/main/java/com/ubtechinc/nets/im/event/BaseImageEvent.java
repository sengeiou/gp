package com.ubtechinc.nets.im.event;

/**
 * @作者：liudongyang
 * @日期: 18/4/2 20:50
 * @描述:
 */

public abstract class BaseImageEvent {

    public static final int ALBUMS_DATA_CHANGE_EVENT = 1000;

    public static final int ALBUMS_IMAGE_DOWNLOAD_EVENT = 1001;


    abstract public int getType();
}
