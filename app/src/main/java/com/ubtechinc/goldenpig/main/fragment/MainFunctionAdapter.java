package com.ubtechinc.goldenpig.main.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseActivity;
import com.ubtechinc.goldenpig.comm.entity.PairPig;
import com.ubtechinc.goldenpig.comm.img.GlideCircleTransform;
import com.ubtechinc.goldenpig.comm.widget.UBTSubTitleDialog;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.main.BleWebActivity;
import com.ubtechinc.goldenpig.main.FunctionModel;
import com.ubtechinc.goldenpig.main.MainActivity;
import com.ubtechinc.goldenpig.personal.alarm.AlarmListActivity;
import com.ubtechinc.goldenpig.personal.interlocution.InterlocutionActivity;
import com.ubtechinc.goldenpig.personal.management.AddressBookActivity;
import com.ubtechinc.goldenpig.personal.remind.RemindActivity;
import com.ubtechinc.goldenpig.pigmanager.RecordActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.mypig.PairPigActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.QRCodeActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.voiceChat.ui.ChatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ubtechinc.goldenpig.main.fragment.MainFunctionAdapter.FunctionEnum.ALARM;
import static com.ubtechinc.goldenpig.main.fragment.MainFunctionAdapter.FunctionEnum.BLE;
import static com.ubtechinc.goldenpig.main.fragment.MainFunctionAdapter.FunctionEnum.CALL_RECORD;
import static com.ubtechinc.goldenpig.main.fragment.MainFunctionAdapter.FunctionEnum.CUSTOM_QA;
import static com.ubtechinc.goldenpig.main.fragment.MainFunctionAdapter.FunctionEnum.MAIL_LIST;
import static com.ubtechinc.goldenpig.main.fragment.MainFunctionAdapter.FunctionEnum.PAIR;
import static com.ubtechinc.goldenpig.main.fragment.MainFunctionAdapter.FunctionEnum.REMIND;
import static com.ubtechinc.goldenpig.main.fragment.MainFunctionAdapter.FunctionEnum.VOICE_MAIL;

public class MainFunctionAdapter extends RecyclerView.Adapter<MainFunctionAdapter.ViewHodler> implements View.OnClickListener {

    private List<FunctionEnum> list;
    private Context context;

    private boolean isDynamicData;

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
        if (isDynamicData) {
            holder.tvItem.setText(functionEnum.name);
            Glide.with(context)
                    .load(functionEnum.icoUrl)
                    .asBitmap()
                    .centerCrop()
                    .transform(new GlideCircleTransform(context))
                    .placeholder(R.drawable.ic_sign_in)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            BitmapDrawable drawable = new BitmapDrawable(context.getResources(), resource);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            holder.tvItem.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                        }
                    });
        } else {
            holder.tvItem.setText(functionEnum.label);
            holder.tvItem.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(context, functionEnum.resIcon),
                    null, null);
        }
        holder.ivRedPoint.setVisibility(functionEnum.hasRedPoint ? View.VISIBLE : View.GONE);
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
                    PairPig pairPig = AuthLive.getInstance().getPairPig();
                    if (pairPig != null) {
                        enterFunction(PairPigActivity.class, null);
                    } else {
                        HashMap<String, Boolean> param = new HashMap<>();
                        param.put("isPair", true);
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
                case MAIL_LIST:
                    HashMap<String,Boolean> map = new HashMap<>();
                    map.put("card",((MainActivity)context).isNoSim);
                    enterFunction(AddressBookActivity.class, null);
                    break;
                case BLE:
                    ActivityRoute.toAnotherActivity((Activity) context, BleWebActivity.class, false);
                    break;
            }
        }
    }

    public void notifyItemChanged(FunctionEnum functionEnum) {
        notifyItemChanged(functionEnum.ordinal());
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
        dialog.setSubTipGravity(Gravity.CENTER);
        dialog.setLeftButtonTxt("取消");
        dialog.setRightButtonTxt("确认");
        dialog.setOnUbtDialogClickLinsenter(new UBTSubTitleDialog.OnUbtDialogClickLinsenter() {
            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                //TODO goto ble bind config
                if (context instanceof BaseActivity) {
                    ((BaseActivity) context).toBleConfigActivity(false);
                }
            }
        });
        dialog.show();
    }

    public synchronized void updateData(FunctionModel mFunctionModel) {
        List<FunctionModel.CategorysModel> categorys = mFunctionModel.catetory.categorys;
        if (categorys != null && !categorys.isEmpty()) {
            List<FunctionEnum> tempList = new ArrayList<>();
            for (FunctionModel.CategorysModel categorysModel : categorys) {
                FunctionEnum functionEnum = parseModel(categorysModel);
                tempList.add(functionEnum);
            }
            if (list != null) {
                list.clear();
            } else {
                list = new ArrayList<>();
            }
            isDynamicData = true;
            list.addAll(tempList);
            notifyDataSetChanged();
        }
    }

    private FunctionEnum parseModel(FunctionModel.CategorysModel categorysModel) {
        int type = Integer.parseInt(categorysModel.type);
        FunctionEnum functionEnum = null;
        switch (type) {
            case 1:
                functionEnum = VOICE_MAIL;
                break;
            case 2:
                functionEnum = PAIR;
                break;
            case 3:
                functionEnum = ALARM;
                break;
            case 4:
                functionEnum = REMIND;
                break;
            case 5:
                functionEnum = CUSTOM_QA;
                break;
            case 6:
                functionEnum = CALL_RECORD;
                break;
            case 7:
                functionEnum = MAIL_LIST;
                break;
            case 8:
                functionEnum = BLE;
                break;
            default:
        }
        if (functionEnum != null) {
            functionEnum.name = categorysModel.name;
            functionEnum.icoUrl = categorysModel.icoUrl;
            functionEnum.type = categorysModel.type;
            functionEnum.url = categorysModel.url;
        }
        return functionEnum;
    }


    class ViewHodler extends RecyclerView.ViewHolder {

        TextView tvItem;
        ImageView ivRedPoint;

        public ViewHodler(View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tv_func_label);
            ivRedPoint = itemView.findViewById(R.id.iv_red_point);
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
        MAIL_LIST("通讯录", R.drawable.ic_mail_list),
        BLE("蓝牙音箱", R.drawable.ic_bt_speaker),;

        String label;
        int resIcon;
        boolean hasRedPoint;

        /**
         * start fro server
         */
        public String name;

        public String icoUrl;

        public String type;

        public String url;

        /**
         * end fro server
         */

        FunctionEnum(String label, int resIcon) {
            this.label = label;
            this.resIcon = resIcon;
        }

        public boolean isHasRedPoint() {
            return hasRedPoint;
        }

        public void setHasRedPoint(boolean hasRedPoint) {
            this.hasRedPoint = hasRedPoint;
        }
    }
}
