package com.ubtechinc.goldenpig.voiceChat.ui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ubtechinc.goldenpig.R;


/**
 * 发送语音提示控件
 */
public class VoiceCancelView extends RelativeLayout {


    private ImageView img;
    public VoiceCancelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.voice_cancel, this);
        img = (ImageView)findViewById(R.id.microphone);
    }

}
