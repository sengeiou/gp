package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ubtechinc.commlib.utils.ToastUtils;
import com.ubtechinc.commlib.view.SpaceItemDecoration;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.comm.view.WrapContentLinearLayoutManager;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.comm.widget.UBTBaseDialog;
import com.ubtechinc.goldenpig.comm.widget.UBTSubTitleDialog;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.net.CheckBindRobotModule;
import com.ubtechinc.goldenpig.pigmanager.adpater.PigMemberAdapter;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.register.CheckUserRepository;
import com.ubtechinc.goldenpig.pigmanager.register.GetPigListHttpProxy;
import com.ubtechinc.goldenpig.pigmanager.register.TransferAdminHttpProxy;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.PigUtils;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :成员组管理
 * @time :2018/9/19 21:11
 * @change :
 * @changetime :2018/9/19 21:11
 */
public class PigMemberActivity extends BaseToolBarActivity implements View.OnClickListener {
    private SwipeMenuRecyclerView mMemberRcy;
    private PigMemberAdapter adapter;
    private Button mUnbindBtn;
    private PigInfo mPig;
    private ArrayList<CheckBindRobotModule.User> mUsertList = new ArrayList<>();
    private boolean isDownloadedUserList;
    private UnbindPigProxy.UnBindPigCallback unBindPigCallback;

    @Override
    protected int getConentView() {
        return R.layout.activity_pigmember;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitleBack(true);
        setToolBarTitle(getString(R.string.ubt_menber_group));
        initViews();

        getMember("1");
        initData();
    }

    private void initData() {
        unBindPigCallback = new UnbindPigProxy.UnBindPigCallback() {
            @Override
            public void onError(IOException e) {

            }

            @Override
            public void onSuccess(String reponse) {
                if (!TextUtils.isEmpty(reponse)) {
                    try {
                        JSONObject jsonObject = new JSONObject(reponse);
                        int code = jsonObject.has("code") ? jsonObject.getInt("code") : -1;

                        if (code == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.showShortToast(PigMemberActivity.this, R.string.ubt_ubbind_success);
                                    new GetPigListHttpProxy().getUserPigs(CookieInterceptor.get().getToken(), BuildConfig.APP_ID, "", null);
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    finish();
                                }
                            });
                        } else {
                            final String msg = jsonObject.has("message") ? jsonObject.getString("message") : "返回的结果格式错误";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.showShortToast(PigMemberActivity.this, msg);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
    }

    private void initViews() {
        mMemberRcy = findViewById(R.id.ubt_rcy_member);
        mUnbindBtn = findViewById(R.id.ubt_btn_unbind_member);
        mUnbindBtn.setOnClickListener(this);

        mPig = AuthLive.getInstance().getCurrentPig();

        adapter = new PigMemberAdapter(this, mUsertList);
        mMemberRcy.setLayoutManager(new WrapContentLinearLayoutManager(this));
        mMemberRcy.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelOffset(R.dimen.dp_5), false));
        mMemberRcy.setSwipeMenuCreator(swipeMenuCreator);
        mMemberRcy.setSwipeMenuItemClickListener(mMenuItemClickListener);
        mMemberRcy.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        setAddBtnEnable(isCurrentAdmin());

    }

    private boolean isCurrentAdmin() {
        if (mUsertList != null) {
            for (CheckBindRobotModule.User user : mUsertList) {
                if (user.getIsAdmin() == 1 && user.getUserId() == Integer.valueOf(AuthLive.getInstance().getCurrentUser().getUserId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void getMember(String admin) {
        if (isDownloadedUserList) {
            return;
        }
        if ("0".equals(admin)) {
            isDownloadedUserList = true;
        }
        if (mPig == null) {
            ToastUtils.showShortToast(this, getString(R.string.ubt_no_pigs));
            return;
        }
        if (mUsertList == null) {
            mUsertList = new ArrayList<>();
        } else if ("1".equals(admin) && mUsertList != null) {
            mUsertList.clear();
        }
        CheckUserRepository repository = new CheckUserRepository();
        repository.getRobotBindUsers(mPig.getRobotName(), CookieInterceptor.get().getToken(), BuildConfig.APP_ID, admin, new CheckUserRepository.ICheckBindStateCallBack() {
            @Override
            public void onError(ThrowableWrapper e) {
                ToastUtils.showShortToast(PigMemberActivity.this, "获取成员列表失败");
            }

            @Override
            public void onSuccess(CheckBindRobotModule.Response response) {
                ToastUtils.showShortToast(PigMemberActivity.this, "获取成员列表成功");
            }

            @Override
            public void onSuccessWithJson(String jsonStr) {
                LoadingDialog.getInstance(PigMemberActivity.this).dismiss();
                final List<CheckBindRobotModule.User> bindUsers = jsonToUserList(jsonStr);
                if (mUsertList != null) {
                    mUsertList.addAll(bindUsers);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
                getMember("0");
            }
        });
    }

    private void setAddBtnEnable(boolean isEnable) {
        if (isEnable) {
            mToolbarRightBtn = findViewById(R.id.ubt_imgbtn_add);
            mToolbarRightBtn.setVisibility(View.VISIBLE);
            mToolbarRightBtn.setOnClickListener(this);
        } else {
            findViewById(R.id.ubt_imgbtn_add).setVisibility(View.GONE);
        }
    }

    private void doUnbind(final String userId) {
        if (mPig == null) {
            return;
        }
        ///操作用户是唯一或只是一般成员可好直接弹框点击确认退出
        //否则要跳转到权限转让界面操作
        if (mUsertList.size() > 1 && isCurrentAdmin()) {
//            HashMap<String, ArrayList<CheckBindRobotModule.User>> param = new HashMap<>();
//            param.put("users", mUsertList);
//            ActivityRoute.toAnotherActivity(this, TransferAdminActivity.class, param, false);

            UBTSubTitleDialog dialog = new UBTSubTitleDialog(this);
            dialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tab_btn_txt_checked_color, null));
            dialog.setTips(getString(R.string.ubt_exit_group_tips));
            dialog.setLeftButtonTxt(getString(R.string.ubt_cancel));
            dialog.setRightButtonTxt(getString(R.string.ubt_enter));
            dialog.setSubTips(getString(R.string.ubt_transfer_tips));
            dialog.setOnUbtDialogClickLinsenter(new UBTSubTitleDialog.OnUbtDialogClickLinsenter() {
                @Override
                public void onLeftButtonClick(View view) {

                }

                @Override
                public void onRightButtonClick(View view) {
                }
            });
            dialog.show();

        } else {
            UBTBaseDialog dialog = new UBTBaseDialog(this);
            dialog.setRightButtonTxt(getString(R.string.ubt_enter));
            dialog.setLeftButtonTxt(getString(R.string.ubt_cancel));
            dialog.setTips(getString(R.string.ubt_drop_up_tips));

            dialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tab_btn_txt_checked_color, null));
            dialog.setOnUbtDialogClickLinsenter(new UBTBaseDialog.OnUbtDialogClickLinsenter() {
                @Override
                public void onLeftButtonClick(View view) {

                }

                @Override
                public void onRightButtonClick(View view) {
                    UnbindPigProxy pigProxy = new UnbindPigProxy();
                    final String serialNo = AuthLive.getInstance().getCurrentPig().getRobotName();

                    final String token = CookieInterceptor.get().getToken();
                    pigProxy.unbindPig(serialNo, userId, token, BuildConfig.APP_ID, unBindPigCallback);
                }
            });
            dialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ubt_imgbtn_add:
                HashMap<String, Boolean> param = new HashMap<>();
                param.put("isPair", false);
                ActivityRoute.toAnotherActivity(this, QRCodeActivity.class, param, false);
                break;
            case R.id.ubt_btn_unbind_member:
                doUnbind(AuthLive.getInstance().getUserId());
                break;
        }

    }

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            if (viewType == 1) {
                return;
            }
            int width = getResources().getDimensionPixelSize(R.dimen.dp_65);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem transferItem = new SwipeMenuItem(PigMemberActivity.this)
                        .setBackgroundColor(getResources().getColor(R.color
                                .ubt_tab_btn_txt_checked_color))
                        .setText(R.string.ubt_trans_admin)
                        .setTextColor(Color.WHITE)
                        .setTextSize(15)
                        .setWidth(getResources().getDimensionPixelSize(R.dimen.dp_88))
                        .setHeight(height);
                swipeRightMenu.addMenuItem(transferItem); // 添加菜单到右侧。
                SwipeMenuItem deleteItem = new SwipeMenuItem(PigMemberActivity.this)
                        .setBackground(getResources().getDrawable(R.drawable.shape_ubt_member_menu_bg))
                        .setText(R.string.ubt_delete)
                        .setTextColor(Color.WHITE)
                        .setTextSize(15)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。

            }
        }
    };
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                if (mUsertList != null && adapterPosition > -1 && adapterPosition < mUsertList.size()) {
                    if (menuPosition == 1) {
                        showDeleteMember(String.valueOf(mUsertList.get(adapterPosition).getUserId()));
                    } else if (menuPosition == 0) {
                        showTransferAdminDialog(String.valueOf(mUsertList.get(adapterPosition).getUserId()));
                    }
                }
            }
        }
    };

    /**
     * 显示删除成员确定框
     *
     * @param userId 用户ID
     */
    private void showDeleteMember(final String userId) {
        //doUnbind(String.valueOf(userId);
        UBTBaseDialog dialog = new UBTBaseDialog(this);
        dialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tab_btn_txt_checked_color, null));
        dialog.setTips(getString(R.string.ubt_delte_member_tips));
        dialog.setLeftButtonTxt(getString(R.string.ubt_cancel));
        dialog.setRightButtonTxt(getString(R.string.ubt_enter));
        dialog.setOnUbtDialogClickLinsenter(new UBTBaseDialog.OnUbtDialogClickLinsenter() {
            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                UnbindPigProxy pigProxy = new UnbindPigProxy();
                final String serialNo = AuthLive.getInstance().getCurrentPig().getRobotName();

                final String token = CookieInterceptor.get().getToken();
                pigProxy.unbindPig(serialNo, userId, token, BuildConfig.APP_ID, unBindPigCallback);
            }
        });
        dialog.show();
    }

    /**
     * 显示转让权限确认对话框
     */
    private void showTransferAdminDialog(final String userId) {
        UBTSubTitleDialog dialog = new UBTSubTitleDialog(this);
        dialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tab_btn_txt_checked_color, null));
        dialog.setTips(getString(R.string.ubt_trandfer_admin_tips));
        dialog.setLeftButtonTxt(getString(R.string.ubt_cancel));
        dialog.setRightButtonTxt(getString(R.string.ubt_enter));
        dialog.setSubTips(getString(R.string.ubt_transfer_tips));
        dialog.setOnUbtDialogClickLinsenter(new UBTSubTitleDialog.OnUbtDialogClickLinsenter() {
            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                //TODO do管理员权限转让
                doTransferAdmin(userId);
            }
        });
        dialog.show();
    }

    /**
     * 执行转让权限操作
     */
    private void doTransferAdmin(String userId) {
        LoadingDialog.getInstance(this).show();
        TransferAdminHttpProxy httpProxy = new TransferAdminHttpProxy();
        httpProxy.transferAdmin(this, CookieInterceptor.get().getToken(), AuthLive.getInstance().getCurrentPig().getRobotName(), userId, new TransferAdminHttpProxy.TransferCallback() {
            @Override
            public void onError(String error) {
                LoadingDialog.getInstance(PigMemberActivity.this).dismiss();
                com.ubtech.utilcode.utils.ToastUtils.showShortToast("转让失败");
            }

            @Override
            public void onException(Exception e) {
                LoadingDialog.getInstance(PigMemberActivity.this).dismiss();
                com.ubtech.utilcode.utils.ToastUtils.showShortToast("转让失败");
            }

            @Override
            public void onSuccess(String msg) {
                com.ubtech.utilcode.utils.ToastUtils.showShortToast("转让成功");
                isDownloadedUserList = false;
                updatePigList();
                getMember("1");
            }
        });
    }

    private void updatePigList() {
        new GetPigListHttpProxy().getUserPigs(CookieInterceptor.get().getToken(), BuildConfig.APP_ID, "", new GetPigListHttpProxy.OnGetPigListLitener() {
            @Override
            public void onError(ThrowableWrapper e) {
                Log.e("getPigList",e.getMessage());
            }

            @Override
            public void onException(Exception e) {
                Log.e("getPigList",e.getMessage());
            }

            @Override
            public void onSuccess(String response) {
                Log.e("getPigList",response);
                PigUtils.getPigList(response,AuthLive.getInstance().getUserId(),AuthLive.getInstance().getCurrentPigList());
            }
        });
    }

    private List<CheckBindRobotModule.User> jsonToUserList(String jsonStr) {
        List<CheckBindRobotModule.User> result = null;
        Gson gson = new Gson();
        try {
            result = gson.fromJson(jsonStr, new TypeToken<List<CheckBindRobotModule.User>>() {
            }.getType());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return result;
    }
}
