package com.ubtechinc.goldenpig.main.fragment;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.ubtechinc.goldenpig.R;

import java.util.List;

/**
 * @auther :zzj
 * @email :zhijun.zhou@ubtrobot.com
 * @Description: :菜单pop
 * @time :2019/1/7 15:46
 * @change :
 * @changetime :2019/1/7 15:46
 */
public class MenuPopupView extends PopupWindow implements AdapterView.OnItemClickListener {

    private MenuPopupView mPopupWindow;

    private Activity context;

    private MenuCallback mMenuCallback = null;

    private List<String> mMenuData;

    public MenuPopupView(Activity context, List<String> menuData) {
        super(context);
        this.context = context;
        this.mMenuData = menuData;
        mPopupWindow = this;
        init();
    }

    private void init() {
        View contentView = LayoutInflater.from(context).inflate(R.layout.menu_pop_view, null);
        ListView listView = contentView.findViewById(R.id.menu_lv);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, mMenuData);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);

        mPopupWindow.setContentView(contentView);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x0000));
//        mPopupWindow.setAnimationStyle(R.style.pop_view);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setOnDismissListener(() -> {
            mMenuCallback.onDismiss();
            mMenuCallback = null;
        });

    }

    public MenuPopupView setCallback(MenuCallback callback) {
        mMenuCallback = callback;
        return mPopupWindow;
    }

    public MenuPopupView showAtBottom(View view) {
//        mPopupWindow.showAsDropDown(view, Math.abs((view.getWidth() - getWidth()) / 2), 2);
        mPopupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int width = mPopupWindow.getContentView().getMeasuredWidth();
        mPopupWindow.showAsDropDown(view, -width + view.getWidth() + (int) context.getResources().getDimension(R.dimen.dp_2), -20);
        return mPopupWindow;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mMenuCallback != null) {
            mMenuCallback.onClickMenu(position, view, mMenuData.get(position));
        }

    }

    public interface MenuCallback {
        void onDismiss();

        void onClickMenu(int position, View view, String value);
    }

}
