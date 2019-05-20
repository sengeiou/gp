package com.ubtechinc.goldenpig.creative;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.ubtech.utilcode.utils.SPUtils;
import com.ubtech.utilcode.utils.TimeUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.comm.entity.UserInfo;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.model.CreateModel;
import com.ubtechinc.goldenpig.view.NewCircleImageView;
import com.ubtechinc.goldenpig.view.RecyclerOnItemLongListener;

import java.util.List;


/**
 * Created by MQ on 2017/6/9.
 */

public class CreateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<CreateModel> mList;
    private RecyclerOnItemLongListener listener;
    private String userIc = "";

    public CreateAdapter(Context mContext, List<CreateModel> mList, RecyclerOnItemLongListener
            listener) {
        this.mContext = mContext;
        this.mList = mList;
        this.listener = listener;
        UserInfo currentUser = AuthLive.getInstance().getCurrentUser();
        if (currentUser != null) {
            userIc = currentUser.getUserImage();
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .adapter_create, parent, false);
            return new CreateHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .adapter_create_header, parent, false);
            return new CreateHolder2(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder aHolder, final int position) {
        CreateModel model = mList.get(position);
        if (model.type == 0) {
            CreateHolder holder = (CreateHolder) aHolder;
            holder.tv_question.setText(model.question);
            holder.tv_answer.setText(model.answer);
            holder.tv_time.setText(TimeUtils.getTime(model.createTime, TimeUtils.DATE_FORMAT_DATE));
            if (model.select == 1) {
                holder.itemView.setSelected(true);
            } else {
                holder.itemView.setSelected(false);
            }
            Glide.with(mContext).load(userIc).asBitmap().placeholder(R.drawable.ic_user_inter)
                    .error(R.drawable.ic_user_inter).diskCacheStrategy(DiskCacheStrategy.ALL).into
                    (holder.iv_ask);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        listener.onItemLongClick(v, position);
                    }
                    return false;
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(v, position);
                    }
                }
            });
        } else {
            CreateHolder2 holder = (CreateHolder2) aHolder;
            holder.tv_count.setText("共" +  SPUtils.get().getInt("create_count") + "条");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).type;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class CreateHolder extends RecyclerView.ViewHolder {
        private TextView tv_content, tv_question, tv_answer, tv_time;
        private NewCircleImageView iv_ask;

        public CreateHolder(View itemView) {
            super(itemView);
            tv_answer = itemView.findViewById(R.id.tv_answer);
            tv_question = itemView.findViewById(R.id.tv_question);
            tv_content = itemView.findViewById(R.id.tv_content);
            iv_ask = itemView.findViewById(R.id.iv_ask);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }

    public class CreateHolder2 extends RecyclerView.ViewHolder {
        TextView tv_count;

        public CreateHolder2(View itemView) {
            super(itemView);
            tv_count = itemView.findViewById(R.id.tv_count);
        }
    }

}
