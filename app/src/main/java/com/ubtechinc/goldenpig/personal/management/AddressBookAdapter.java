package com.ubtechinc.goldenpig.personal.management;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.model.AddressBookmodel;
import com.ubtechinc.goldenpig.view.swipe_menu.SwipeMenuLayout;

import java.util.List;

/**
 * Created by MQ on 2017/6/9.
 */

public class AddressBookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<AddressBookmodel> mList;

    public AddressBookAdapter(Context mContext, List<AddressBookmodel> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .adapter_addressbook, parent, false);
            return new AddressBookHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .adapter_no_more, parent, false);
            return new AddressBookHolder2(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (mList.get(position).type == 0) {
            AddressBookHolder aHolder = (AddressBookHolder) holder;
            AddressBookmodel model = mList.get(position);
            aHolder.tv_content.setText(model.name);
//            aHolder.tv_set.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (aHolder.swipe_menu.isMenuOpen()) {
//                        aHolder.swipe_menu.smoothToCloseMenu();
//                    }
//                    Toast.makeText(mContext, "编辑", Toast.LENGTH_SHORT).show();
//                }
//            });
//            aHolder.tv_delete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (aHolder.swipe_menu.isMenuOpen()) {
//                        aHolder.swipe_menu.smoothToCloseMenu();
//                    }
//                    mList.remove(position);
//                    try {
//                        if (mList.get(mList.size() - 1).type == 1) {
//                            mList.remove(mList.size() - 1);
//                        }
//                    } catch (Exception e) {
//                    }
//                    notifyDataSetChanged();
//                }
//            });
//            aHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (aHolder.swipe_menu.isMenuOpen()) {
//                        aHolder.swipe_menu.smoothToCloseMenu();
//                    } else {
//                        Toast.makeText(mContext, "这是第" + (position + 1) + "条数据", Toast.LENGTH_SHORT)
//                                .show();
//                    }
//                }
//            });
        } else {
            AddressBookHolder2 aHolder = (AddressBookHolder2) holder;
            aHolder.tv_content.setText(mContext.getString(R.string.contact_limit));
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

    public class AddressBookHolder extends RecyclerView.ViewHolder {
        private TextView tv_content;//, tv_set, tv_delete;
        //private SwipeMenuLayout swipe_menu;

        public AddressBookHolder(View itemView) {
            super(itemView);
//            swipe_menu = (SwipeMenuLayout) itemView.findViewById(R.id.swipe_menu);
//            tv_set = (TextView) itemView.findViewById(R.id.tv_set);
//            tv_delete = (TextView) itemView.findViewById(R.id.tv_delete);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }

    public class AddressBookHolder2 extends RecyclerView.ViewHolder {
        TextView tv_content;

        public AddressBookHolder2(View itemView) {
            super(itemView);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }
}
