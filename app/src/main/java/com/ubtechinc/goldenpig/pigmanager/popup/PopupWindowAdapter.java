package com.ubtechinc.goldenpig.pigmanager.popup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.ubtechinc.goldenpig.R;

import java.util.List;

/**
 * Created by MQ on 2017/6/9.
 */

public class PopupWindowAdapter extends RecyclerView.Adapter<PopupWindowAdapter.AlarmListHolder> {
    private Context mContext;
    private List<String> mList;

    public PopupWindowAdapter(Context mContext, List<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public AlarmListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mList != null && mList.size() > 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .adapter_popup, parent, false);
            return new AlarmListHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .adapter_popup_only, parent, false);
            return new AlarmListHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final AlarmListHolder holder, final int position) {
        holder.tv_amorpm.setText(mList.get(position));
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class AlarmListHolder extends RecyclerView.ViewHolder {
        private TextView tv_amorpm;

        public AlarmListHolder(View itemView) {
            super(itemView);
            tv_amorpm = (TextView) itemView.findViewById(R.id.tv_amorpm);
        }
    }
}
