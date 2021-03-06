package com.ubtechinc.goldenpig.voiceChat.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.tencent.TIMCallBack;
import com.tencent.TIMMessage;
import com.tencent.TIMSnapshot;
import com.tencent.TIMVideo;
import com.tencent.TIMVideoElem;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.common.adapter.ViewHolder;
import com.ubtechinc.goldenpig.voiceChat.ui.ChatActivity;
import com.ubtechinc.goldenpig.voiceChat.util.FileUtil;
import com.ubtechinc.goldenpig.voiceChat.util.MediaUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 小视频消息数据
 */
public class VideoMessage extends Message {

    private static final String TAG = "VideoMessage";



    public VideoMessage(TIMMessage message){
        this.message = message;
    }

    public VideoMessage(String fileName){
        message = new TIMMessage();
        TIMVideoElem elem = new TIMVideoElem();
        elem.setVideoPath(FileUtil.getCacheFilePath(fileName));
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(FileUtil.getCacheFilePath(fileName), MediaStore.Images.Thumbnails.MINI_KIND);
        elem.setSnapshotPath(FileUtil.createFile(thumb, new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())));
        TIMSnapshot snapshot = new TIMSnapshot();
        snapshot.setType("PNG");
        snapshot.setHeight(thumb.getHeight());
        snapshot.setWidth(thumb.getWidth());
        TIMVideo video = new TIMVideo();
        video.setType("MP4");
        video.setDuaration(MediaUtil.getInstance().getDuration(FileUtil.getCacheFilePath(fileName)));
        elem.setSnapshot(snapshot);
        elem.setVideo(video);
        message.addElement(elem);
    }

    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context 显示消息的上下文
     */
    @Override
    public void showMessage(final ViewHolder viewHolder, final Context context) {
        clearView(viewHolder);
        final TIMVideoElem e = (TIMVideoElem) message.getElement(0);
        switch (message.status()){
            case Sending:
                showSnapshot(viewHolder, BitmapFactory.decodeFile(e.getSnapshotPath(), new BitmapFactory.Options()));
                break;
            case SendSucc:

                final TIMSnapshot snapshot = e.getSnapshotInfo();
                if (FileUtil.isCacheFileExist(snapshot.getUuid())){
                    showSnapshot(viewHolder, BitmapFactory.decodeFile(FileUtil.getCacheFilePath(snapshot.getUuid()), new BitmapFactory.Options()));
                }else{
                    snapshot.getImage(FileUtil.getCacheFilePath(snapshot.getUuid()), new TIMCallBack() {
                        @Override
                        public void onError(int i, String s) {
                            Log.e(TAG, "get snapshot failed. code: " + i + " errmsg: " + s);
                        }

                        @Override
                        public void onSuccess() {
                            showSnapshot(viewHolder, BitmapFactory.decodeFile(FileUtil.getCacheFilePath(snapshot.getUuid()), new BitmapFactory.Options()));
                        }
                    });
                }
                final String fileName = e.getVideoInfo().getUuid();
                if (!FileUtil.isCacheFileExist(fileName)) {
                    e.getVideoInfo().getVideo(FileUtil.getCacheFilePath(fileName), new TIMCallBack() {
                        @Override
                        public void onError(int i, String s) {
                            Log.e(TAG, "get video failed. code: " + i + " errmsg: " + s);
                        }

                        @Override
                        public void onSuccess() {
                            setVideoEvent(viewHolder,fileName,context);
                        }
                    });
                }else{
                    setVideoEvent(viewHolder,fileName,context);
                }
                break;
        }
        showStatus(viewHolder);
    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        return UBTPGApplication.getContext().getString(R.string.summary_video);
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }


    /**
     * 显示缩略图
     */
    private void showSnapshot(final ViewHolder viewHolder,final Bitmap bitmap){
        if (bitmap == null) return;
        ImageView imageView = new ImageView(UBTPGApplication.getContext());
        imageView.setImageBitmap(bitmap);
        getBubbleView(viewHolder).addView(imageView);
    }

    private void showVideo(String path, Context context){
        if (context == null) return;
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("path", path);
        context.startActivity(intent);
    }

    private void setVideoEvent(final ViewHolder viewHolder, final String fileName, final Context context){
        getBubbleView(viewHolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVideo(FileUtil.getCacheFilePath(fileName),context);
            }
        });
    }
}
