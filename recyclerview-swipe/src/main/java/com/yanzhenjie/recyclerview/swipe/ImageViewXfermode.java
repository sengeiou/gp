package com.yanzhenjie.recyclerview.swipe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class ImageViewXfermode extends ImageView {
    Context context;

    public ImageViewXfermode(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ImageViewXfermode(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public ImageViewXfermode(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int width = getWidth();
        int height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int defaultWidth = getResources().getDisplayMetrics().widthPixels - getResources()
                .getDimensionPixelSize(R.dimen.dp_30);
        //xml里view的宽度是85dp
        int defaultdHeight = getMeasuredHeight();//getResources().getDimensionPixelSize(R.dimen.dp_115);

        //拿到黄色圆形的bitmap
        Bitmap bitcircle = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config
                .ARGB_8888);
        Canvas canvascicle = new Canvas(bitcircle);
        Paint paintcicle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintcicle.setColor(0xFFF5F8FB);
        canvascicle.drawRect(0, 0, defaultWidth, defaultdHeight, paintcicle);

        //拿到蓝色矩形的bitmap
        Bitmap bitrect = Bitmap.createBitmap(defaultWidth, defaultdHeight, Bitmap.Config
                .ARGB_8888);
        Canvas canvasrect = new Canvas(bitrect);
        Paint paintrect = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintrect.setColor(0xFF66AAFF);
        canvasrect.drawRoundRect(new RectF(0, 0, defaultWidth, defaultdHeight), getResources()
                .getDimensionPixelSize(R.dimen.dp_20), getResources()
                .getDimensionPixelSize(R.dimen.dp_20), paintrect);
//        canvasrect.drawRoundRect(0, 0, defaultWidth, defaultdHeight, getResources()
//                .getDimensionPixelSize(R.dimen.dp_25), getResources()
//                .getDimensionPixelSize(R.dimen.dp_25), paintrect);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.XOR);

        //采用saveLayer，让后续canvas的绘制在自动创建的bitmap上
        int cnt = canvas.saveLayer(0, 0, defaultWidth, defaultdHeight, null, Canvas
                .ALL_SAVE_FLAG);
        //先画圆形，圆形是dest
        canvas.drawBitmap(bitcircle, 0, 0, paint);
        paint.setXfermode(xfermode);
        //后画矩形，矩形是src
        canvas.drawBitmap(bitrect, 0, 0, paint);
        paint.setXfermode(null);
        canvas.restoreToCount(cnt);
    }

    private int dip2px(float dip) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }
}