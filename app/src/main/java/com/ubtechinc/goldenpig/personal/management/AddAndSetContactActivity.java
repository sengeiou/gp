package com.ubtechinc.goldenpig.personal.management;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.imlibv2.bean.listener.OnUbtTIMConverListener;
import com.ubt.improtolib.GPResponse;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.ScreenUtils;
import com.ubtech.utilcode.utils.network.NetworkHelper;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewTv;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.comm.GlobalVariable;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.model.AddressBookmodel;
import com.ubtechinc.goldenpig.personal.management.contact.ContactListActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.utils.CommendUtil;
import com.ubtechinc.goldenpig.utils.UbtToastUtils;
import com.ubtechinc.goldenpig.view.GridSpacingItemDecoration;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import okio.Timeout;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.CONTACT_CHECK_SUCCESS;
import static com.ubtechinc.goldenpig.utils.CommendUtil.TIMEOUT_MILLI;

public class AddAndSetContactActivity extends BaseNewActivity implements Observer {
    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewTv rl_titlebar;
    @BindView(R.id.et_phone)
    EditText etPhone;
    //    @BindView(R.id.iv_phone_clear)
//    ImageView ivPhoneClear;
//    @BindView(R.id.view_clear_line)
//    View viewClearLine;
//    @BindView(R.id.iv_add)
//    ImageView ivAdd;
    @BindView(R.id.et_name)
    EditText etName;
    //    @BindView(R.id.iv_name_clear)
//    ImageView ivNameClear;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    /**
     * 0为添加
     * 1为编辑
     */
    private int type = 0;
    private List<String> mList;
    private BaseQuickAdapter<String, BaseViewHolder> adapter;
    private String strPhone;
    private String strName;
    private ArrayList<AddressBookmodel> oldList;
    private int updatePosition = -1;
    private int curPosition = -1;
    private String TAG = AddAndSetContactActivity.class.getSimpleName();

    private MyHandler mHandler;

    private class MyHandler extends Handler {
        WeakReference<Activity> mWeakReference;

        public MyHandler(Activity activity) {
            mWeakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (mWeakReference.get() != null) {
                    UbtToastUtils.showCustomToast(mWeakReference.get(), getString(R.string.ubt_robot_offline));
                    LoadingDialog.getInstance(mWeakReference.get()).dismiss();
                }
            }
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_set_contact;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new MyHandler(this);
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null) {
            UbtTIMManager.getInstance().setPigAccount(pigInfo.getRobotName());
        } else {
//            UbtTIMManager.getInstance().setPigAccount("2cb9b9a3");
        }
        UbtTIMManager.getInstance().setMsgObserve(this);
        UbtTIMManager.getInstance().setOnUbtTIMConverListener(new OnUbtTIMConverListener() {
            @Override
            public void onError(int i, String s) {
                if (AuthLive.getInstance().getCurrentPig() != null) {
                    UbtToastUtils.showCustomToast(getApplication(), "八戒未登录");
                } else {
                    UbtToastUtils.showCustomToast(getApplication(), "未绑定八戒");
                }
                LoadingDialog.getInstance(AddAndSetContactActivity.this).dismiss();
            }

            @Override
            public void onSuccess() {

            }
        });
        //initLengthLimit();
        type = getIntent().getIntExtra("type", 0);
        updatePosition = getIntent().getIntExtra("position", -1);
        oldList = getIntent().getParcelableArrayListExtra("list");
        if (oldList == null) {
            oldList = new ArrayList<>();
        }
//        else if (oldList.size() == 11) {
//            oldList.remove(oldList.size() - 1);
//        }
        switch (type) {
            case 0:
                rl_titlebar.setTitleText(getString(R.string.add_contact));
                break;
            case 1:
                rl_titlebar.setTitleText(getString(R.string.set_contact));
                break;
            default:
        }
        rl_titlebar.setLeftOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rl_titlebar.setRightOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(strPhone) || TextUtils.isEmpty(strName)) {
                    UbtToastUtils.showCustomToast(getApplication(), "电话或昵称不能为空");
                    return;
                }
                if (!isGB2312(strName)) {
                    UbtToastUtils.showCustomToast(AddAndSetContactActivity.this, "请输入6个字以内中文昵称");
                    return;
                }
                if (!checkOldList()) {
                    return;
                }
                if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
                    UbtToastUtils.showCustomToast(AddAndSetContactActivity.this, getString(R.string.network_error));
                    return;
                }
                if (mHandler.hasMessages(1)) {
                    mHandler.removeMessages(1);
                }
                mHandler.sendEmptyMessageDelayed(1, TIMEOUT_MILLI);// 15s 秒后检查加载框是否还在
                try {
                    if (type == 0) {
                        UbtTIMManager.getInstance().addUser(strName, strPhone);
                    } else {
                        UbtTIMManager.getInstance().updateUser(strName, strPhone, oldList.get
                                (updatePosition).id + "");
                    }
                    LoadingDialog.getInstance(AddAndSetContactActivity.this).show();
                } catch (Exception e) {
                    UbtToastUtils.showCustomToast(getApplication(), "请求异常，请重试");
                }
            }
        });
        rl_titlebar.setTvRightName(getString(R.string.complete));
        rl_titlebar.getTvRight().setTextColor(getResources().getColor(R.color
                .ubt_skip_txt_unenable_color));
        rl_titlebar.getTvRight().setEnabled(false);
        mList = new ArrayList<>();
        initData();
        GridLayoutManager gm = new GridLayoutManager(this, 5);
        gm.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(gm);
        int itemSpace = (int) ((ScreenUtils.getScreenWidth() - 2 * getResources().getDimension(R.dimen.dp_30) - 5 *
                getResources().getDimension(R.dimen.dp_50)) / 4);
        GridSpacingItemDecoration de = new GridSpacingItemDecoration(5, itemSpace, (int)
                getResources()
                        .getDimension(R.dimen.dp_17), false);
        recycler.addItemDecoration(de);
        recycler.setAdapter(adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout
                .adapter_contact, mList) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_name, item);

                //TODO
                if (curPosition >= 0 && curPosition < mList.size()) {
                    String curText = mList.get(curPosition);
                    if (curText.equals(item)) {
                        helper.setBackgroundRes(R.id.tv_name, R.drawable.shape_ubt_btn_cyan_gb);
                    } else {
                        helper.setBackgroundRes(R.id.tv_name, R.drawable.gray_round_frame);
                    }
                } else {
                    helper.setBackgroundRes(R.id.tv_name, R.drawable.gray_round_frame);
                }
            }
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                etName.setText(mList.get(position));
                etName.setSelection(mList.get(position).length());

                //refresh
                curPosition = position;
                adapter.notifyDataSetChanged();
//                adapter.notifyItemChanged(position);
            }
        });
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                strPhone = etPhone.getText().toString();
                checkMSG();
            }
        });
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                strName = etName.getText().toString();
                if (mList != null && !mList.isEmpty()) {
                    int newPos = curPosition;
                    for (int i = 0; i < mList.size(); i++) {
                        String value = mList.get(i);
                        if (!TextUtils.isEmpty(value) && value.equals(strName)) {
                            newPos = i;
                            break;
                        } else {
                            newPos = -1;
                        }
                    }
                    if (curPosition != newPos) {
                        curPosition = newPos;
                        adapter.notifyDataSetChanged();
                    }
                }
                checkMSG();
            }
        });
        if (updatePosition >= 0 && updatePosition < oldList.size()) {
            strPhone = oldList.get(updatePosition).phone;
            strName = oldList.get(updatePosition).name;
        }
        switch (type) {
            case 1:
                if (!TextUtils.isEmpty(strPhone)) {
                    etPhone.setText(strPhone);
                    try {
                        etPhone.setSelection(strPhone.length());
                    } catch (Exception e) {
                    }
                }
                if (!TextUtils.isEmpty(strName)) {
                    etName.setText(strName);
                }
                break;
            default:
        }
    }

    /**
     * 判定输入汉字
     */
    public boolean isChinese(char c) {
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

    public void initLengthLimit() {
//        InputFilter[] FilterArray = new InputFilter[1];
//        FilterArray[0] = (source, start, end, dest, dstart, dend) -> {
//            for (int i = start; i < end; i++) {
//                if (!isChinese(source.charAt(i))) {
//                    return "";
//                } else {
//                    if ((source.charAt(i) >= 0x4e00) && (source.charAt(i) <= 0x9fbb)) {
//
//                    } else {
//                        return "";
//                    }
//                }
//            }
//            int sourceLen = CommendUtil.getMsgLength(source.toString());
//            int destLen = CommendUtil.getMsgLength(dest.toString());
//            LogUtils.d("sourceLen:" + sourceLen + ",destLen:" + destLen);
//            if (sourceLen + destLen > 6) {
//                UbtToastUtils.showCustomToast(getApplication(), "请输入6个字以内昵称");
//                return "";
//            }
//            return source;
//        };
//        etName.setFilters(FilterArray);
    }

//    @OnClick({R.id.iv_phone_clear, R.id.iv_name_clear, R.id.iv_add})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.iv_phone_clear:
//                etPhone.setText("");
//                break;
//            case R.id.iv_name_clear:
//                etName.setText("");
//                break;
//            case R.id.iv_add:
//                importContact();
////                if (TextUtils.isEmpty(strPhone) || TextUtils.isEmpty(strName)) {
////                    ToastUtils.showShortToast("电话或昵称不能为空");
////                    break;
////                }
////                if (!checkOldList()) {
////                    break;
////                }
////                try {
////                    if (type == 0) {
////                        UbtTIMManager.getInstance().addUser(strName, strPhone);
////                    } else {
////                        UbtTIMManager.getInstance().updateUser(strName, strPhone, oldList.get
////                                (updatePosition).id + "");
////                    }
////                    LoadingDialog.getInstance(AddAndSetContactActivity.this).setTimeout(20)
////                            .setShowToast(true).show();
////                } catch (Exception e) {
////                    ToastUtils.showShortToast("请求异常，请重试");
////                }
//                break;
//            default:
//        }
//    }

    /**
     * 导入联系人
     */
    private void importContact() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (AndPermission.hasPermission(this, Manifest.permission.READ_CONTACTS)) {
                intentToContact();
            } else if (AndPermission.hasAlwaysDeniedPermission(this, Arrays.asList(Manifest.permission.READ_CONTACTS)
            )) {
                showPermissionDialog(Permission.CONTACTS);
            } else {
                ActivityCompat.requestPermissions(AddAndSetContactActivity.this,
                        new String[]{android.Manifest.permission.READ_CONTACTS},
                        GlobalVariable.REQUEST_CONTACTS_READ_PERMISSON);
            }
//            if (ContextCompat.checkSelfPermission(AddAndSetContactActivity.this, android.Manifest.permission
// .READ_CONTACTS)
//                    != PackageManager.PERMISSION_GRANTED) {
//                // 若不为GRANTED(即为DENIED)则要申请权限了
//                // 申请权限 第一个为context 第二个可以指定多个请求的权限 第三个参数为请求码
//                ActivityCompat.requestPermissions(AddAndSetContactActivity.this,
//                        new String[]{android.Manifest.permission.READ_CONTACTS},
//                        GlobalVariable.REQUEST_CONTACTS_READ_PERMISSON);
//            } else {
//                //权限已经被授予，在这里直接写要执行的相应方法即可
//                intentToContact();
//            }
        } else {
            // 低于6.0的手机直接访问
            intentToContact();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (requestCode == GlobalVariable.REQUEST_CONTACTS_READ_PERMISSON) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                intentToContact();
            } else {
                Toast.makeText(AddAndSetContactActivity.this, "授权被禁止", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
    }

    private void intentToContact() {
        // 跳转到联系人界面
//        Intent intent = new Intent();
//        intent.setAction("android.intent.action.PICK");
//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.setType("vnd.android.cursor.dir/phone_v2");
//        startActivityForResult(intent, GlobalVariable.REQUEST_CONTACTS_READ_RESULT);

        startActivity(new Intent(this, ContactListActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GlobalVariable.REQUEST_CONTACTS_READ_RESULT) {
            if (data != null) {
                Uri uri = data.getData();
                String phoneNum = null;
                String contactName = null;
                // 创建内容解析者
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = null;
                if (uri != null) {
                    cursor = contentResolver.query(uri,
                            new String[]{"display_name", "data1"}, null, null, null);
                }
                if (cursor == null) return;
                while (cursor.moveToNext()) {
                    contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone
                            .DISPLAY_NAME));
                    phoneNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                cursor.close();
                //  把电话号码中的  -  符号 替换成空格
                if (phoneNum != null) {
                    phoneNum = phoneNum.replaceAll("-", " ");
                    // 空格去掉  为什么不直接-替换成"" 因为测试的时候发现还是会有空格 只能这么处理
                    phoneNum = phoneNum.replaceAll(" ", "");
                }

                etName.setText(contactName);
                etPhone.setText(phoneNum);
            }
        }
    }

    public void initData() {
        mList.clear();
        mList.add("爸爸");
        mList.add("妈妈");
        mList.add("爷爷");
        mList.add("奶奶");
        mList.add("外公");
        mList.add("外婆");
        mList.add("儿子");
        mList.add("女儿");
        mList.add("弟弟");
        mList.add("哥哥");
        mList.add("姐姐");
        mList.add("妹妹");
        mList.add("老公");
        mList.add("老婆");
    }

    private boolean checkOldList() {
        try {
            for (int i = 0; i < oldList.size(); i++) {
                if (TextUtils.isEmpty(oldList.get(i).phone) || TextUtils.isEmpty(oldList.get(i).name)) {
                    continue;
                }
                if (oldList.get(i).type != 0) {
                    continue;
                }
                if (type == 1 && i == updatePosition) {
                    continue;
                }
                if (oldList.get(i).phone.equals(strPhone)) {
                    UbtToastUtils.showCustomToast(getApplication(), "手机号重复，请重新输入");
                    return false;
                }
                if (oldList.get(i).name.equals(strName)) {
                    UbtToastUtils.showCustomToast(getApplication(), "昵称重复，请重新输入");
                    return false;
                }
            }
        } catch (Exception e) {
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UbtTIMManager.getInstance().deleteMsgObserve(this);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        LoadingDialog.getInstance(AddAndSetContactActivity.this).dismiss();
        mHandler.removeMessages(1);
        try {
            TIMMessage msg = (TIMMessage) arg;
            for (int i = 0; i < msg.getElementCount(); ++i) {
                TIMElem tIMElem = msg.getElement(i);
                if (tIMElem instanceof TIMCustomElem) {
                    TIMCustomElem elem = (TIMCustomElem) tIMElem;
                    dealMsg(elem.getData());
                }
            }
        } catch (Exception e) {

        }
    }

    private void dealMsg(Object arg) throws InvalidProtocolBufferException {
        ChannelMessageContainer.ChannelMessage msg = ChannelMessageContainer.ChannelMessage
                .parseFrom((byte[]) arg);
        String action = msg.getHeader().getAction();
        switch (action) {
            case "/im/mail/query":
                break;
            case "/im/mail/add":
                Boolean flag = msg.getPayload().unpack(GPResponse.Response.class).getResult();
                if (flag) {
                    UbtToastUtils.showCustomToast(getApplication(), "添加成功");
                    EventBusUtil.sendEvent(new Event<String>(CONTACT_CHECK_SUCCESS));
                    finish();
                } else {
                    UbtToastUtils.showCustomToast(getApplication(), "请求异常，请重试");
                }
                break;

            case "/im/mail/delete":
                break;
            case "/im/mail/update":
                Boolean flag2 = msg.getPayload().unpack(GPResponse.Response.class).getResult();
                if (flag2) {
                    UbtToastUtils.showCustomToast(getApplication(), "编辑成功");
                    EventBusUtil.sendEvent(new Event<String>(CONTACT_CHECK_SUCCESS));
                    finish();
                } else {
                    UbtToastUtils.showCustomToast(getApplication(), "请求异常，请重试");
                }
                break;
            default:
        }
    }

    private void checkMSG() {
        if (TextUtils.isEmpty(strPhone) || TextUtils.isEmpty(strName)) {
            rl_titlebar.getTvRight().setEnabled(false);
            rl_titlebar.getTvRight().setTextColor(getResources().getColor(R.color
                    .ubt_skip_txt_unenable_color));
        } else {
            rl_titlebar.getTvRight().setEnabled(true);
            rl_titlebar.getTvRight().setTextColor(getResources().getColor(R.color
                    .ubt_tab_btn_txt_checked_color));
        }
    }

    public static Boolean isGB2312(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            String bb = str.substring(i, i + 1);
            // 生成一个Pattern,同时编译一个正则表达式,其中的u4E00("一"的unicode编码)-\u9FA5("龥"的unicode编码)
            boolean cc = java.util.regex.Pattern.matches("[\u4E00-\u9FA5]", bb);
            if (cc == false) {
                return cc;
            }
        }
        return true;
    }
}
