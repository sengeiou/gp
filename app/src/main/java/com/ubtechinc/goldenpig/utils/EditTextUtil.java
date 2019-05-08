package com.ubtechinc.goldenpig.utils;

import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.EditText;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.ToastUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditTextUtil {
    private static String TAG = "EditTextUtil";
    /**
     * 干扰词
     */
    public static String[] noises = {"八戒八戒", "叮当叮当"};
    public static final int INTERLOCUTION = 0;
    public static final int CREATE = 1;

    public static Boolean checkNoise(int type, String msg, EditText et) {
        try {
            String tagNoise = "";
            int tagPosition = 0;
            for (String noise : noises) {
                LogUtils.d("hdf", "checkNoise:" + noise);
                if (msg.contains(noise)) {
                    if (TextUtils.isEmpty(tagNoise)) {//第一次匹配到直接赋值
                        tagNoise = noise;
                        String[] sourceStrArray = msg.split(noise, 2);
                        if (TextUtils.isEmpty(sourceStrArray[0])) {
                            tagPosition = 0;
                        } else {
                            tagPosition = sourceStrArray[0].length();
                        }
                    } else {//第二次匹配到直接赋值作比较
                        String[] sourceStrArray = msg.split(noise, 2);
                        if (TextUtils.isEmpty(sourceStrArray[0])) {
                            tagPosition = 0;
                            tagNoise = noise;
                        } else if (tagPosition > sourceStrArray[0].length()) {
                            tagPosition = sourceStrArray[0].length();
                            tagNoise = noise;
                        }
                    }
                }
            }
            if (TextUtils.isEmpty(tagNoise)) {
                return false;
            }
            switch (type) {
                case INTERLOCUTION:
                    ToastUtils.showShortToast("保存失败，与唤醒词冲突");
                    break;
                case CREATE:
                    ToastUtils.showShortToast("提交失败，提交内容与唤醒词冲突");
                    break;
                default:
                    ToastUtils.showShortToast("提交失败，提交内容与唤醒词冲突");
                    break;
            }
            String[] sourceStrArray = msg.split(tagNoise, 2);
            int po = et.getSelectionStart();
            StringBuilder sb = new StringBuilder();
            sb.append(sourceStrArray[0]);
            sb.append("<font color='#FE4C4C'>");
            sb.append(tagNoise);
            sb.append("</font>");
            sb.append(sourceStrArray[1]);
            et.setText(Html.fromHtml(sb.toString()));
            et.setSelection(po);
            return true;
        } catch (Exception e) {
            LogUtils.d("hdf", "Exception:" + e);
        }
        return false;
    }

    /**
     * 判定输入汉字
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    //过滤只可输入中文，count为最大长度，-1为不限长
    public static InputFilter[] getChinseInpF(int count) {
        InputFilter[] filterArray = new InputFilter[1];
        if (count > 0) {
            filterArray = new InputFilter[2];
        }
        filterArray[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!isChinese(source.charAt(i))) {
                        return "";
                    } else {
                        if ((source.charAt(i) >= 0x4e00) && (source.charAt(i) <= 0x9fbb)) {

                        } else {
                            return "";
                        }
                    }
                }
                return source;
            }
        };
        if (count > 0) {
            filterArray[1] = new InputFilter.LengthFilter(count);
        }
        return filterArray;
    }

    public static InputFilter[] getNotSupportInputFi(int count) {
        InputFilter[] filterArray = new InputFilter[1];
        if (count > 0) {
            filterArray = new InputFilter[2];
        }
        filterArray[0] = new InputFilter() {

            Pattern emoji = Pattern.compile(EMOJI,
                    Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                LogUtils.d("hdf", "source:" + source.toString());
                Matcher emojiMatcher = emoji.matcher(source);
                if (emojiMatcher.find() || source.toString().contains(" ") || source.toString().contentEquals("\n")) {
                    return "";
                }
                return source;
            }
        };
        return filterArray;
    }

    private static String EMOJI = "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]";
    private static String LETTER = "a-zA-Z";
    //private static String LARGE_LETTER = "A-Z";
    private static String NUMBER = "0-9";
    //中英文标点
    private static String PUNCTUATION = "\\p{Han}\\p{P}";//
    //汉字
    private static String CHINESE = "\\u4e00-\\u9fa5";

    /**
     * 0为只能输入汉字，数字，大小写字母，标点
     */
    public static InputFilter[] getOnlyInputType(int type, int count) {
        InputFilter[] filterArray = new InputFilter[1];
        if (count > 0) {
            filterArray = new InputFilter[2];
        }
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case 0:
                sb.append("[^");
                sb.append(LETTER);
                sb.append(NUMBER);
                sb.append(PUNCTUATION);
                sb.append(CHINESE);
                sb.append("]");
                break;
            case 1:
                sb.append("[^");
                sb.append(CHINESE);
                sb.append("]");
                break;
            default:
                sb.append("[^");
                sb.append(LETTER);
                sb.append(NUMBER);
                sb.append(PUNCTUATION);
                sb.append(CHINESE);
                sb.append("]");
                break;
        }
        LogUtils.d(TAG, sb.toString());
        filterArray[0] = new InputFilter() {

            Pattern pattern = Pattern.compile(sb.toString());

            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                Matcher emojiMatcher = pattern.matcher(source);
                if (!emojiMatcher.find()) {
                    return null;
                } else {
                    return "";
                }
                //return source;
            }
        };
        if (count > 0) {
            filterArray[1] = new InputFilter.LengthFilter(count);
        }
        return filterArray;
    }


}
