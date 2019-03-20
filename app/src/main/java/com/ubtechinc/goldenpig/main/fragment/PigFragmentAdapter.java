package com.ubtechinc.goldenpig.main.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.main.FunctionModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PigFragmentAdapter extends RecyclerView.Adapter<PigFragmentAdapter.ViewHodler> {

    private final String TAG = "PigFragmentAdapter";

    private List<String> list;
    private List<String> allList;
    private int lastPosition = 0;

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

    public synchronized void updateData(FunctionModel mFunctionModel) {
        String[] statements = mFunctionModel.statement.statements;
        if (statements != null) {

            if (allList != null) {
                allList.clear();
            } else {
                allList = new ArrayList<>();
            }

            allList.addAll(new ArrayList<>(Arrays.asList(statements)));
            update();

//            notifyDataSetChanged();
        }
    }

    private void update(){

        UbtLogger.d(TAG, "update");

        if (list != null) {
            list.clear();
        } else {
            list = new ArrayList<>();
        }

        for (int i = lastPosition; list.size() < 6; i++) {
            list.add(allList.get(i % allList.size()));
        }
        lastPosition = (lastPosition + 6) % allList.size();

        notifyDataSetChanged();
    }

    class ViewHodler extends RecyclerView.ViewHolder {
        TextView tvItem;

        public ViewHodler(View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tv_item_skill);
        }
    }
}
