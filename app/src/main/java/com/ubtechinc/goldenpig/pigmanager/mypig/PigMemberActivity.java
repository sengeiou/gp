package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtechinc.commlib.utils.ToastUtils;
import com.ubtechinc.commlib.view.SpaceItemDecoration;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.comm.view.WrapContentLinearLayoutManager;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.net.CheckBindRobotModule;
import com.ubtechinc.goldenpig.personal.management.AddAndSetContactActivity;
import com.ubtechinc.goldenpig.personal.management.AddressBookActivity;
import com.ubtechinc.goldenpig.pigmanager.adpater.PigMemberAdapter;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.register.CheckUserRepository;
import com.ubtechinc.goldenpig.pigmanager.register.UnbindMemberHttpProxy;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :成员组管理
 *@time          :2018/9/19 21:11
 *@change        :
 *@changetime    :2018/9/19 21:11
*/
public class PigMemberActivity extends BaseToolBarActivity implements View.OnClickListener{
    private SwipeMenuRecyclerView  mMemberRcy;
    private PigMemberAdapter adapter;
    private Button mUnbindBtn;
    private PigInfo mPig;
    private ArrayList<CheckBindRobotModule.User> mUsertList=new ArrayList<>();
    @Override
    protected int getConentView() {
        return R.layout.activity_pigmember;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitleBack(true);
        setToolBarTitle(getString(R.string.ubt_menber_group));
        initViews();

        getMember();
        initData();
    }
    private void initData(){
        CheckBindRobotModule.User user=new CheckBindRobotModule().new User();
        user.setNickName("哈哈哈哈哈");
        mUsertList.add(user);
        CheckBindRobotModule.User user2=new CheckBindRobotModule().new User();
        user2.setNickName("嘻嘻嘻嘻嘻");
        mUsertList.add(user2);
        CheckBindRobotModule.User user3=new CheckBindRobotModule().new User();
        user3.setNickName("哦哦哦哦哦");
        mUsertList.add(user3);

        adapter.notifyDataSetChanged();
    }
    private void initViews(){
        mMemberRcy=findViewById(R.id.ubt_rcy_member);
        mUnbindBtn=findViewById(R.id.ubt_btn_unbind_member);
        mUnbindBtn.setOnClickListener(this);

        mPig=AuthLive.getInstance().getCurrentPig();

        adapter=new PigMemberAdapter(this,mUsertList);
        mMemberRcy.setLayoutManager(new WrapContentLinearLayoutManager(this));
        mMemberRcy.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelOffset(R.dimen.dp_5),false));
        mMemberRcy.setSwipeMenuCreator(swipeMenuCreator);
        mMemberRcy.setSwipeMenuItemClickListener(mMenuItemClickListener);
        mMemberRcy.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        setAddBtnEnable(isCurrentAdmin());

    }

    private boolean isCurrentAdmin(){
        return true;
    }
    private void getMember(){
        if (mPig==null) {
            ToastUtils.showShortToast(this, getString(R.string.ubt_no_pigs));
            return;
        }
        CheckUserRepository repository=new CheckUserRepository();
        repository.getRobotBindUsers(mPig.getRobotName(), CookieInterceptor.get().getToken(), BuildConfig.APP_ID, new CheckUserRepository.ICheckBindStateCallBack() {
            @Override
            public void onError(ThrowableWrapper e) {
                ToastUtils.showShortToast(PigMemberActivity.this, "获取成员列表失败");
            }

            @Override
            public void onSuccess(CheckBindRobotModule.Response response) {

            }

            @Override
            public void onSuccessWithJson(String jsonStr) {

            }
        });
    }
    private void setAddBtnEnable(boolean isEnable){
        if (isEnable){
            mAddBtn=findViewById(R.id.ubt_imgbtn_add);
            mAddBtn.setVisibility(View.VISIBLE);
            mAddBtn.setOnClickListener(this);
        }else {
            findViewById(R.id.ubt_imgbtn_add).setVisibility(View.GONE);
        }
    }
    private void doUnbind(String userId){
        if (mPig==null)
            return;
        UnbindMemberHttpProxy proxy=new UnbindMemberHttpProxy();
        proxy.doUnbind(BuildConfig.APP_ID, CookieInterceptor.get().getToken(), mPig.getRobotName(), userId, new UnbindMemberHttpProxy.UnbindCallBack() {
            @Override
            public void onError(String err) {

            }

            @Override
            public void onSuccess() {

            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ubt_imgbtn_add:
                ActivityRoute.toAnotherActivity(this,AddMemberActivity.class,false);
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
                SwipeMenuItem addItem = new SwipeMenuItem(PigMemberActivity.this)
                        .setBackgroundColor(getResources().getColor(R.color
                                .ubt_tab_btn_txt_checked_color))
                        .setText(R.string.ubt_trans_admin)
                        .setTextColor(Color.WHITE)
                        .setTextSize(15)
                        .setWidth(getResources().getDimensionPixelSize(R.dimen.dp_88))
                        .setHeight(height);
                swipeRightMenu.addMenuItem(addItem); // 添加菜单到右侧。
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
                if (menuPosition == 0) {

                } else if (menuPosition == 1) {

                }
            }
        }
    };
}
