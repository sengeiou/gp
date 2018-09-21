package com.ubtechinc.commlib.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

//条目滑动效果
public class SlideLayout extends LinearLayout {
    private ViewDragHelper mDragHelper;
    private View contentView;
    private View actionView;
    private int dragDistance;
    private final double AUTO_OPEN_SPEED_LIMIT = 800.0;
    private int draggedX;
    public SlideLayout(Context context) {
        super(context);
        init();
    }
    public SlideLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public SlideLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    //初始化
    public void init (){
        mDragHelper = ViewDragHelper.create(this, new DragHelperCallback());
    }
    @Override
    public boolean callOnClick() {
        return super.callOnClick();
    }
    /*当你触摸屏幕，移动的时候，就会回调这个方法。
    它会返回两个参数。第一个参数，就是你触摸的那个控件。
    第二个就是ＩＤ。
    返回值又代表什么呢？返回ture,就是代笔允许拖动这个控件。
    返回false就代表不允许拖动这个控件.。这里我只允许拖动主控件。*/
    //把容器的事件处理委托给ViewDragHelper对象
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mDragHelper.shouldInterceptTouchEvent(event)) {
            return true;
        }
        return super.onInterceptTouchEvent(event);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }
    @Override
    protected void onFinishInflate() {
        contentView = getChildAt(0);
        actionView = getChildAt(1);
        actionView.setVisibility(GONE);
    }
    //设置拖动的距离为actionView的宽度
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        dragDistance = actionView.getMeasuredWidth();
        //System.out.println("rightTop"+actionView.getTop());
    }
    private class DragHelperCallback extends ViewDragHelper.Callback {
        //用来确定contentView和actionView是可以拖动的
        @Override
        public boolean tryCaptureView(View view, int i) {
            return view == contentView || view == actionView;
        }
        //被拖动的view位置改变的时候调用，如果被拖动的view是contentView，
        // 我们需要在这里更新actionView的位置
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            draggedX = left;
            if (changedView == contentView) {
                actionView.offsetLeftAndRight(dx);
            } else {
                contentView.offsetLeftAndRight(dx);
            }
            //actionView 是否可见
            //0  --------  VISIBLE  可见
            //4  --------  INVISIBLE  不可见但是占用布局空间
            //8  --------  GONE  不可见也不占用布局空间
            if (actionView.getVisibility() == View.GONE) {
                actionView.setVisibility(View.VISIBLE);
            }
            if (left==25)
            {
                actionView.setVisibility(View.GONE);
            }
            invalidate(); //刷新View
        }
        //用来限制view在x轴上拖动
        //@Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == contentView) {
                final int leftBound = getPaddingLeft();
                final int minLeftBound = -leftBound - dragDistance;
                final int newLeft = Math.min(Math.max(minLeftBound, left), 25);
                //System.out.println("content "+newLeft);
                return newLeft;
            } else {
                //getMeasuredWidth()获取全部长度 包括隐藏的
                final int minLeftBound = getPaddingLeft() + contentView.getMeasuredWidth() - dragDistance;
                final int maxLeftBound = getPaddingLeft() + contentView.getMeasuredWidth() + getPaddingRight();
                final int newLeft = Math.min(Math.max(left, minLeftBound), maxLeftBound);
                System.out.println("action "+newLeft);
                return newLeft;
            }
        }
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            //System.out.println("top "+top);
            if(top!=25)
            {
                top=25;
            }
            return top;
        }
        //用来限制view可以拖动的范围
        //@Override
        public int getViewHorizontalDragRange(View child) {
            return dragDistance;
        }
        @Override
        public int getViewVerticalDragRange(View child) {
            return 0;
        }
        //根据滑动手势的速度以及滑动的距离来确定是否显示actionView。
        // smoothSlideViewTo方法用来在滑动手势之后实现惯性滑动效果
        //@Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            boolean settleToOpen = false;
            if (xvel > AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = false;
            } else if (xvel < -AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = true;
            } else if (draggedX <= -dragDistance / 2) {
                settleToOpen = true;
            } else if (draggedX > -dragDistance / 2) {
                settleToOpen = false;
            }
            final int settleDestX = settleToOpen ? -dragDistance : 0;
            mDragHelper.smoothSlideViewTo(contentView, settleDestX, 0);
            ViewCompat.postInvalidateOnAnimation(SlideLayout.this);
        }
    }
}
