package com.ubtechinc.goldenpig.personal.remind;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tencent.ai.tvs.comm.CommOpInfo;
import com.tencent.ai.tvs.env.ELoginPlatform;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.TimeUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.model.RemindModel;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.CommendUtil;
import com.ubtechinc.goldenpig.utils.PigUtils;
import com.ubtechinc.tvlloginlib.TVSManager;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ubtech.utilcode.utils.TimeUtils.DATE_FORMAT_DATE;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.ADD_REMIND_REPEAT_SUCCESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.ADD_REMIND_SUCCESS;

public class AddRemindActivity extends BaseNewActivity {
    @BindView(R.id.loopView_date)
    LoopView loopView_date;
    @BindView(R.id.loopView_am)
    LoopView loopView_am;
    @BindView(R.id.loopView_hour)
    LoopView loopView_hour;
    @BindView(R.id.loopView_minute)
    LoopView loopView_minute;
    @BindView(R.id.ed_msg)
    EditText ed_msg;
    @BindView(R.id.tv_cycle)
    TextView tv_cycle;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv_right)
    TextView tv_right;
    private List<String> dateList;
    private List<String> dateList2;
    private List<String> amList;
    private List<String> hourList;
    private List<String> minList;
    private int repeatType = 1;
    Date today = new Date();
    private RemindModel model;
    int maxchineseLength = 20;

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_remind;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = getIntent().getParcelableExtra("item");
        initData();
        loopView_date.setItems(dateList);
        loopView_date.setItemsVisibleCount(0);
        loopView_date.setTextSize(18);
        loopView_date.setNotLoop();
        loopView_am.setItems(amList);
        loopView_am.setItemsVisibleCount(0);
        loopView_am.setTextSize(18);
        loopView_am.setNotLoop();
        loopView_hour.setItems(hourList);
        loopView_hour.setItemsVisibleCount(0);
        loopView_hour.setInitPosition(8);
        loopView_hour.setTextSize(18);
        loopView_minute.setItems(minList);
        loopView_minute.setItemsVisibleCount(0);
        loopView_minute.setTextSize(18);
        loopView_minute.setInitPosition(30);
        loopView_date.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                getMsgTime();
            }
        });
        loopView_am.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                getMsgTime();
            }
        });
        loopView_hour.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                getMsgTime();
            }
        });
        loopView_minute.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                getMsgTime();
            }
        });
        StringBuffer sb = new StringBuffer();
        Date da = TimeUtils.addDate(today, loopView_date.getSelectedItem());
        int year = TimeUtils.getYearFromDate(da);
        int month = TimeUtils.getMonthFromDate(da);
        int day = TimeUtils.getDayFromDate(da);
        sb.append(year);
        sb.append("年");
        sb.append(month);
        sb.append("月");
        sb.append(day);
        sb.append("日");
        sb.append(amList.get(loopView_am.getSelectedItem()));
        sb.append(hourList.get(8));
        sb.append(":");
        sb.append(minList.get(30));
        tv_time.setText(sb.toString());
        tv_right.setEnabled(false);
        tv_right.setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_color));
        initLengthLimit();
    }


    @OnClick({R.id.rl_recount, R.id.tv_left, R.id.tv_right})
    void rl_recount(View view) {
        switch (view.getId()) {
            case R.id.tv_left:
                finish();
                break;
            case R.id.rl_recount:
                Intent it =new Intent(this, SetRemindRepeatActivity.class);
                it.putExtra("repeatType",repeatType);
                startActivity(it);
                break;
            case R.id.tv_right:
                String sNote = ed_msg.getText().toString();
                if (TextUtils.isEmpty(sNote)) {
                    ToastUtils.showShortToast("请先填写提醒事项");
                    return;
                }
                if (model == null) {
                    addRemind(sNote, 1, 0);
                } else {
                    addRemind(sNote, 3, model.lReminderId);
                }
                break;
        }
    }

    private void initData() {
        dateList = new ArrayList<>();
        dateList2 = new ArrayList<>();
        amList = new ArrayList<>();
        hourList = new ArrayList<>();
        minList = new ArrayList<>();
        amList.add("上午");
        amList.add("下午");
        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                minList.add("0" + i);
            } else {
                minList.add("" + i);
            }
        }
        for (int i = 1; i < 13; i++) {
            if (i < 10) {
                hourList.add("0" + i);
            } else {
                hourList.add("" + i);
            }
        }
        dateList.add("今天");
        dateList2.add(TimeUtils.getTime(System.currentTimeMillis(), DATE_FORMAT_DATE));
        for (int i = 1; i < 7; i++) {
            Date da = TimeUtils.addDate(today, i);
            int month = TimeUtils.getMonthFromDate(da);
            int day = TimeUtils.getDayFromDate(da);
            String sb = month + "月" + day + "日" + TimeUtils.getWeekFromIn(TimeUtils
                    .getWeekFromDate(da));
            dateList.add(sb);
            dateList2.add(TimeUtils.getTime(da.getTime(), DATE_FORMAT_DATE));
        }
    }

    public String getMsgTime() {
        StringBuffer sb = new StringBuffer();
        Date da = TimeUtils.addDate(today, loopView_date.getSelectedItem());
        int year = TimeUtils.getYearFromDate(da);
        int month = TimeUtils.getMonthFromDate(da);
        int day = TimeUtils.getDayFromDate(da);
        sb.append(year);
        sb.append("年");
        sb.append(month);
        sb.append("月");
        sb.append(day);
        sb.append("日");
        sb.append(amList.get(loopView_am.getSelectedItem()));
        sb.append(hourList.get(loopView_hour.getSelectedItem()));
        sb.append(":");
        sb.append(minList.get(loopView_minute.getSelectedItem()));
        tv_time.setText(sb.toString());
        return sb.toString();
    }

    @Override
    protected void onReceiveEvent(Event event) {
        super.onReceiveEvent(event);
        if (event.getCode() == ADD_REMIND_REPEAT_SUCCESS) {
            repeatType = (int) event.getData();
            switch (repeatType) {
                case 1:
                    tv_cycle.setText("永不");
                    break;
                case 2:
                    tv_cycle.setText("每天");
                    break;
                case 3:
                    tv_cycle.setText("每周");
                    break;
                case 4:
                    tv_cycle.setText("每月");
                    break;
                case 5:
                    tv_cycle.setText("每年");
                    repeatType = 1;
                    break;
            }
        }
    }

    public void addRemind(String sNote, int eCloud_type, long lReminderId) {
        ELoginPlatform platform;
        if (CookieInterceptor.get().getThridLogin().getLoginType().toLowerCase().equals("wx")) {
            platform = ELoginPlatform.WX;
        } else {
            platform = ELoginPlatform.QQOpen;
        }
        String ymd = dateList2.get(loopView_date.getSelectedItem());
        int hour = 0;
        if (loopView_am.getSelectedItem() == 1) {
            hour += 12;
        }
        hour += Integer.parseInt(hourList.get(loopView_hour.getSelectedItem()));
        String date = ymd + " " + hour + ":" + minList.get(loopView_minute.getSelectedItem()) + ":00";
        long timeMill = TimeUtils.string2Millis(date);
        if (System.currentTimeMillis() > timeMill) {
            ToastUtils.showShortToast("日程提醒时间必须大于当前时间");
            return;
        }
        LoadingDialog.getInstance(this).show();
        TVSManager.getInstance(this, BuildConfig.APP_ID_WX, BuildConfig.APP_ID_QQ)
                .requestTskmUniAccess(platform, PigUtils.getAlarmDeviceMManager(), PigUtils.getRemindUniAccessinfo
                        (sNote, eCloud_type, repeatType, lReminderId, timeMill), new TVSManager
                        .TVSAlarmListener() {
                    @Override
                    public void onSuccess(CommOpInfo msg) {
                        LoadingDialog.getInstance(AddRemindActivity.this).dismiss();
                        if (model == null) {
                            ToastUtils.showShortToast("新建提醒成功");
                        } else {
                            ToastUtils.showShortToast("更新提醒成功");
                        }
                        Event<String> event = new Event<>(ADD_REMIND_SUCCESS);
                        EventBusUtil.sendEvent(event);
                        finish();
                    }

                    @Override
                    public void onError(String code) {
                        LoadingDialog.getInstance(AddRemindActivity.this).dismiss();
                        ToastUtils.showShortToast(code);
                        LogUtils.d("code:" + code);
                    }
                });
    }

    public void initLengthLimit() {
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new

                InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                        boolean bInvlid = false;
                        int sourceLen = CommendUtil.getMsgLength(source.toString());
                        int destLen = CommendUtil.getMsgLength(dest.toString());
                        LogUtils.d("sourceLen:" + sourceLen + ",destLen:" + destLen);
                        if (sourceLen + destLen > maxchineseLength) {
                            ToastUtils.showShortToast("提醒内容最多输入20字");
                            return "";
                        }
                        return source;
                    }
                };
        ed_msg.setFilters(FilterArray);
        ed_msg.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s,
                                                  int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s,
                                              int start, int before, int count) {
                        int mMsgMaxLength = 0;
                        Editable editable = ed_msg.getText();
                        String str = editable.toString().trim();
                        //得到最初字段的长度大小，用于光标位置的判断
                        int selEndIndex = Selection.getSelectionEnd(editable);
                        // 取出每个字符进行判断，如果是字母数字和标点符号则为一个字符加1，
                        //如果是汉字则为两个字符
                        for (int i = 0; i < str.length(); i++) {
                            char charAt = str.charAt(i);
                            //32-122包含了空格，大小写字母，数字和一些常用的符号，
                            //如果在这个范围内则算一个字符，
                            //如果不在这个范围比如是汉字的话就是两个字符
                            if (charAt >= 32 && charAt <= 122) {
                                mMsgMaxLength++;
                            } else {
                                mMsgMaxLength += 2;
                            }
                            // 当最大字符大于6000时，进行字段的截取，并进行提示字段的大小
                            if (mMsgMaxLength > maxchineseLength * 2) {
                                // 截取最大的字段
                                String newStr = str.substring(0, i);
                                ed_msg.setText(newStr);
                                // 得到新字段的长度值
                                editable = ed_msg.getText();
                                int newLen = editable.length();
                                if (selEndIndex > newLen) {
                                    selEndIndex = editable.length();
                                }
                                ToastUtils.showShortToast("提醒内容最多输入20字");
                            }
                            // 设置新光标所在的位置
                            Selection.setSelection(editable, selEndIndex);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (TextUtils.isEmpty(ed_msg.getText())) {
                            tv_right.setEnabled(false);
                            tv_right.setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_color));
                        } else {
                            tv_right.setEnabled(true);
                            tv_right.setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_checked_color));
                        }
                    }
                }
        );
    }
}
