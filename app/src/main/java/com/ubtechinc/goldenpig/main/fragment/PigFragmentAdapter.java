package com.ubtechinc.goldenpig.main.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubtechinc.goldenpig.R;

import java.util.List;

public class PigFragmentAdapter extends RecyclerView.Adapter<PigFragmentAdapter.ViewHodler> {

    private final String TAG = "PigFragmentAdapter";

    private List<String> list;
    private Context context;

    public PigFragmentAdapter(Context context, List<String> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_skill_list, parent, false);
        return new ViewHodler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHodler holder, int position) {
        holder.tvItem.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHodler extends RecyclerView.ViewHolder {

        TextView tvItem;

        public ViewHodler(View itemView) {
            super(itemView);
            tvItem = (TextView) itemView.findViewById(R.id.tv_item_skill);
        }
    }
}
