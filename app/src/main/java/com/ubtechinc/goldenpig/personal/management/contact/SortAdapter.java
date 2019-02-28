package com.ubtechinc.goldenpig.personal.management.contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.ubt.imlibv2.bean.MyContact;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.utils.CommendUtil;

import java.util.List;


public class SortAdapter extends BaseAdapter implements SectionIndexer {
    private List<MyContact> list = null;
    private Context mContext;

    public SortAdapter(Context mContext, List<MyContact> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public void updateListView(List<MyContact> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final MyContact mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_contact, null);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.tv_catagory);
            viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.tv_phone = (TextView) view.findViewById(R.id.tv_phone);
            viewHolder.iv_select = view.findViewById(R.id.iv_select);
            viewHolder.cur_line = view.findViewById(R.id.cur_line);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (list.get(position).select) {
            viewHolder.iv_select.setImageResource(R.drawable.ic_choose2);
        } else {
            viewHolder.iv_select.setImageResource(R.drawable.ic_choose1);
        }
        int section = getSectionForPosition(position);
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.sortLetter);
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        try {
            if (list.get(position).sortLetter.equals(list.get(position + 1).sortLetter)) {
                viewHolder.cur_line.setVisibility(View.VISIBLE);
            } else {
                viewHolder.cur_line.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            viewHolder.cur_line.setVisibility(View.GONE);
        }
        viewHolder.tv_phone.setText(CommendUtil.showPhone(mContent.mobile));
        viewHolder.tv_name.setText(this.list.get(position).name);
        return view;

    }


    final static class ViewHolder {
        TextView tvLetter;
        TextView tv_name, tv_phone;
        ImageView iv_select;
        View cur_line;

    }

    public int getSectionForPosition(int position) {
        return list.get(position).sortLetter.charAt(0);
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).sortLetter;
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}