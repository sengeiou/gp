package com.ubtechinc.goldenpig.message;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.view.NewCircleImageView;
import com.ubtechinc.goldenpig.view.RecyclerOnItemLongListener;

import java.util.List;


/**
 * Created by MQ on 2017/6/9.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<MessageModel> mList;
    private RecyclerOnItemLongListener listener;

    public MessageAdapter(Context mContext, List<MessageModel> mList, RecyclerOnItemLongListener listener) {
        this.mContext = mContext;
        this.mList = mList;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .adapter_system_msg_list, parent, false);
            return new SystemMsgHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .adapter_system_msg_top, parent, false);
            return new SystemMsgTopHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        MessageModel model = mList.get(position);
        if (viewHolder instanceof SystemMsgHolder) {
            SystemMsgHolder holder = (SystemMsgHolder) viewHolder;
            holder.tv_title.setText(model.title);
            if (model.status != null && model.status.equals("0")) {
                holder.iv_red_point.setVisibility(View.VISIBLE);
            } else {
                holder.iv_red_point.setVisibility(View.GONE);
            }
            if (model.select == 1) {
                holder.itemView.setSelected(true);
            } else {
                holder.itemView.setSelected(false);
            }
            holder.tv_content.setText(model.content);
            holder.tv_date.setText(model.createTime);
            holder.rl_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(v, position);
                    }
                }
            });
            UbtLogger.d("msgAdapter", "model.imgUrl:" + model.imgUrl);
            Glide.with(mContext).load(model.imgUrl).asBitmap().placeholder(R.drawable.ic_notice)
                    .error(R.drawable.ic_notice).diskCacheStrategy(DiskCacheStrategy.ALL).into
                    (holder.iv);
            holder.rl_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        listener.onItemLongClick(v, position);
                    }
                    return false;
                }
            });
//            if (position == mList.size() - 1) {
//                holder.view_curline_last2.setVisibility(View.VISIBLE);
//            } else {
//                holder.view_curline_last2.setVisibility(View.GONE);
//            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class SystemMsgHolder extends RecyclerView.ViewHolder {
        private NewCircleImageView iv;
        private RelativeLayout rl_item;
        private ImageView iv_red_point;
        private TextView tv_title, tv_date, tv_content;
        private View view_curline, view_curline_last, view_curline_last2;

        public SystemMsgHolder(View itemView) {
            super(itemView);
            iv = (NewCircleImageView) itemView.findViewById(R.id.iv);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            view_curline = itemView.findViewById(R.id.view_curline);
//            view_curline_last = itemView.findViewById(R.id.view_curline_last);
            iv_red_point = itemView.findViewById(R.id.iv_red_point);
            tv_date = itemView.findViewById(R.id.tv_date);
            rl_item = itemView.findViewById(R.id.rl_item);
            tv_content = itemView.findViewById(R.id.tv_content);
//            view_curline_last2 = itemView.findViewById(R.id.view_curline_last2);
        }
    }

    public class SystemMsgTopHolder extends RecyclerView.ViewHolder {

        public SystemMsgTopHolder(View itemView) {
            super(itemView);
        }
    }

}
