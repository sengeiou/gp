package com.ubtechinc.goldenpig.personal;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ubt.qrcodelib.Constants;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.comm.widget.UBTSubTitleDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.personal.management.AddressBookActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.mypig.MyPigActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.PairPigActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.PigMemberActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.QRCodeActivity;
import com.ubtechinc.goldenpig.pigmanager.register.GetPigListHttpProxy;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.PigUtils;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.USER_PIG_UPDATE;

public class DeviceManageActivity extends BaseToolBarActivity implements View.OnClickListener {

    TextView memberItemTitle;

    TextView memberItemSubTitle;

    View rlMyPig;

    View rlPairing;

    View rlMemberGroup;

    View rlAddressbook;

    private PigInfo mPig;

    private UBTSubTitleDialog dialog;

    @Override
    protected int getConentView() {
        return R.layout.activity_device_manage;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitleBack(true);
        setToolBarTitle(getString(R.string.device_manage));
        initView();
    }

    private void initView() {
        EventBusUtil.register(this);
        memberItemTitle = findViewById(R.id.ubt_tv_member_group);
        memberItemSubTitle = findViewById(R.id.ubt_tv_member_subtitle);
        rlMyPig = findViewById(R.id.rl_my_pig);
        rlPairing = findViewById(R.id.rl_pairing);
        rlMemberGroup = findViewById(R.id.rl_member_group);
        rlAddressbook = findViewById(R.id.rl_addressbook);

        rlMyPig.setOnClickListener(this);
        rlPairing.setOnClickListener(this);
        rlMemberGroup.setOnClickListener(this);
        rlAddressbook.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
        updatePigList();
    }

    private void updatePigList() {
        new GetPigListHttpProxy().getUserPigs(CookieInterceptor.get().getToken(), BuildConfig.APP_ID, "", new GetPigListHttpProxy.OnGetPigListLitener() {
            @Override
            public void onError(ThrowableWrapper e) {
                Log.e("getPigList", e.getMessage());
            }

            @Override
            public void onException(Exception e) {
                Log.e("getPigList", e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showShortToast("网络异常");
                    }
                });
            }

            @Override
            public void onSuccess(String response) {
                Log.e("getPigList", response);
                PigUtils.getPigList(response, AuthLive.getInstance().getUserId(), AuthLive.getInstance().getCurrentPigList());
            }
        });
    }

    private void updateUI() {
        mPig = AuthLive.getInstance().getCurrentPig();
        if (mPig != null) {
            if (mPig.isAdmin) {
                rlMyPig.setAlpha(1.0f);
                rlPairing.setAlpha(1.0f);
                rlMemberGroup.setAlpha(1.0f);
                rlAddressbook.setAlpha(1.0f);
                memberItemTitle.setText(R.string.member_group);
                memberItemSubTitle.setVisibility(View.GONE);

                rlMyPig.setEnabled(true);
                rlPairing.setEnabled(true);
                rlMemberGroup.setEnabled(true);
                rlAddressbook.setEnabled(true);
            } else {
                rlMyPig.setAlpha(1.0f);
                rlPairing.setAlpha(0.5f);
                rlMemberGroup.setAlpha(1.0f);
                rlAddressbook.setAlpha(0.5f);
                memberItemTitle.setText(R.string.member_group);
                memberItemSubTitle.setVisibility(View.GONE);

                rlMyPig.setEnabled(true);
                rlPairing.setEnabled(false);
                rlMemberGroup.setEnabled(true);
                rlAddressbook.setEnabled(false);
            }
        } else {
            rlMyPig.setAlpha(0.5f);
            rlPairing.setAlpha(0.5f);
            rlMemberGroup.setAlpha(1.0f);
            rlAddressbook.setAlpha(0.5f);
            memberItemTitle.setText(R.string.ubt_join_group);
            memberItemSubTitle.setVisibility(View.VISIBLE);
            memberItemSubTitle.setText(R.string.ubt_san_formember);

            rlMyPig.setEnabled(false);
            rlPairing.setEnabled(false);
            rlMemberGroup.setEnabled(true);
            rlAddressbook.setEnabled(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_my_pig:
                ActivityRoute.toAnotherActivity(DeviceManageActivity.this, MyPigActivity
                        .class, false);
                break;
            case R.id.rl_pairing:
                String pairSerialNumber = UBTPGApplication.mPairSerialNumber;
                if (!TextUtils.isEmpty(pairSerialNumber)) {
                    //TODO 配对列表
                    HashMap<String, String> map = new HashMap<>();
                    map.put("pairSerialNumber", String.valueOf(pairSerialNumber));
                    ActivityRoute.toAnotherActivity(this, PairPigActivity.class, map, false);
                } else {
                    HashMap<String, Boolean> param = new HashMap<>();
                    param.put("isPair", true);
                    ActivityRoute.toAnotherActivity(this, QRCodeActivity.class, param, false);
                }
                break;
            case R.id.rl_member_group:
                if (AuthLive.getInstance().getCurrentPig() == null) {
                    goToMemberQRScannerActivity();
                } else {
                    ActivityRoute.toAnotherActivity(this, PigMemberActivity.class, false);
                }
                break;
            case R.id.rl_addressbook:
                ActivityRoute.toAnotherActivity(DeviceManageActivity.this, AddressBookActivity
                        .class, false);
                break;
            default:
        }
    }

    private void goToMemberQRScannerActivity() {
        if (Build.VERSION.SDK_INT >= 23) {
            AndPermission.with(this)
                    .requestCode(0x1101)
                    .permission(Permission.CAMERA)
                    .callback(new PermissionListener() {
                        @Override
                        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                            ActivityRoute.toAnotherActivity(DeviceManageActivity.this, MemberQRScannerActivity.class, Constants.QR_PAIR_PIG_REQUEST, false);
                        }

                        @Override
                        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                            showPermissionDialog(Permission.CAMERA);
                        }
                    })
                    .rationale((requestCode, rationale) -> rationale.resume())
                    .start();

        } else {
            if (cameraIsCanUse()) {
                ActivityRoute.toAnotherActivity(DeviceManageActivity.this, MemberQRScannerActivity.class, Constants.QR_PAIR_PIG_REQUEST, false);
            } else {
                showPermissionDialog(Permission.CAMERA);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event) {
        if (event == null) return;
        int code = event.getCode();
        switch (code) {
            case USER_PIG_UPDATE:
                updateUI();
                break;
        }
    }


}
