package com.ubtechinc.goldenpig.personal.interlocution;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.model.AddressBookmodel;
import com.ubtechinc.goldenpig.model.InterlocutionItemModel;
import com.ubtechinc.goldenpig.pigmanager.bean.RecordModel;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.view.RecyclerOnItemLongListener;

import java.util.List;

/**
 * Created by MQ on 2017/6/9.
 */

public class InterlocutionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<InterlocutionItemModel> mList;
    private RecyclerOnItemLongListener listener;

    public InterlocutionAdapter(Context mContext, List<InterlocutionItemModel> mList, RecyclerOnItemLongListener
            listener) {
        this.mContext = mContext;
        this.mList = mList;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .adapter_interlocution, parent, false);
            return new InterlocutionHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .adapter_interlocution_header, parent, false);
            return new InterlocutionHolder2(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (mList.get(position).type == 0) {
            InterlocutionHolder aHolder = (InterlocutionHolder) holder;
            InterlocutionItemModel model = mList.get(position);
            if (model.vQueries != null && model.vQueries.size() > 0) {
                aHolder.tv_question.setText(model.vQueries.get(0).strQuery);
            }
            if (model.vAnswers != null && model.vAnswers.size() > 0) {
                for (int i = 0; i < mList.size(); i++) {
                    if (model.vAnswers.get(i).iType == 0) {
                        aHolder.tv_answer.setText(model.vAnswers.get(i).strText);
                        break;
                    }
                }
            }
            if (model.select == 1) {
                holder.itemView.setSelected(true);
            } else {
                holder.itemView.setSelected(false);
            }
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
            InterlocutionHolder2 aHolder = (InterlocutionHolder2) holder;
            aHolder.tv_add.setText("添加问答");
            aHolder.iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(v, position);
                    }
                }
            });
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

    public class InterlocutionHolder extends RecyclerView.ViewHolder {
        private TextView tv_content, tv_question, tv_answer;

        public InterlocutionHolder(View itemView) {
            super(itemView);
            tv_answer = itemView.findViewById(R.id.tv_answer);
            tv_question = itemView.findViewById(R.id.tv_question);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }

    public class InterlocutionHolder2 extends RecyclerView.ViewHolder {
        TextView tv_add;
        ImageView iv;
        public InterlocutionHolder2(View itemView) {
            super(itemView);
            tv_add = itemView.findViewById(R.id.tv_add);
            iv = itemView.findViewById(R.id.iv);
        }
    }
}
