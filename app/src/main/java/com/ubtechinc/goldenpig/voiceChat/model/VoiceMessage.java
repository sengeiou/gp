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
import android.widget.Toast;

import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMFaceElem;
import com.tencent.TIMMessage;
import com.tencent.TIMSoundElem;
import com.tencent.TIMTextElem;
import com.tencent.TIMValueCallBack;
import com.ubt.improtolib.VoiceMailContainer;
import com.ubtech.utilcode.utils.LogUtils;
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
    //two spacce ==2mm
    private static final String voice_metrics="  ";

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
              voicetime = ((TIMSoundElem) message.getElement(0)).getDuration()/1000;
           }
//           if(5<=voicetime&&voicetime<10){
//               tv.setText("     ");
//           }else if(10<=voicetime&&voicetime<15){
//               tv.setText("         ");
//           }else if(15<=voicetime&&voicetime<20){
//               tv.setText("             ");
//           }else if(20<=voicetime&&voicetime<25){
//               tv.setText("                 ");
//           }else if(25<=voicetime&&voicetime<30){
//               tv.setText("                     ");
//           }else if(voicetime>=30){
//               tv.setText("                         ");
//           }
           //to improve the voice length from product advices
       try {
           String mcontent = "";
           if (1 <= voicetime && voicetime <= 9) {
               for (int i = 0; i < (voicetime - 1); i++) {
                   //one space is equal to 2mms
                   LogUtils.d("F voice length" + voicetime + "length begin:" + mcontent + ":end");
                   mcontent=mcontent.concat(voice_metrics);
               }
           }
           if (10 <= voicetime && voicetime <= 19) {
               for (int i = 0; i < 9; i++) {
                   mcontent=mcontent.concat(voice_metrics);
               }
           }
           if (20 <= voicetime && voicetime <= 29) {
               for (int i = 0; i < 10; i++) {
                   mcontent=mcontent.concat(voice_metrics);
               }
           }
           if (30 <= voicetime && voicetime <= 39) {
               for (int i = 0; i < 11; i++) {
                   mcontent=mcontent.concat(voice_metrics);
               }
           }
           if (40 <= voicetime && voicetime <= 49) {
               for (int i = 0; i < 12; i++) {
                   mcontent=mcontent.concat(voice_metrics);
               }
           }
           if (50 <= voicetime && voicetime <= 59) {
               for (int i = 0; i < 13; i++) {
                   mcontent=mcontent.concat(voice_metrics);
               }
           }
           if (voicetime == 60) {
               for (int i = 0; i < 14; i++) {
                   mcontent=mcontent.concat(voice_metrics);
               }
           }
           tv.setText(mcontent);
           LogUtils.d("voice length" + voicetime + "length begin:" + mcontent + ":end");
       }catch(Exception e){
               e.printStackTrace();
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
                   if(UBTPGApplication.voiceMail_debug){
                       Toast.makeText(UBTPGApplication.getContext(),"click voice xxx ", Toast.LENGTH_SHORT).show();
                   }
                   MediaUtil.getInstance().setIsReadyPlayingIndex(message.getMsgUniqueId());
                   VoiceMessage.this.playAudio(frameAnimatio);
               }
           });
           showStatus(viewHolder);

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
//                        MediaUtil.getInstance().play(fis);
                       if (!MediaUtil.getInstance().playCustomize(fis,message.getMsgUniqueId())){
                            return;
                       }
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
