package com.ubtechinc.goldenpig.personal.remind;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.model.InterlocutionItemModel;
import com.ubtechinc.goldenpig.model.RemindModel;

import java.util.List;

/**
 * Created by MQ on 2017/6/9.
 */

public class RemindAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<RemindModel> mList;

    public RemindAdapter(Context mContext, List<RemindModel> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .adapter_remind, parent, false);
            return new RemindHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .adapter_remind_header, parent, false);
            return new RemindHolder2(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (mList.get(position).type == 0) {
            RemindHolder aHolder = (RemindHolder) holder;
            RemindModel model = mList.get(position);
            aHolder.tv_remind_msg.setText(model.sNote);
            aHolder.tv_am.setText(model.amOrpm);
            aHolder.tv_time.setText(model.time);
            aHolder.tv_date.setText(model.date);
        } else {
            RemindHolder2 aHolder = (RemindHolder2) holder;
            aHolder.tv_add.setText("设置提醒事项");
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

    public class RemindHolder extends RecyclerView.ViewHolder {
        private TextView tv_remind_msg, tv_am, tv_time, tv_date;

        public RemindHolder(View itemView) {
            super(itemView);
            tv_remind_msg = itemView.findViewById(R.id.tv_remind_msg);
            tv_am = itemView.findViewById(R.id.tv_am);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_date = itemView.findViewById(R.id.tv_date);
        }
    }

    public class RemindHolder2 extends RecyclerView.ViewHolder {
        TextView tv_add;

        public RemindHolder2(View itemView) {
            super(itemView);
            tv_add = itemView.findViewById(R.id.tv_add);
        }
    }
}
