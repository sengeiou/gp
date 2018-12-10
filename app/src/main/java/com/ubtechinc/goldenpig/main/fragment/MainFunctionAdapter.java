package com.ubtechinc.goldenpig.main.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.comm.entity.PairPig;
import com.ubtechinc.goldenpig.comm.widget.UBTSubTitleDialog;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.main.BleWebActivity;
import com.ubtechinc.goldenpig.personal.alarm.AlarmListActivity;
import com.ubtechinc.goldenpig.personal.interlocution.InterlocutionActivity;
import com.ubtechinc.goldenpig.personal.remind.RemindActivity;
import com.ubtechinc.goldenpig.pigmanager.RecordActivity;
import com.ubtechinc.goldenpig.pigmanager.SetNetWorkEnterActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.mypig.PairPigActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.QRCodeActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.voiceChat.ui.ChatActivity;

import java.util.HashMap;
import java.util.List;

public class MainFunctionAdapter extends RecyclerView.Adapter<MainFunctionAdapter.ViewHodler> implements View.OnClickListener {

    private List<FunctionEnum> list;
    private Context context;

    public MainFunctionAdapter(Context context, List<FunctionEnum> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_function_card, null);
        return new ViewHodler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHodler holder, int position) {
        FunctionEnum functionEnum = list.get(position);
        holder.tvItem.setText(functionEnum.label);
        holder.tvItem.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(context, functionEnum.resIcon),
                null, null);
        holder.itemView.setTag(functionEnum);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {
        Object o = v.getTag();
        if (o != null && o instanceof FunctionEnum) {
            FunctionEnum functionEnum = (FunctionEnum) o;
            switch (functionEnum) {
                case VOICE_MAIL:
                    if (UBTPGApplication.voiceMail_debug) {
                        ActivityRoute.toAnotherActivity((Activity) context, ChatActivity.class, false);
                    } else {
                        enterFunction(ChatActivity.class, null);
                    }
                    break;
                case PAIR:
                    HashMap<String, Boolean> param = new HashMap<>();
                    param.put("isPair", true);
                    PairPig pairPig = AuthLive.getInstance().getPairPig();
                    if (pairPig != null) {
                        enterFunction(PairPigActivity.class, param);
                    } else {
                        enterFunction(QRCodeActivity.class, param);
                    }
                    break;
                case ALARM:
                    enterFunction(AlarmListActivity.class, null);
                    break;
                case REMIND:
                    enterFunction(RemindActivity.class, null);
                    break;
                case CUSTOM_QA:
                    enterFunction(InterlocutionActivity.class, null);
                    break;
                case CALL_RECORD:
                    enterFunction(RecordActivity.class, null);
                    break;
                case BLE:
                    ActivityRoute.toAnotherActivity((Activity) context, BleWebActivity.class, false);
                    break;
            }
        }
    }

    private void enterFunction(Class clazz, HashMap<String, ? extends Object> hashMap) {
        PigInfo myPig = AuthLive.getInstance().getCurrentPig();
        if (myPig == null) {
            showBindTipDialog();
        } else if (myPig.isAdmin) {
            ActivityRoute.toAnotherActivity((Activity) context, clazz, hashMap, false);
        } else {
            ToastUtils.showShortToast(R.string.only_admin_operate);
        }
    }

    private void showBindTipDialog() {
        UBTSubTitleDialog dialog = new UBTSubTitleDialog(context);
        dialog.setRightBtnColor(ContextCompat.getColor(context, R.color.ubt_tab_btn_txt_checked_color));
        dialog.setTips("请完成绑定与配网");
        dialog.setSubTips("完成后即可使用各项技能");
        dialog.setLeftButtonTxt("取消");
        dialog.setRightButtonTxt("确认");
        dialog.setOnUbtDialogClickLinsenter(new UBTSubTitleDialog.OnUbtDialogClickLinsenter() {
            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                //TODO do管理员权限转让
                ActivityRoute.toAnotherActivity((Activity) context, SetNetWorkEnterActivity.class, false);
            }
        });
        dialog.show();
    }


    class ViewHodler extends RecyclerView.ViewHolder {

        TextView tvItem;

        public ViewHodler(View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tv_func_label);
            itemView.setOnClickListener(MainFunctionAdapter.this);
        }
    }

    enum FunctionEnum {

        VOICE_MAIL("语音留言", R.drawable.ic_voicemail),
        PAIR("配对八戒", R.drawable.ic_pair),
        ALARM("闹钟", R.drawable.ic_alarm),
        REMIND("日程提醒", R.drawable.ic_scheduel),
        CUSTOM_QA("定制问答", R.drawable.ic_question_answer),
        CALL_RECORD("最近通话", R.drawable.ic_call_record),
        BLE("蓝牙音箱", R.drawable.ic_bt_speaker),;

        String label;
        int resIcon;

        FunctionEnum(String label, int resIcon) {
            this.label = label;
            this.resIcon = resIcon;
        }

    }
}
