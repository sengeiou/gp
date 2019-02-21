package com.ubtechinc.goldenpig.comm.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.facebook.stetho.common.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 解决了TextView滑动问题
 * @Author: zhijunzhou
 * @CreateDate: 2018/12/25 17:23
 */
public class ScrollTextView extends View {

    public interface OnTextChangedListener {
        void onTextChanged(String text);
    }

    private class TextStyle {
        int alpha;
        float y;
        String text;

        TextStyle(String text, int alpha, float y) {
            this.text = text;
            this.alpha = alpha;
            this.y = y;
        }
    }

    public static final int SCROLL_UP = 0, SCROLL_DOWN = 1;

    private List<TextStyle> textRows = new ArrayList<>();

    private OnTextChangedListener onTextChangedListener;

    private Paint textPaint;

    /**
     * 标题内容
     */
    private String title;

    /**
     * 是否是标题模式
     */
    private boolean setTitle;

    /**
     * 当前的文本内容是否正在滚动
     */
    private boolean scrolling;

    /**
     * 文字滚动方向，支持上下滚动
     */
    private int scrollDirect;

    /**
     * 每行的最大宽度
     */
    private float lineMaxWidth;

    /**
     * 最大行数
     */
    private int maxLineCount;

    /**
     * 每行的高度，此值是根据文字的大小自动去测量出来的
     */
    private float lineHeight;

    public ScrollTextView(Context context) {
        super(context);
        init();
    }

    public ScrollTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        textPaint = createTextPaint(255);
        lineMaxWidth = textPaint.measureText("一二三四五六七八九十"); // 默认一行最大长度为10个汉字的长度
        maxLineCount = 4;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float x;
        float y = fontMetrics.bottom - fontMetrics.top;
        lineHeight = y;
        if (setTitle) {
            x = getWidth() / 2 - textPaint.measureText(title) / 2;
            canvas.drawText(title, x, y, textPaint);
        } else {
            synchronized (this) {
                if (textRows.isEmpty()) {
                    return;
                }
                scrolling = true;
                x = getWidth() / 2 - textPaint.measureText(textRows.get(0).text) / 2;
                if (textRows.size() <= 2) {
                    for (int index = 0; index < 2 && index < textRows.size(); index++) {
                        TextStyle textStyle = textRows.get(index);
                        textPaint.setAlpha(textStyle.alpha);
                        canvas.drawText(textStyle.text, x, textStyle.y, textPaint);
                    }
                } else {
                    boolean draw = false;
                    for (int row = 0; row < textRows.size(); row++) {
                        TextStyle textStyle = textRows.get(row);
                        textPaint.setAlpha(textStyle.alpha);
                        canvas.drawText(textStyle.text, x, textStyle.y, textPaint);
                        if (textStyle.alpha < 255) {
                            textStyle.alpha += 51;
                            draw = true;
                        }
                        if (textRows.size() > 2) {
                            if (scrollDirect == SCROLL_UP) {
                                // 此处的9.0f的值是由255/51得来的，要保证文字透明度的变化速度和文字滚动的速度要保持一致
                                // 否则可能造成透明度已经变化完了，文字还在滚动或者透明度还没变化完成，但是文字已经不滚动了
                                textStyle.y = textStyle.y - (lineHeight / 9.0f);
                            } else {
                                if (textStyle.y < lineHeight + lineHeight * row) {
                                    textStyle.y = textStyle.y + (lineHeight / 9.0f);
                                    draw = true;
                                }
                            }
                        }
                    }
                    if (draw) {
                        postInvalidateDelayed(50);
                    } else {
                        scrolling = false;
                    }
                }
            }
        }
    }

    private Paint createTextPaint(int a) {
        Paint textPaint = new Paint();
        textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getContext().getResources().getDisplayMetrics()));
//        textPaint.setColor(getContext().getColor(R.color.color_999999));
        textPaint.setAlpha(a);
        return textPaint;
    }

    public void resetText() {
        synchronized (this) {
            textRows.clear();
        }
    }

    public void formatText() {
        scrollDirect = SCROLL_DOWN;
        StringBuffer stringBuffer = new StringBuffer("\n");
        synchronized (this) {
            for (int i = 0; i < textRows.size(); i++) {
                TextStyle textStyle = textRows.get(i);
                if (textStyle != null) {
                    textStyle.alpha = 255;
//                    textStyle.y = 45 + 45 * i;
                    stringBuffer.append(textStyle.text + "\n");
                }
            }
        }
        postInvalidateDelayed(100);
        LogUtil.i("formatText:" + stringBuffer.toString());
    }

    public void appendText(String text) {
        setTitle = false;
        scrollDirect = SCROLL_UP;
        synchronized (this) {
            if (textRows.size() > maxLineCount) {
                return;
            }
            if (text.length() <= 10) {
                if (textRows.isEmpty()) {
                    textRows.add(new TextStyle(text, 255, lineHeight + lineHeight * textRows.size()));
                } else {
                    TextStyle pre = textRows.get(textRows.size() - 1);
                    textRows.set(textRows.size() - 1, new TextStyle(text, pre.alpha, pre.y));
                }
            } else {
                List<String> list = new ArrayList<>();
                StringBuffer stringBuffer = new StringBuffer();
                float curWidth = 0;
                for (int index = 0; index < text.length(); index++) {
                    char c = text.charAt(index);
                    curWidth += textPaint.measureText(String.valueOf(c));
                    if (curWidth <= lineMaxWidth) {
                        stringBuffer.append(c);
                    } else {
                        if (list.size() < maxLineCount) {
                            list.add(stringBuffer.toString());
                            curWidth = 0;
                            index--;
                            stringBuffer.delete(0, stringBuffer.length());
                        } else {
                            break;
                        }
                    }
                }
                if (!TextUtils.isEmpty(stringBuffer.toString()) && list.size() < maxLineCount) {
                    list.add(stringBuffer.toString());
                }
                if (textRows.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        if (i < 2) {
                            textRows.add(new TextStyle(list.get(i), 255, lineHeight + lineHeight * i));
                        } else {
                            textRows.add(new TextStyle(list.get(i), 0, lineHeight + lineHeight * i));
                        }
                    }
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        if (textRows.size() > i) {
                            TextStyle pre = textRows.get(i);
                            textRows.set(i, new TextStyle(list.get(i), pre.alpha, pre.y));
                        } else {
                            TextStyle pre = textRows.get(textRows.size() - 1);
                            if (i < 2) {
                                textRows.add(new TextStyle(list.get(i), 255, pre.y + lineHeight));
                            } else {
                                textRows.add(new TextStyle(list.get(i), 0, pre.y + lineHeight));
                            }
                        }
                    }
                }
            }
            if (!scrolling) {
                invalidate();
            }
        }
        textChanged();
    }

    public void setTextColor(int corlor) {
        textPaint.setColor(corlor);
        invalidate();
    }

    public void setTitle(int resId) {
        this.title = getContext().getString(resId);
        setTitle = true;
        invalidate();
    }

    public void setOnTextChangedListener(OnTextChangedListener onTextChangedListener) {
        this.onTextChangedListener = onTextChangedListener;
    }

    private void textChanged() {
        if (onTextChangedListener != null) {
            onTextChangedListener.onTextChanged(getText());
        }
    }

    public String getText() {
        StringBuffer allText = new StringBuffer();
        for (TextStyle textStyle : textRows) {
            allText.append(textStyle.text);
        }
        return allText.toString();
    }

    public int getScrollDirect() {
        return scrollDirect;
    }

    public void setScrollDirect(int scrollDirect) {
        this.scrollDirect = scrollDirect;
    }

    public float getLineMaxWidth() {
        return lineMaxWidth;
    }

    public void setLineMaxWidth(float lineMaxWidth) {
        this.lineMaxWidth = lineMaxWidth;
    }

    public int getMaxLineCount() {
        return maxLineCount;
    }

    public void setMaxLineCount(int maxLineCount) {
        this.maxLineCount = maxLineCount;
    }

    public boolean isScrolling() {
        return scrolling;
    }

}
