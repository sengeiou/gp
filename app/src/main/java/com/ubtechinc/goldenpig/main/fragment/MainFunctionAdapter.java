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
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.main.BleWebActivity;
import com.ubtechinc.goldenpig.personal.alarm.AlarmListActivity;
import com.ubtechinc.goldenpig.personal.interlocution.InterlocutionActivity;
import com.ubtechinc.goldenpig.personal.remind.RemindActivity;
import com.ubtechinc.goldenpig.pigmanager.RecordActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.mypig.QRCodeActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.voiceChat.ui.ChatActivity;

import java.util.HashMap;
import java.util.List;

public class MainFunctionAdapter extends RecyclerView.Adapter<MainFunctionAdapter.ViewHodler> implements View.OnClickListener {


    private final String TAG = "PigFragmentAdapter";
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
            PigInfo myPig = AuthLive.getInstance().getCurrentPig();
            switch (functionEnum) {
                case VOICE_MAIL: {
                    if (myPig != null && myPig.isAdmin) {
                        ActivityRoute.toAnotherActivity((Activity) context, ChatActivity.class, false);
                    }
                    if (UBTPGApplication.voiceMail_debug) {
                        ActivityRoute.toAnotherActivity((Activity) context, ChatActivity.class, false);
                    }
                }
                break;
                case PAIR: {
                    if (myPig != null && myPig.isAdmin) {
                        //TODO 配对二维码
                        HashMap<String, Boolean> param = new HashMap<>();
                        param.put("isPair", true);
                        ActivityRoute.toAnotherActivity((Activity) context, QRCodeActivity.class, param, false);
                    } else {
                        ToastUtils.showShortToast(R.string.only_admin_operate);
                    }
                }
                break;
                case ALARM: {
                    if (myPig != null && myPig.isAdmin) {
                        ActivityRoute.toAnotherActivity((Activity) context, AlarmListActivity.class, false);
                    } else {
                        ToastUtils.showShortToast(R.string.only_admin_operate);
                    }
                }
                break;
                case REMIND:
                    if (myPig != null && myPig.isAdmin) {
                        ActivityRoute.toAnotherActivity((Activity) context, RemindActivity.class, false);
                    } else {
                        ToastUtils.showShortToast(R.string.only_admin_operate);
                    }
                    break;
                case CUSTOM_QA:
                    if (myPig != null && myPig.isAdmin) {
                        ActivityRoute.toAnotherActivity((Activity) context, InterlocutionActivity.class, false);
                    } else {
                        ToastUtils.showShortToast(R.string.only_admin_operate);
                    }
                    break;
                case CALL_RECORD:
                    if (myPig != null && myPig.isAdmin) {
                        ActivityRoute.toAnotherActivity((Activity) context, RecordActivity.class, false);
                    } else {
                        ToastUtils.showShortToast(R.string.only_admin_operate);
                    }
                    break;
                case BLE:
                    ActivityRoute.toAnotherActivity((Activity) context, BleWebActivity.class, false);
                    break;
            }
        }
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
