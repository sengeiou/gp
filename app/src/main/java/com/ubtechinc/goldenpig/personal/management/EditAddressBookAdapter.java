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
    private EditABListener mEditABListener;

    public interface EditABListener {
        void clearAll();

        void delete();

        void nothing();
    }

    public EditAddressBookAdapter(Context mContext, ArrayList<AddressBookmodel> mList, EditABListener mEditABListener) {
        this.mContext = mContext;
        this.mList = mList;
        this.mEditABListener = mEditABListener;
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
            aHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mList.get(position).select = !mList.get(position).select;
                    Boolean check = false;
                    for (int i = 1; i < mList.size(); i++) {
                        if (!mList.get(i).select) {
                            mList.get(0).selectAll = false;
                            break;
                        }
                        if (i == mList.size() - 1) {
                            mList.get(0).selectAll = true;
                            mEditABListener.clearAll();
                            check = true;
                        }
                    }
                    notifyDataSetChanged();
                    if (check) {
                        return;
                    }
                    for (int i = 1; i < mList.size(); i++) {
                        if (mList.get(i).select) {
                            mEditABListener.delete();
                            break;
                        }
                        if (i == mList.size() - 1) {
                            mEditABListener.nothing();
                        }
                    }
                }
            });
            if (mList.get(position).select) {
                aHolder.iv_select.setImageResource(R.drawable.ic_choose2);
            } else {
                aHolder.iv_select.setImageResource(R.drawable.ic_choose1);
            }
        } else {
            AddressBookHolder3 aHolder = (AddressBookHolder3) holder;
            if (!mList.get(position).card) {
                aHolder.tv_has_card.setVisibility(View.VISIBLE);
            } else {
                aHolder.tv_has_card.setVisibility(View.GONE);
            }
            if (mList.get(position).selectAll) {
                aHolder.iv_select.setImageResource(R.drawable.ic_choose2);
                aHolder.tv_select_all.setTextColor(mContext.getResources().getColor(R.color.ubt_main_item_tab_color));
            } else {
                aHolder.iv_select.setImageResource(R.drawable.ic_choose1);
                aHolder.tv_select_all.setTextColor(mContext.getResources().getColor(R.color.empty_color));
            }
            aHolder.ll_select_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mList.get(position).selectAll) {
                        mList.get(position).selectAll = false;
                        mEditABListener.nothing();
                        for (int i = 0; i < mList.size(); i++) {
                            mList.get(i).select = false;
                        }
                    } else {
                        mList.get(position).selectAll = true;
                        mEditABListener.clearAll();
                        for (int i = 0; i < mList.size(); i++) {
                            mList.get(i).select = true;
                        }
                    }
                    notifyDataSetChanged();
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
        private ImageView iv_select;

        public AddressBookHolder(View itemView) {
            super(itemView);
//            swipe_menu = (SwipeMenuLayout) itemView.findViewById(R.id.swipe_menu);
            view_curline_bottom = itemView.findViewById(R.id.view_curline_bottom);
            view_curline = itemView.findViewById(R.id.view_curline);
            iv_select = itemView.findViewById(R.id.iv_select);
            tv_number = itemView.findViewById(R.id.tv_number);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }

    public class AddressBookHolder3 extends RecyclerView.ViewHolder {
        TextView tv_has_card;
        LinearLayout ll_select_all;
        ImageView iv_select;
        TextView tv_select_all;

        public AddressBookHolder3(View itemView) {
            super(itemView);
            tv_has_card = itemView.findViewById(R.id.tv_has_card);
            ll_select_all = itemView.findViewById(R.id.ll_select_all);
            iv_select = itemView.findViewById(R.id.iv_select);
            tv_select_all = itemView.findViewById(R.id.tv_select_all);
        }
    }
}
