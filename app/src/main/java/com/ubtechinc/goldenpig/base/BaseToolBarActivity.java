package com.ubtechinc.goldenpig.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;


import com.ubtechinc.goldenpig.R;

/**
 * @author     : HQT
 * @email      :qiangta.huang@ubtrobot.com
 * @describe   :基础带ToolBar Activity
 * @time       :2018/8/17 17:59
 * @change     :
 * @changTime  :2018/8/17 17:59
 */
public abstract  class BaseToolBarActivity extends BaseActivity {
    private String menuStr;
    private int menuResId;
    private String menuStr2;
    private int menuResId2;
    private TextView tvTitle;
    private FrameLayout viewContent;
    private Toolbar toolbar;
    private TextView mNotifyTv; ///动态提示
    protected TextView mTvSkip;              //跳过按钮
    protected ImageButton mToolbarRightBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //1、设置支出，并不显示项目的title文字
        toolbar = (Toolbar) findViewById(R.id.ubt_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//返回
            }
        });
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        //2、将子类的布局解析到 FrameLayout 里面
        viewContent = (FrameLayout) findViewById(R.id.ubt_toolbar_contentview);
        if (getConentView()>0) {
            LayoutInflater.from(this).inflate(getConentView(), viewContent);
        }
        //3、初始化操作（此方法必须放在最后执行位置）
        init(savedInstanceState);


    }
    @Override
    protected int getContentViewId() {
        return R.layout.activity_base_toolbar;
    }
    protected void showNotify(String notifyTips){
        if (mNotifyTv==null){
            mNotifyTv=new TextView(this);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.TOP;
            mNotifyTv.setPadding(0,10,0,10);
            mNotifyTv.setBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.ubt_notify_tv_bg_color,null));
            mNotifyTv.setTextColor(ResourcesCompat.getColor(getResources(),R.color.ubt_dialog_btn_txt_color,null));
            mNotifyTv.setLayoutParams(lp);
            mNotifyTv.setGravity(Gravity.CENTER);
        }
        mNotifyTv.setText(notifyTips);
        mNotifyTv.setVisibility(View.VISIBLE);
        if (viewContent!=null||mNotifyTv.getParent()==null){
            try {
                viewContent.removeView(mNotifyTv);
            }catch (Exception e){

            }
            try {
                viewContent.addView(mNotifyTv); //index是0，表示添加的child在linearlayout顶部，-1为底部
            }catch (Exception e){

            }
        }
    }
    protected void setToolBarBackground(int color){
        if (toolbar!=null){
            toolbar.setBackgroundColor(color);
        }
    }
    protected void setSkipEnable(boolean enable){
        if (mTvSkip!=null){
            mTvSkip.setEnabled(enable);
        }
    }
    protected void hideNotify(){
        if (mNotifyTv!=null &&viewContent!=null){
            mNotifyTv.setVisibility(View.GONE);

        }
    }
    /**
     * 设置布局资源
     *
     * @return
     */
    protected abstract int getConentView();

    /**
     * 初始化操作
     *
     * @param savedInstanceState
     */
    protected abstract void init(Bundle savedInstanceState);

    /**
     * 设置页面标题
     *
     * @param title 标题文字
     */
    protected void setToolBarTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            tvTitle = (TextView)findViewById(R.id.ubt_tv_toolbar_title);

            tvTitle.setText(title);
        }
    }
    protected void setToolBarTitle(int titleId) {
         String title=getString(titleId);
        setToolBarTitle(title);
    }
    /**
     * 设置显示返回按钮
     */
    protected void setTitleBack(boolean visible) {
        if (visible) {
            //toolbar.setNavigationIcon(R.drawable.dingdang_btn_back_holo_light);//设置返回按钮
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }
    /**设置右侧按钮文字***/
    protected void setToolBarRightBtn(String title) {
        if (!TextUtils.isEmpty(title)) {

        }
    }
    protected void setToolBarRightBtn(int titleId) {
        String title=getString(titleId);
        setTitle(title);
    }
    /**
     * 设置标题栏右键按钮事件
     *
     * @param menuStr 文字
     * @param menuResId 图片icon
     * @param onClickListener 事件响应
     */
    protected void setToolBarMenuOne(String menuStr, int menuResId, OnClickRightListener onClickListener) {
        this.onClickRightListener = onClickListener;
        this.menuStr = menuStr;
        this.menuResId = menuResId;
    }
    protected void setToolBarMenuTwo(String menuStr, int menuResId, OnClickRightListener onClickListener) {
        this.onClickRightListener2 = onClickListener;
        this.menuStr2 = menuStr;
        this.menuResId2 = menuResId;
    }
    /**
     * 设置拦截事件处理业务逻辑
     *
     * @param item 自定义菜单项
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                break;
            case R.id.menu_item_one:
                this.onClickRightListener.onClick();

                break;
            case R.id.menu_item_two:
                this.onClickRightListener2.onClick();

                break;
        }
        return true;//拦截系统处理事件
    }

    /**
     * 加载Toolbar标题右菜单图标和文字
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menuResId != 0 || !TextUtils.isEmpty(menuStr)) {//显示自定义右菜单
            getMenuInflater().inflate(R.menu.ubt_toolbar, menu);
        }else if (menuResId2 != 0 || !TextUtils.isEmpty(menuStr2)){
            getMenuInflater().inflate(R.menu.ubt_toolbar, menu);
        }else {

            //如果把下面这行代码表示右侧菜单显示默认值。
            //显示的默认Menu、Item里的值必须在menu文件中配置好文字和icon。
//            getMenuInflater().inflate(R.menu.toobar, menu);
        }
        return true;
    }

    /**
     * 选择性显示图标或文字
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem=menu.findItem(R.id.menu_item_one);
        if (menuItem!=null) {
            if (menuResId != 0) {
                menuItem.setIcon(menuResId);
            } else if (!TextUtils.isEmpty(menuStr)) {
                menuItem.setTitle(menuStr);
            } else {
                menuItem.setVisible(false);
            }
        }
        menuItem=menu.findItem(R.id.menu_item_two);
        if (menuItem!=null) {
            if (menuResId2 != 0) {
                menu.findItem(R.id.menu_item_two).setIcon(menuResId2);
            } else if (!TextUtils.isEmpty(menuStr)) {
                menu.findItem(R.id.menu_item_two).setTitle(menuStr2);
            } else {
                menu.findItem(R.id.menu_item_two).setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private OnClickRightListener onClickRightListener;
    private OnClickRightListener onClickRightListener2;

    public interface OnClickRightListener {
        void onClick();
    }
}
