package com.ubtechinc.goldenpig.pigmanager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubtech.utilcode.utils.TimeUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.model.AddressBookmodel;
import com.ubtechinc.goldenpig.pigmanager.bean.RecordModel;

import java.util.List;

/**
 * 通话记录
 */
public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordHolder> {
    private Context mContext;
    private List<RecordModel> mList;

    public RecordAdapter(Context mContext, List<RecordModel> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public RecordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .adapter_record, parent, false);
        return new RecordHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecordHolder holder, final int position) {
        RecordModel model = mList.get(position);
        if (!TextUtils.isEmpty(model.name)) {
            holder.tv_content.setText(model.name);
        } else if (!TextUtils.isEmpty(model.number)) {
            holder.tv_content.setText(model.number);
        } else {
            holder.tv_content.setText("未知号码");
        }
        holder.iv.setVisibility(model.type == 2 ? View.VISIBLE : View.INVISIBLE);
        if (model.type == 3) {
            holder.tv_content.setTextColor(mContext.getResources().getColor(R.color.ubt_dialog_btn_txt_color));
            holder.tv_count.setTextColor(mContext.getResources().getColor(R.color.ubt_dialog_btn_txt_color));
        } else {
            holder.tv_content.setTextColor(mContext.getResources().getColor(R.color.ubt_tips_txt_color));
            holder.tv_count.setTextColor(mContext.getResources().getColor(R.color.ubt_tips_txt_color));
        }
        holder.tv_count.setText("(" + model.count + ")");
        if (model.count > 1) {
            holder.tv_count.setVisibility(View.VISIBLE);
        } else {
            holder.tv_count.setVisibility(View.INVISIBLE);
        }
        holder.tv_date.setText(TimeUtils.format(TimeUtils.millis2Date(model.dateLong)));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class RecordHolder extends RecyclerView.ViewHolder {
        private TextView tv_content, tv_date, tv_count;
        private ImageView iv;

        public RecordHolder(View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_count = itemView.findViewById(R.id.tv_count);
            iv = itemView.findViewById(R.id.iv);
        }
    }
}
