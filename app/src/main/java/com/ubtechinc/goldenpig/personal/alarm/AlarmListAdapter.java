package com.ubtechinc.goldenpig.personal.alarm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.model.AlarmModel;
import com.ubtechinc.goldenpig.view.RecyclerOnItemLongListener;

import java.util.List;

/**
 * Created by MQ on 2017/6/9.
 */

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.AlarmListHolder> {
    private Context mContext;
    private List<AlarmModel> mList;
    private RecyclerOnItemLongListener listener;

    public AlarmListAdapter(Context mContext, List<AlarmModel> mList, RecyclerOnItemLongListener listener) {
        this.mContext = mContext;
        this.mList = mList;
        this.listener = listener;
    }

    @Override
    public AlarmListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .adapter_alarm_list, parent, false);
        return new AlarmListHolder(view);
    }

    @Override
    public void onBindViewHolder(final AlarmListHolder holder, final int position) {
        AlarmModel model = mList.get(position);
        holder.tv_amorpm.setText(model.amOrpm);
        holder.tv_time.setText(model.time);
        holder.tv_repeat.setText(model.repeatName);
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
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class AlarmListHolder extends RecyclerView.ViewHolder {
        private TextView tv_amorpm, tv_time, tv_repeat;

        public AlarmListHolder(View itemView) {
            super(itemView);
            tv_amorpm = (TextView) itemView.findViewById(R.id.tv_amorpm);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_repeat = (TextView) itemView.findViewById(R.id.tv_repeat);
        }
    }
}
