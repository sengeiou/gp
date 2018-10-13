package com.ubtechinc.goldenpig.common.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * @author ubt
 */
public abstract class CommonAdaper<T> extends BaseAdapter {

    protected Context context;
    private List<T> list;
    private int itemLayoutId;

    public CommonAdaper(Context context, List<T> list, int itemLayoutId) {
        this.context = context;
        this.list = list;
        this.itemLayoutId = itemLayoutId;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public T getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = getViewHolder(convertView, parent);
        convert(holder, getItem(position), position);
        return holder.getConvertview();
    }

    protected abstract void convert(ViewHolder holder, T item, int position);

    private ViewHolder getViewHolder(View convertView, ViewGroup parent) {
        return ViewHolder.get(context, convertView, parent, itemLayoutId);
    }

    public void update(List<T> items) {
        this.list = items;
        notifyDataSetChanged();
    }
}
