package com.ubtechinc.goldenpig.voiceChat.util;

import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;


import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.app.UBTPGApplication;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * 媒体播放工具
 */
public class MediaUtil {

    private static final String TAG = "MediaUtil";

    private MediaPlayer player;
    private EventListener eventListener;
    private long playingIndex=-1;

    public long getIsReadyPlayingIndex() {
        return isReadyPlayingIndex;
    }

    public void setIsReadyPlayingIndex(long isReadyPlayingIndex) {
        this.isReadyPlayingIndex = isReadyPlayingIndex;
    }

    private long isReadyPlayingIndex=-1;

    private MediaUtil(){
        player = new MediaPlayer();
    }

    private static MediaUtil instance = new MediaUtil();

    public static MediaUtil getInstance(){
        return instance;
    }

    public MediaPlayer getPlayer() {
        return player;
    }


    public void setEventListener(final EventListener eventListener) {
        if (player != null){
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    eventListener.onStop();
                }
            });
        }
        this.eventListener = eventListener;
    }

    public void play(FileInputStream inputStream){
        try{
            if (eventListener != null){
                eventListener.onStop();
            }
            player.reset();
            player.setDataSource(inputStream.getFD());
            player.prepare();
            player.start();
        }catch (IOException e){
            Log.e(TAG, "play error:" + e);
        }
    }

    public boolean playCustomize(FileInputStream inputStream,long index){
        try{
            if (eventListener != null){
                eventListener.onStop();
            }
            UbtLogger.d(TAG,"PLAYING INDEX  "+getPlayingIndex() +"isReady  "+getIsReadyPlayingIndex());
            if(getPlayingIndex()==getIsReadyPlayingIndex()&&player.isPlaying()){
                if(UBTPGApplication.voiceMail_debug){
                    Toast.makeText(UBTPGApplication.getContext(),"stop ", Toast.LENGTH_SHORT).show();
                }
                stop();
                return false;
            }
            player.reset();
            player.setDataSource(inputStream.getFD());
            player.prepare();
            player.start();
            setPlayingIndex(index);
        }catch (IOException e){
            Log.e(TAG, "play error:" + e);
        }
        return true;
    }

    public void setPlayingIndex(long index ){
        playingIndex=index;
    }
    public long getPlayingIndex(){
        return playingIndex;
    }


    public void stop(){
        if (player != null && player.isPlaying()){
            player.stop();
        }
    }

    public long getDuration(String path){
        player = MediaPlayer.create(UBTPGApplication.getContext(), Uri.parse(path));
        return player.getDuration();
    }


    /**
     * 播放器事件监听
     */
    public interface EventListener{
        void onStop();
    }
}
