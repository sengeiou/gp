package com.ubtechinc.goldenpig.voiceChat.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMFaceElem;
import com.tencent.TIMMessage;
import com.tencent.TIMSoundElem;
import com.tencent.TIMTextElem;
import com.tencent.TIMValueCallBack;
import com.ubt.improtolib.VoiceMailContainer;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.common.adapter.ViewHolder;
import com.ubtechinc.goldenpig.voiceChat.presenter.ChatPresenter;
import com.ubtechinc.goldenpig.voiceChat.ui.ChatActivity;
import com.ubtechinc.goldenpig.voiceChat.util.FileUtil;
import com.ubtechinc.goldenpig.voiceChat.util.MediaUtil;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 语音消息数据
 */
public class VoiceMessage extends Message {

    private static final String TAG = "VoiceMessage";

    public VoiceMessage(TIMMessage message){
        this.message = message;
    }
    /*
            protobuf format send voice information
         */
    public VoiceMessage(byte[] data, String duration){
        message = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(data);
        elem.setDesc(duration);
        message.addElement(elem);
    }

    /**
     * 语音消息构造方法
     *
     * @param duration 时长
     * @param data 语音数据
     */
    public VoiceMessage(long duration,byte[] data){
        message = new TIMMessage();
        TIMSoundElem elem = new TIMSoundElem();
        elem.setData(data);
        elem.setDuration(duration);  //填写语音时长
        message.addElement(elem);
    }

    /**
     * 语音消息构造方法
     *
     * @param duration 时长
     * @param filePath 语音数据地址
     */
    public VoiceMessage(long duration,String filePath){
        message = new TIMMessage();
        TIMSoundElem elem = new TIMSoundElem();
        elem.setPath(filePath);
        elem.setDuration(duration);  //填写语音时长
        message.addElement(elem);
    }

    @Override
    public String getSender() {
        return super.getSender();
    }



    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context 显示消息的上下文
     */
    @Override
    public void showMessage(ViewHolder viewHolder, Context context) {
//        Exception e1 = new Exception("THIS IS VOICE MESSAGE SHOW MESSAGE ");
//        e1.printStackTrace();
       if(!ChatActivity.VERSION_BYPASS) {
           try {
               TIMCustomElem customElem = (TIMCustomElem) message.getElement(0);
               ChannelMessageContainer.ChannelMessage msg = ChannelMessageContainer.ChannelMessage
                       .parseFrom((byte[]) customElem.getData());
               Log.d(TAG, "message before " + msg.getHeader().getAction());
               if (msg.getHeader().getAction().equals("/im/voicemail/receiver")) {
                   VoiceMailContainer.VoiceMail mVoiceData = msg.getPayload().unpack(VoiceMailContainer.VoiceMail.class);
                   Log.d(TAG, "message before type " + mVoiceData.getMsgType());

                   if (mVoiceData.getMsgType() == ChatPresenter.MESSAGE_TEXT) {
                       Log.d(TAG, "message text  " + isSelf());
                       clearView(viewHolder);
                       boolean hasText = false;
                       TextView tv = new TextView(UBTPGApplication.getContext());
                       tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                       tv.setTextColor(UBTPGApplication.getContext().getResources().getColor(isSelf() ? R.color.white : R.color.black));
                       List<TIMElem> elems = new ArrayList<>();
                       for (int i = 0; i < message.getElementCount(); ++i) {
                           elems.add(message.getElement(i));
//                         if (message.getElement(i).getType() == TIMElemType.Text){
//                             hasText = true;
//                         }
                       }
                       SpannableStringBuilder stringBuilder = getString(elems, context);
//                     if (!hasText){
//                         stringBuilder.insert(0," ");
//                     }
                       tv.setText(stringBuilder);
                       getBubbleView(viewHolder).addView(tv);
                       viewHolder.getView(R.id.right_voice_time_me).setVisibility(View.INVISIBLE);
                       viewHolder.getView(R.id.left_voice_time_other).setVisibility(View.INVISIBLE);
                       showStatus(viewHolder);
                   } else if (mVoiceData.getMsgType() == ChatPresenter.MESSAGE_VOICE) {
                       Log.d(TAG, "message voice " + isSelf());
                       LinearLayout linearLayout = new LinearLayout(UBTPGApplication.getContext());
                       linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                       linearLayout.setGravity(Gravity.CENTER);
                       ImageView voiceIcon = new ImageView(UBTPGApplication.getContext());
                       voiceIcon.setBackgroundResource(message.isSelf() ? R.drawable.right_voice : R.drawable.left_voice);
                       final AnimationDrawable frameAnimatio = (AnimationDrawable) voiceIcon.getBackground();

                       TextView tv = new TextView(UBTPGApplication.getContext());
                       tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                       tv.setTextColor(UBTPGApplication.getContext().getResources().getColor(isSelf() ? R.color.white : R.color.black));
                       //RECORD TIME ON THE MESSAGE
                       // tv.setText(String.valueOf(((TIMCustomElem) message.getElement(0)).getDesc()) + "’");
                       int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, context.getResources().getDisplayMetrics());
                       int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, context.getResources().getDisplayMetrics());
                       LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                       LinearLayout.LayoutParams imageLp = new LinearLayout.LayoutParams(width, height);
                       if (message.isSelf()) {
                           linearLayout.addView(tv);
                           imageLp.setMargins(10, 0, 0, 0);
                           voiceIcon.setLayoutParams(imageLp);
                           linearLayout.addView(voiceIcon);
                           viewHolder.getView(R.id.right_voice_time_me).setVisibility(View.VISIBLE);
                           viewHolder.setText(R.id.right_voice_time_me, String.valueOf(((TIMCustomElem) message.getElement(0)).getDesc()) + "\"");
                       } else {
                           voiceIcon.setLayoutParams(imageLp);
                           linearLayout.addView(voiceIcon);
                           lp.setMargins(10, 0, 0, 0);
                           tv.setLayoutParams(lp);
                           linearLayout.addView(tv);
                           viewHolder.getView(R.id.left_voice_time_other).setVisibility(View.VISIBLE);
                           viewHolder.setText(R.id.left_voice_time_other, mVoiceData.getElapsedMillis() / 1000 + "\"");
                       }
                       clearView(viewHolder);
                       getBubbleView(viewHolder).addView(linearLayout);
                       getBubbleView(viewHolder).setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               VoiceMessage.this.playAudio(frameAnimatio);
                           }
                       });
                       showStatus(viewHolder);
                   }
               }

           } catch (Exception e) {
               e.printStackTrace();
           }
       }else{
           LinearLayout linearLayout = new LinearLayout(UBTPGApplication.getContext());
           linearLayout.setOrientation(LinearLayout.HORIZONTAL);
           linearLayout.setGravity(Gravity.CENTER);
           ImageView voiceIcon = new ImageView(UBTPGApplication.getContext());
           voiceIcon.setBackgroundResource(message.isSelf() ? R.drawable.right_voice : R.drawable.left_voice);
           final AnimationDrawable frameAnimatio = (AnimationDrawable) voiceIcon.getBackground();

           TextView tv = new TextView(UBTPGApplication.getContext());
           tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
           tv.setTextColor(UBTPGApplication.getContext().getResources().getColor(isSelf() ? R.color.white : R.color.black));
          //  tv.setText(String.valueOf(((TIMSoundElem) message.getElement(0)).getDuration()) + "’");
           int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, context.getResources().getDisplayMetrics());
           int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, context.getResources().getDisplayMetrics());
           LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
           LinearLayout.LayoutParams imageLp = new LinearLayout.LayoutParams(width, height);
            //VOICE LENGTH IS NOT SAME
           long voicetime=0;
           if(message.isSelf()) {
               voicetime = ((TIMSoundElem) message.getElement(0)).getDuration();
           }else {
               //milliseconds
              voicetime = ((TIMSoundElem) message.getElement(0)).getDuration()%1000;
           }
           if(5<voicetime&&voicetime<10){
               tv.setText("     ");
           }else if(10<voicetime&&voicetime<15){
               tv.setText("         ");
           }else if(15<voicetime&&voicetime<20){
               tv.setText("             ");
           }else if(20<voicetime&&voicetime<25){
               tv.setText("                 ");
           }else if(25<voicetime&&voicetime<30){
               tv.setText("                     ");
           }else if(voicetime>30){
               tv.setText("                         ");
           }
           if (message.isSelf()) {
               linearLayout.addView(tv);
               imageLp.setMargins(10, 0, 0, 0);
               voiceIcon.setLayoutParams(imageLp);
               linearLayout.addView(voiceIcon);
               viewHolder.getView(R.id.right_voice_time_me).setVisibility(View.VISIBLE);
               viewHolder.setText(R.id.right_voice_time_me, String.valueOf(((TIMSoundElem) message.getElement(0)).getDuration()) + "\"");

           } else {
               voiceIcon.setLayoutParams(imageLp);
               linearLayout.addView(voiceIcon);
               lp.setMargins(10, 0, 0, 0);
               tv.setLayoutParams(lp);
               linearLayout.addView(tv);
               viewHolder.getView(R.id.left_voice_time_other).setVisibility(View.VISIBLE);
               viewHolder.setText(R.id.left_voice_time_other, String.valueOf(((TIMSoundElem) message.getElement(0)).getDuration()/1000) + "\"");
           }
           clearView(viewHolder);
           getBubbleView(viewHolder).addView(linearLayout);
           getBubbleView(viewHolder).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   VoiceMessage.this.playAudio(frameAnimatio);
               }
           });
           showStatus(viewHolder);
       }
    }



    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        return UBTPGApplication.getContext().getString(R.string.summary_voice);
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    private void playAudio(final AnimationDrawable frameAnimatio) {

        if(!ChatActivity.VERSION_BYPASS) {
            TIMCustomElem customElem = (TIMCustomElem) message.getElement(0);
            customElem.getData();
            try {
                ChannelMessageContainer.ChannelMessage msg = ChannelMessageContainer.ChannelMessage
                        .parseFrom((byte[]) customElem.getData());
                VoiceMailContainer.VoiceMail mVoiceData = msg.getPayload().unpack(VoiceMailContainer.VoiceMail.class);
                //mVoiceData.getMessage().toByteArray();
                File tempAudio = FileUtil.getTempFile(FileUtil.FileType.AUDIO);
                FileOutputStream fos = new FileOutputStream(tempAudio);
                fos.write(mVoiceData.getMessage().toByteArray());
                fos.close();
                FileInputStream fis = new FileInputStream(tempAudio);
                MediaUtil.getInstance().play(fis);
                frameAnimatio.start();
                MediaUtil.getInstance().setEventListener(new MediaUtil.EventListener() {
                    @Override
                    public void onStop() {
                        frameAnimatio.stop();
                        frameAnimatio.selectDrawable(0);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            TIMSoundElem elem = (TIMSoundElem) message.getElement(0);
            elem.getSound(new TIMValueCallBack<byte[]>() {
                @Override
                public void onError(int i, String s) {
                }

                @Override
                public void onSuccess(byte[] bytes) {
                    try {
                        File tempAudio = FileUtil.getTempFile(FileUtil.FileType.AUDIO);
                        FileOutputStream fos = new FileOutputStream(tempAudio);
                        fos.write(bytes);
                        fos.close();
                        FileInputStream fis = new FileInputStream(tempAudio);
                        MediaUtil.getInstance().play(fis);
                        frameAnimatio.start();
                        MediaUtil.getInstance().setEventListener(new MediaUtil.EventListener() {
                            @Override
                            public void onStop() {
                                frameAnimatio.stop();
                                frameAnimatio.selectDrawable(0);
                            }
                        });
                    } catch (IOException e) {

                    }
                }
            });
        }
    }

    public static SpannableStringBuilder getString(List<TIMElem> elems, Context context){
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        for (int i = 0; i<elems.size(); ++i){
            switch (elems.get(i).getType()){
                case Face:
                    TIMFaceElem faceElem = (TIMFaceElem) elems.get(i);
                    int startIndex = stringBuilder.length();
                    try{
                        AssetManager am = context.getAssets();
                        InputStream is = am.open(String.format("emoticon/%d.gif", faceElem.getIndex()));
                        if (is == null) {
                            continue;
                        }
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        Matrix matrix = new Matrix();
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        matrix.postScale(2, 2);
                        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                width, height, matrix, true);
                        ImageSpan span = new ImageSpan(context, resizedBitmap, ImageSpan.ALIGN_BASELINE);
                        stringBuilder.append(String.valueOf(faceElem.getIndex()));
                        stringBuilder.setSpan(span, startIndex, startIndex + getNumLength(faceElem.getIndex()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        is.close();
                    }catch (IOException e){
                            e.getMessage();
                    }
                    break;
                case Text:
                    TIMTextElem textElem = (TIMTextElem) elems.get(i);
                    stringBuilder.append(textElem.getText());
                    break;
                default:
                    try{
                    TIMCustomElem customElem=(TIMCustomElem)elems.get(0);
                    ChannelMessageContainer.ChannelMessage msg = ChannelMessageContainer.ChannelMessage
                            .parseFrom((byte[]) customElem.getData());
                    VoiceMailContainer.VoiceMail mVoiceData = msg.getPayload().unpack(VoiceMailContainer.VoiceMail.class);
                    stringBuilder.append(new String(mVoiceData.getMessage().toByteArray()));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                break;
            }

        }
        return stringBuilder;
    }

    private static int getNumLength(int n){
        return String.valueOf(n).length();
    }
}
