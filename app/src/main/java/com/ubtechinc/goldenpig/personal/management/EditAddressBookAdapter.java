package com.ubtechinc.goldenpig.personal.management;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.model.AddressBookmodel;

import java.util.ArrayList;

/**
 * Created by MQ on 2017/6/9.
 */

public class EditAddressBookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<AddressBookmodel> mList;
    private int hasCard = 0;

    public EditAddressBookAdapter(Context mContext, ArrayList<AddressBookmodel> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .adapter_edit_addressbook, parent, false);
            return new AddressBookHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .adapter_edit_addressbook_header, parent, false);
            return new AddressBookHolder3(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (mList.get(position).type == 0) {
            AddressBookHolder aHolder = (AddressBookHolder) holder;
            AddressBookmodel model = mList.get(position);
            aHolder.tv_content.setText(model.name);
            aHolder.tv_number.setText(model.phone);
            if (mList.size() - 1 == position) {
                aHolder.view_curline.setVisibility(View.GONE);
                aHolder.view_curline_bottom.setVisibility(View.VISIBLE);
            } else {
                aHolder.view_curline.setVisibility(View.VISIBLE);
                aHolder.view_curline_bottom.setVisibility(View.GONE);
            }
        } else {
            AddressBookHolder3 aHolder = (AddressBookHolder3) holder;
            if (hasCard == 0) {
                aHolder.tv_has_card.setVisibility(View.VISIBLE);
            } else {
                aHolder.tv_has_card.setVisibility(View.GONE);
            }
            aHolder.ll_select_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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

    public class AddressBookHolder extends RecyclerView.ViewHolder {
        private TextView tv_content, tv_number;//, tv_set, tv_delete;
        private View view_curline, view_curline_bottom;

        public AddressBookHolder(View itemView) {
            super(itemView);
//            swipe_menu = (SwipeMenuLayout) itemView.findViewById(R.id.swipe_menu);
            view_curline_bottom = itemView.findViewById(R.id.view_curline_bottom);
            view_curline = itemView.findViewById(R.id.view_curline);
            tv_number = (TextView) itemView.findViewById(R.id.tv_number);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }

    public class AddressBookHolder3 extends RecyclerView.ViewHolder {
        TextView tv_has_card;
        LinearLayout ll_select_all;

        public AddressBookHolder3(View itemView) {
            super(itemView);
            tv_has_card = itemView.findViewById(R.id.tv_has_card);
            ll_select_all = itemView.findViewById(R.id.ll_select_all);
        }
    }
}
