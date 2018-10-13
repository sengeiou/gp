package com.ubtechinc.goldenpig.common.adapter;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author ubt
 */
public class ViewHolder {

    private final SparseArray<View> views;
    private View convertview;

    protected ViewHolder(Context context, ViewGroup parent, int itemLayoutId) {
        this.views = new SparseArray<>();
        this.convertview = LayoutInflater.from(context).inflate(itemLayoutId, parent, false);
        convertview.setTag(this);
    }

    public static ViewHolder get(Context context, View convertview, ViewGroup parent, int layoutId) {
        if (convertview == null) {
            return new ViewHolder(context, parent, layoutId);
        }
        return (ViewHolder) convertview.getTag();
    }

    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = convertview.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertview() {
        return convertview;
    }

    public ViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    public ViewHolder setTextColor(int viewId, @ColorInt int color) {
        TextView tv = getView(viewId);
        tv.setTextColor(color);
        return this;
    }

    public ViewHolder setBackgroundColor(int viewId, @ColorInt int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    public ViewHolder setImageResource(int viewId, int drawableId) {
        ImageView iv = getView(viewId);
        iv.setImageResource(drawableId);
        return this;
    }

}
