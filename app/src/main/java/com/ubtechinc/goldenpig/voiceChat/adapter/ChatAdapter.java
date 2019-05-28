package com.ubtechinc.goldenpig.voiceChat.adapter;

import android.content.Context;
import android.util.Log;

import com.ubtechinc.goldenpig.common.adapter.CommonAdaper;
import com.ubtechinc.goldenpig.voiceChat.model.Message;
import com.ubtechinc.goldenpig.voiceChat.presenter.ChatPresenter;

import java.util.List;

/**
 * 聊天界面adapter
 */
public class ChatAdapter extends CommonAdaper<Message> {

    private final String TAG = "ChatAdapter";

    public ChatAdapter(Context context, List<Message> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    /**
     *  200 voice record limited
     * @param messageList
     */
    private void filter(List<Message> messageList) {
        Log.d("ChatAdapter","filter size" +messageList.size());
       try {
           if (messageList.size() > ChatPresenter.SHOW_MESSAGE_MAX) {
               Message message = messageList.get(0);
               message.remove();
               messageList.remove(0);
           }
       }catch(Exception e){
           e.printStackTrace();
       }
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView != null){
//            view = convertView;
//            viewHolder = (ViewHolder) view.getTag();
//        }else{
//            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
//            viewHolder = new ViewHolder();
//            viewHolder.leftAvatar = (CircleImageView)view.findViewById(R.id.leftAvatar);
//            viewHolder.rightAvatar = (CircleImageView)view.findViewById(R.id.rightAvatar);
//            viewHolder.leftMessage = (RelativeLayout) view.findViewById(R.id.leftMessage);
//            viewHolder.rightMessage = (RelativeLayout) view.findViewById(R.id.rightMessage);
//            viewHolder.leftPanel = (RelativeLayout) view.findViewById(R.id.leftPanel);
//            viewHolder.rightPanel = (RelativeLayout) view.findViewById(R.id.rightPanel);
//            viewHolder.right_voice_time_me=(TextView)view.findViewById(R.id.right_voice_time_me);
//            viewHolder.left_voice_time_other=(TextView)view.findViewById(R.id.left_voice_time_other);
//            viewHolder.sending = (ProgressBar) view.findViewById(R.id.sending);
//            viewHolder.error = (ImageView) view.findViewById(R.id.sendError);
//            viewHolder.sender = (TextView) view.findViewById(R.id.sender);
//            viewHolder.rightDesc = (TextView) view.findViewById(R.id.rightDesc);
//            viewHolder.systemMessage = (TextView) view.findViewById(R.id.systemMessage);
//            view.setTag(viewHolder);
//        }
//        if (position < getCount()){
//            Log.d(TAG,"ChatActivity--->ChatPresenter(send)--->MessageEvent(observable)---->ChatPresenter(observe update)----->ChatView showMessage -->ChatAdapter notify---->--->getView----> VoiceMessage(showMessage)  "+position);
//            final Message data = getItem(position);
//            if (checkMsg(data)) {
//                data.showMessage(viewHolder, getContext());
//            }
//        }
//        return view;
//    }

    @Override
    protected void convert(com.ubtechinc.goldenpig.common.adapter.ViewHolder holder, Message message, int position) {
        message.showMessage(holder, context);
    }

    @Override
    public void update(List<Message> items) {
        filter(items);
        notifyDataSetChanged();
    }


//    public class ViewHolder {
//        public ImageView leftAvatar;
//        public ImageView rightAvatar;
//        public RelativeLayout leftMessage;
//        public RelativeLayout rightMessage;
//        public RelativeLayout leftPanel;
//        public RelativeLayout rightPanel;
//        public TextView right_voice_time_me;
//        public TextView left_voice_time_other;
//        public ProgressBar sending;
//        public ImageView error;
//        public TextView sender;
//        public TextView systemMessage;
//        public TextView rightDesc;
//
//    }
}