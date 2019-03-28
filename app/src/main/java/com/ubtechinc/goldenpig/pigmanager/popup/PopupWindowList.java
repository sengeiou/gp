package com.ubtechinc.goldenpig.pigmanager.popup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.view.Divider;
import com.ubtechinc.goldenpig.view.RecyclerItemClickListener;

import java.util.List;

/**
 * Created by GT on 2018/1/22.
 */

public class PopupWindowList {

    private Context mContext;
    private PopupWindow mPopupWindow;
    //the view where PopupWindow lie in
    private View mAnchorView;
    //ListView item data
    private List<String> mItemData;
    //the animation for PopupWindow
    private int mPopAnimStyle;
    //the PopupWindow width
    private int mPopupWindowWidth;
    //the PopupWindow height
    private int mPopupWindowHeight;
    private RecyclerItemClickListener mItemClickListener;
    private boolean mModal;
    private DissListener dissListener;

    public void setDissListener(DissListener dissListener) {
        this.dissListener = dissListener;
    }

    public interface DissListener {
        void onDissListener();
    }

    public PopupWindowList(Context mContext) {
        if (mContext == null) {
            throw new IllegalArgumentException("context can not be null");
        }
        this.mContext = mContext;
        setHeightWidth();
    }

    public void setAnchorView(@Nullable View anchor) {
        mAnchorView = anchor;
    }

    public void setItemData(List<String> mItemData) {
        this.mItemData = mItemData;
    }

    public void setPopAnimStyle(int mPopAnimStyle) {
        this.mPopAnimStyle = mPopAnimStyle;
    }

    public void setPopupWindowWidth(int mPopupWindowWidth) {
        this.mPopupWindowWidth = mPopupWindowWidth;
    }

    public void setPopupWindowHeight(int mPopupWindowHeight) {
        this.mPopupWindowHeight = mPopupWindowHeight;
    }

    /**
     * Set whether this window should be modal when shown.
     * <p>
     * <p>If a popup window is modal, it will receive all touch and key input.
     * If the user touches outside the popup window's content area the popup window
     * will be dismissed.
     *
     * @param modal {@code true} if the popup window should be modal, {@code false} otherwise.
     */
    public void setModal(boolean modal) {
        mModal = modal;
    }

    public boolean isShowing() {
        return mPopupWindow != null && mPopupWindow.isShowing();
    }

    public void hide() {
        if (isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * Sets a listener to receive events when a list item is clicked.
     *
     * @param clickListener Listener to register
     * @see ListView#setOnItemClickListener(AdapterView.OnItemClickListener)
     */
    public void setOnItemClickListener(@Nullable RecyclerItemClickListener clickListener) {
        mItemClickListener = clickListener;
        if (mPopView != null) {
            //mPopView.setOnItemClickListener(mItemClickListener);
            mPopView.addOnItemTouchListener(mItemClickListener);
        }
    }

    private RecyclerView mPopView;

    public void show() {
        if (mAnchorView == null) {
            throw new IllegalArgumentException("PopupWindow show location view can  not be null");
        }
        if (mItemData == null) {
            throw new IllegalArgumentException("please fill ListView Data");
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_popupwindow_list, null);

        //mPopView = new RecyclerView(mContext);
        mPopView = view.findViewById(R.id.recycler);
        mPopView.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.white));
        mPopView.setVerticalScrollBarEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPopView.setLayoutManager(linearLayoutManager);
        mPopView.setHasFixedSize(true);
        Divider divider = new Divider(new ColorDrawable(mContext.getResources().getColor(R.color
                .ubt_wifi_list_divider)), OrientationHelper.VERTICAL);
        divider.setHeight((int) mContext.getResources().getDimension(R.dimen.dp_1));
        mPopView.addItemDecoration(divider);
        mPopView.setAdapter(new PopupWindowAdapter(mContext, mItemData));
        if (mItemClickListener != null) {
            mPopView.addOnItemTouchListener(mItemClickListener);
        }
        mPopView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        if (mPopupWindowWidth == 0) {
            mPopupWindowWidth = mDeviceWidth / 3;
        }
        if (mPopupWindowHeight == 0) {
            mPopupWindowHeight = mPopView.getMeasuredHeight();
            if (mPopupWindowHeight > mDeviceHeight / 2) {
                mPopupWindowHeight = mDeviceHeight / 2;
            }
        }
        //mPopupWindow = new PopupWindow(view, mPopupWindowWidth, mPopupWindowHeight);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (mPopAnimStyle != 0) {
            mPopupWindow.setAnimationStyle(mPopAnimStyle);
        }
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(mModal);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), (Bitmap) null));
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (dissListener != null) {
                    dissListener.onDissListener();
                }
            }
        });

        Rect location = locateView(mAnchorView);
        if (location != null) {
            LogUtils.d("hdf", "mAnchorView.getWidth():" + mAnchorView.getWidth() + "," + mContext.getResources()
                    .getDimensionPixelSize(R.dimen.dp_30));
            int x = mAnchorView.getWidth() - mContext.getResources().getDimensionPixelSize(R.dimen.dp_115);
            if (mItemData.size() > 1) {
                x = mAnchorView.getWidth() - mContext.getResources().getDimensionPixelSize(R.dimen.dp_138) + location
                        .left * 2;
            }
            //view中心点X坐标
//            int xMiddle = location.left + mAnchorView.getWidth() / 2;
//            if (xMiddle > mDeviceWidth / 2) {
//                //在右边
//                x = xMiddle - mPopupWindowWidth;
//            } else {
//                x = xMiddle;
//            }

            int y;
            //view中心点Y坐标
            int yMiddle = location.top + mAnchorView.getHeight() / 2;
            if (yMiddle > mDeviceHeight / 2) {
                //在下方
                y = yMiddle - mPopupWindowHeight - mAnchorView.getHeight() / 2 + mContext.getResources()
                        .getDimensionPixelSize(R.dimen.dp_10);
            } else {
                //在上方
                y = yMiddle + mAnchorView.getHeight() / 2 - mContext.getResources()
                        .getDimensionPixelSize(R.dimen.dp_14);
            }
            mPopupWindow.showAtLocation(mAnchorView, Gravity.NO_GRAVITY, x, y);
        }
    }

    public Rect locateView(View v) {
        if (v == null) return null;
        int[] loc_int = new int[2];
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }


    private int mDeviceWidth, mDeviceHeight;

    public void setHeightWidth() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        //API 13才允许使用新方法
        Point outSize = new Point();
        wm.getDefaultDisplay().getSize(outSize);
        if (outSize.x != 0) {
            mDeviceWidth = outSize.x;
        }
        if (outSize.y != 0) {
            mDeviceHeight = outSize.y;
        }
    }

}
