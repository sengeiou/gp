package com.ubtechinc.goldenpig.voiceChat.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.tencent.TIMConversationType;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageDraft;
import com.tencent.TIMMessageStatus;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.improtolib.VoiceMailContainer;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.voiceChat.ChannelInfo;
import com.ubtechinc.goldenpig.voiceChat.adapter.ChatAdapter;
import com.ubtechinc.goldenpig.voiceChat.model.CustomMessage;
import com.ubtechinc.goldenpig.voiceChat.model.FileMessage;
import com.ubtechinc.goldenpig.voiceChat.model.ImageMessage;
import com.ubtechinc.goldenpig.voiceChat.model.Message;
import com.ubtechinc.goldenpig.voiceChat.model.MessageFactory;
import com.ubtechinc.goldenpig.voiceChat.model.TextMessage;
import com.ubtechinc.goldenpig.voiceChat.model.VoiceMessage;
import com.ubtechinc.goldenpig.voiceChat.presenter.ChatPresenter;
import com.ubtechinc.goldenpig.voiceChat.util.FileUtil;
import com.ubtechinc.goldenpig.voiceChat.util.MediaUtil;
import com.ubtechinc.goldenpig.voiceChat.util.RecorderUtil;
import com.ubtechinc.goldenpig.voiceChat.viewfeatures.ChatView;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.tencent.TIMElemType.GroupSystem;

public class ChatActivity extends BaseToolBarActivity implements ChatView {
    public static ChatActivity Instance = null;
    private List<Message> messageList = new ArrayList<>();
    private ChatAdapter adapter;
    private ListView listView;
    private ChatPresenter presenter;
    private ChatInput input;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int IMAGE_STORE = 200;
    private static final int FILE_CODE = 300;
    private static final int IMAGE_PREVIEW = 400;
    private VoiceSendingView voiceSendingView;
    private VoiceCancelView  voiceCancelView;
    private String identify;
    private RecorderUtil recorder = new RecorderUtil();
    private TIMConversationType type;
    private ChannelInfo info = null;
    private String TAG="ChatActivity";
    long mVoiceRecordingTimeout=60*1000;
    private int HIDDEN_CANCEL=1000;
    public static void navToChat(Context context, String identify, TIMConversationType type, ChannelInfo info){
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("identify", identify);
        intent.putExtra("type", type);
        intent.putExtra("info", info);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instance = this;
//        setContentView(R.layout.activity_chat);
        setTitleBack(true);
        setToolBarTitle(R.string.my_pig);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();

        identify = getIntent().getStringExtra("identify");
        if (pigInfo != null) {
            identify=pigInfo.getRobotName();
            UbtLogger.d("ChatActivity", "Pig identity  "+identify +"me identity "+UbtTIMManager.userId);
        } else {
           // identify="2cb9b9a3";
           // identify="8989898989880000";
           //  identify="89898989898800000";
           // identify="990011998000581";
          //  identify="889834038000566";
            //identify="809722";
            //INSTALL XIAOMI MACHINE
            //identify="813312";
            //INSTALL SAMSUNG MACHINE
           identify="776322";
            Log.d("ChatActivity", "test identity  "+identify);
        }
       // type = (TIMConversationType) getIntent().getSerializableExtra("type");
        type=TIMConversationType.C2C;
        info = getIntent().getParcelableExtra("info");
        presenter = new ChatPresenter(this, identify, type);
        input = (ChatInput) findViewById(R.id.input_panel);
        input.setChatView(this);

        adapter = new ChatAdapter(this, messageList, R.layout.item_message);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        input.setInputMode(ChatInput.InputMode.NONE);
                        break;
                }
                return false;
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int firstItem;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && firstItem == 0) {
                    //如果拉到顶端读取更多消息
                    presenter.getMessage(messageList.size() > 0 ? messageList.get(0).getMessage() : null);
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                firstItem = firstVisibleItem;
            }
        });
        registerForContextMenu(listView);
        TemplateTitle title = (TemplateTitle) findViewById(R.id.chat_title);
        switch (type) {
            case Group:
                title.setMoreImg(R.drawable.btn_group);
                title.setMoreImgAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChatActivity.this, ChatActivity.class);
                        intent.putExtra("info", info);
                        startActivity(intent);
                    }
                });
                title.setTitleText(info.Title);
                break;
        }
        voiceSendingView = (VoiceSendingView) findViewById(R.id.voice_sending);
        voiceCancelView = (VoiceCancelView) findViewById(R.id.voice_cancel);
        presenter.start();
    }

    @Override
    protected int getConentView() {
        return R.layout.activity_chat;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    protected void onPause(){
        super.onPause();
        //退出聊天界面时输入框有内容，保存草稿
        if (input.getText().length() > 0){
            TextMessage message = new TextMessage(input.getText());
            presenter.saveDraft(message.getMessage());
        }else{
            presenter.saveDraft(null);
        }
        presenter.readMessages();
        MediaUtil.getInstance().stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.stop();
        Instance = null;
    }

    /**
     * 处理系统消息
     * @param message
     */
    @Override
    public void handleSystemMessage(TIMMessage message) {
        if (null == message || GroupSystem != message.getElement(0).getType()) {
            return;
        }
        TIMGroupSystemElem e = (TIMGroupSystemElem) message.getElement(0);
        switch (e.getSubtype()){
            case TIM_GROUP_SYSTEM_GRANT_ADMIN_TYPE:
                //LiveHelper.toast("您被设置为管理员");
                break;
            case TIM_GROUP_SYSTEM_CANCEL_ADMIN_TYPE:
               // LiveHelper.toast("您被取消管理员身份");
                break;
            case TIM_GROUP_SYSTEM_KICK_OFF_FROM_GROUP_TYPE:
                finish();
                Instance = null;
                break;
        }
    }

    /**
     * 显示消息
     * @param message
     */
    @Override
    public void showMessage(TIMMessage message) {
        if (message == null) {
            adapter.update(messageList);
        } else {
            Message mMessage = MessageFactory.getMessage(message);
            if (mMessage != null) {
                if (mMessage instanceof CustomMessage){
                    Log.d(TAG,"receive the customeMessae");
                }else{
                    if (messageList.size()==0){
                        mMessage.setHasTime(null);
                    }else{
                        Log.d(TAG,"set has time ");
                        mMessage.setHasTime(messageList.get(messageList.size()-1).getMessage());
                    }
                    messageList.add(mMessage);
                    adapter.update(messageList);
                    listView.setSelection(adapter.getCount()-1);
                }
            }
        }
    }

    /**
     * 显示消息
     * @param messages
     */
    @Override
    public void showMessage(List<TIMMessage> messages) {
        int newMsgNum = 0;
        for (int i = 0; i < messages.size(); ++i){
            Message mMessage = MessageFactory.getMessage(messages.get(i));
            if (mMessage == null || messages.get(i).status() == TIMMessageStatus.HasDeleted) continue;
            Log.d(TAG,"receive the customeMessae List<TIMMessage>"+mMessage.getMessage().getElement(0).getType());
            if (mMessage instanceof CustomMessage && (((CustomMessage) mMessage).getType() == CustomMessage.Type.TYPING ||
                    ((CustomMessage) mMessage).getType() == CustomMessage.Type.INVALID)) continue;

            if (mMessage instanceof CustomMessage ) {
                Log.d(TAG,"(CustomMessage) mMessage).getType()  "+((CustomMessage) mMessage).getType());
            }
            ++newMsgNum;
            Log.d(TAG,"receive the customeMessae List<TIMMessage> number " +newMsgNum );
            if (i != messages.size() - 1){
                mMessage.setHasTime(messages.get(i+1));
                messageList.add(0, mMessage);
            }else{
                mMessage.setHasTime(null);
                messageList.add(0, mMessage);
            }

        }
        adapter.update(messageList);
        listView.setSelection(newMsgNum);
        UbtLogger.d(TAG,"showMessage number   "+newMsgNum);
    }

    /**
     * 清除所有消息，等待刷新
     */
    @Override
    public void clearAllMessage() {
        messageList.clear();
    }

    /**
     * 发送消息成功
     * @param message 返回的消息
     */
    @Override
    public void onSendMessageSuccess(TIMMessage message) {
        showMessage(message);
    }

    /**
     * 发送消息失败
     *
     * @param code 返回码
     * @param desc 返回描述
     */
    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {
        long id = message.getMsgUniqueId();
        for (Message msg : messageList){
            if (msg.getMessage().getMsgUniqueId() == id){
                switch (code){
                    case 80001:
                        //发送内容包含敏感词
                        msg.setDesc(getString(R.string.chat_content_bad));
                        adapter.update(messageList);
                        if(UBTPGApplication.voiceMail_debug) {
                            Toast.makeText(this, "内容含有敏感词", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 6011:
                        adapter.update(messageList);
                        if(UBTPGApplication.voiceMail_debug) {
                            Toast.makeText(this, "接收方不存在(desc: to user invalid)", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        }
    }

    /**
     * 发送图片消息
     */
    @Override
    public void sendImage() {
//        Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
//        intent_album.setType("image/*");
//        startActivityForResult(intent_album, IMAGE_STORE);
    }

    /**
     * 发送照片消息
     */
    @Override
    public void sendPhoto() {
//        DateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
//        String filename = "IMG_" + formatter.format(new Date()) + ".jpg";
//        cameraFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/", filename);
//        if (!cameraFile.exists() && !cameraFile.isDirectory()) {
//            try {
//                cameraFile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            fileUri = Uri.fromFile(cameraFile);
//            intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//            startActivityForResult(intent_photo, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
//        } else {
//            fileUri = FileProvider.getUriForFile(ChatActivity.this, "com.example.nyapp.fileprovider", cameraFile);
//            intent_photo.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
//            intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);//将拍取的照片保存到指定URI
//            startActivityForResult(intent_photo, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
//        }
    }

    /**
     * 发送文本消息
     */
    @Override
    public void sendText() {
           Message message = new TextMessage(input.getText());
           presenter.sendMessage(message.getMessage(), ChatPresenter.MESSAGE_TEXT);
           input.setText("");
    }

    /**
     * 发送文件
     */
    @Override
    public void sendFile() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*");
//        startActivityForResult(intent, FILE_CODE);
    }

    /**
     * 开始发送语音消息
     */
    @Override
    public void startSendVoice() {
        voiceSendingView.setVisibility(View.VISIBLE);
        voiceCancelView.setVisibility(View.GONE);
        voiceSendingView.showRecording();
        if(!recorder.isRecording()) {
            recorder.startRecording();
           // startVoiceRecordingTask();
        }
    }

    /**
     * 结束发送语音消息
     */
    @Override
    public void endSendVoice() {
//        if(!recorder.isRecording()){
//            return;
//        }
      //  stopVoiceRecordingTask();
        voiceSendingView.release();
        voiceSendingView.setVisibility(View.GONE);
        recorder.stopRecording();
        if (recorder.getTimeInterval() < 1) {
            Toast.makeText(this, getResources().getString(R.string.chat_audio_too_short), Toast.LENGTH_SHORT).show();
        } else {
            if (recorder.getTimeInterval() == mVoiceRecordingTimeout) {
                Toast.makeText(this, getResources().getString(R.string.chat_audio_too_long), Toast.LENGTH_SHORT).show();
            }
            Message message = new VoiceMessage(recorder.getTimeInterval(), recorder.getFilePath());
            presenter.sendMessage(message.getMessage(), ChatPresenter.MESSAGE_VOICE);

        }
    }

    @Override
    public void showCancelSending() {
        voiceSendingView.setVisibility(View.GONE);
        voiceCancelView.setVisibility(View.VISIBLE);
    }

    @Override
    public void cancelSending() {
//        if(!recorder.isRecording()){
//            return;
//        }
        //stopVoiceRecordingTask();
        mHandler.sendEmptyMessageDelayed(HIDDEN_CANCEL,100);
        recorder.stopRecording();
    }
    /**
     * 发送小视频消息
     * @param fileName 文件名
     */
    @Override
    public void sendVideo(String fileName) {
//        Message message = new VideoMessage(fileName);
//        presenter.sendMessage(message.getMessage(),ChatPresenter.MESSAGE_VIDEO);
    }

    /**
     * 结束发送语音消息
     */
    @Override
    public void cancelSendVoice() {
    }

    /**
     * 正在发送
     */
    @Override
    public void sending() {
        if (type == TIMConversationType.C2C){
            Message message = new CustomMessage(CustomMessage.Type.TYPING);
            presenter.sendOnlineMessage(message.getMessage());
        }
    }

    /**
     * 显示草稿
     */
    @Override
    public void showDraft(TIMMessageDraft draft) {
        input.getText().append(TextMessage.getString(draft.getElems(), this));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Message message = messageList.get(info.position);
        menu.add(0, 1, Menu.NONE, getString(R.string.chat_del));
        if (message.isSendFail()){
            menu.add(0, 2, Menu.NONE, getString(R.string.chat_resend));
        }
        if (message instanceof ImageMessage || message instanceof FileMessage){
            menu.add(0, 3, Menu.NONE, getString(R.string.chat_save));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Message message = messageList.get(mi.position);
        switch (item.getItemId()) {
            case 1:
                message.remove();
                messageList.remove(mi.position);
                adapter.update(messageList);
                break;
            case 2:
                messageList.remove(message);
                presenter.sendMessage(message.getMessage(),ChatPresenter.MESSAGE_TEXT);
                break;
            case 3:
                message.save();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == IMAGE_STORE) {
            if (resultCode == RESULT_OK && data != null) {
                showImagePreview(FileUtil.getFilePath(this, data.getData()));
            }

        } else if (requestCode == FILE_CODE) {
            if (resultCode == RESULT_OK) {
                sendFile(FileUtil.getFilePath(this, data.getData()));
            }
        } else if (requestCode == IMAGE_PREVIEW){
            if (resultCode == RESULT_OK) {
                boolean isOri = data.getBooleanExtra("isOri",false);
                String path = data.getStringExtra("path");
                File file = new File(path);
                if (file.exists() && file.length() > 0){
                    if (file.length() > 1024 * 1024 * 10){
                        Toast.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
                    }else{
                        Message message = new ImageMessage(path,isOri);
                        presenter.sendMessage(message.getMessage(),ChatPresenter.MESSAGE_IMAGE);
                    }
                }else{
                    Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showImagePreview(String path){
        if (path == null) return;
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("path", path);
        startActivityForResult(intent, IMAGE_PREVIEW);
    }

    private void sendFile(String path){
        if (path == null) return;
        File file = new File(path);
        if (file.exists()){
            if (file.length() > 1024 * 1024 * 10){
                Toast.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
            }else{
                Message message = new FileMessage(path);
                presenter.sendMessage(message.getMessage(),ChatPresenter.MESSAGE_FILE);
            }
        }else{
            Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
        }
    }
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(android.os.Message message) {
            if(message.what==HIDDEN_CANCEL) {
                voiceCancelView.setVisibility(View.GONE);

            }
        }
    };

}
