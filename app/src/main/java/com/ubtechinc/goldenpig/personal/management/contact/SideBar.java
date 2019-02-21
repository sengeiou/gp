package com.ubtechinc.goldenpig.personal.management.contact;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.ubtechinc.goldenpig.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SideBar extends View {
    public static String[] INDEX_STRING = {"#", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    private List<String> letterList;
    private int choose = -1;
    private String chooseD = "";
    private Paint paint = new Paint();
    private TextView mTextDialog;

    public SideBar(Context context) {
        this(context, null);
    }

    public SideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setBackgroundColor(Color.parseColor("#00000000"));
        letterList = Arrays.asList(INDEX_STRING);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        //int singleHeight = height / letterList.size();
        int singleHeight = height / 27;
        Log.d("hdf", "singleHeight:" + singleHeight);
        for (int i = 0; i < letterList.size(); i++) {
            paint.setColor(Color.parseColor("#606060"));
            paint.setTypeface(Typeface.DEFAULT);
            paint.setAntiAlias(true);
            paint.setTextSize(singleHeight * 2 / 3);

//            if (i == choose) {
//                paint.setColor(Color.parseColor("#4F41FD"));
//                paint.setFakeBoldText(true);
//            }
            if (letterList.get(i).equals(chooseD)) {
                paint.setColor(Color.parseColor("#4F41FD"));
                paint.setFakeBoldText(true);
            }

            float xPos = width / 2 - paint.measureText(letterList.get(i)) / 2;
            float yPos = 10 + singleHeight * (27 - letterList.size()) / 2 + singleHeight * i + singleHeight / 2;
            Log.d("hdf", "yPos:" + yPos);
            canvas.drawText(letterList.get(i), xPos, yPos, paint);
            paint.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * letterList.size());
        switch (action) {
            case MotionEvent.ACTION_UP:
                setBackgroundColor(Color.parseColor("#00000000"));
                choose = -1;
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.GONE);
                }
                break;
            default:
                //setBackgroundResource(R.drawable.bg_sidebar);
                if (oldChoose != c) {
                    if (c >= 0 && c < letterList.size()) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(letterList.get(c));
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(letterList.get(c));
                            mTextDialog.setVisibility(View.VISIBLE);
                        }
                        choose = c;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    public void setIndexText(ArrayList<String> indexStrings) {
        this.letterList = indexStrings;
        invalidate();
    }

    /**
     * @param mTextDialog
     */
    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    /**
     * @param onTouchingLetterChangedListener
     */
    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }

    public void setTouchIndex(String choose) {
        if (!chooseD.equals(choose)) {
            this.chooseD = choose;
            invalidate();
        }
    }
}