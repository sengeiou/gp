package com.ubtechinc.goldenpig.personal.alarm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.model.AddressBookmodel;
import com.ubtechinc.goldenpig.model.AlarmModel;

import java.util.List;

/**
 * Created by MQ on 2017/6/9.
 */

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.AlarmListHolder> {
    private Context mContext;
    private List<AlarmModel> mList;

    public AlarmListAdapter(Context mContext, List<AlarmModel> mList) {
        this.mContext = mContext;
        this.mList = mList;
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
        holder.tv_time_state.setText(model.time_state);
        holder.tv_time.setText(model.time);
        holder.tv_date.setText(model.date);
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class AlarmListHolder extends RecyclerView.ViewHolder {
        private TextView tv_time_state, tv_time, tv_date;

        public AlarmListHolder(View itemView) {
            super(itemView);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_time_state = (TextView) itemView.findViewById(R.id.tv_time_state);
        }
    }
}
