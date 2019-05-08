package com.ubtechinc.goldenpig.creative;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.utils.SharedPreferencesUtils;
import com.ubtechinc.goldenpig.view.ViewPagerSlide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

public class CreateActivity extends BaseNewActivity {
    @BindView(R.id.viewPager)
    ViewPagerSlide viewPager;
    @BindView(R.id.iv_add)
    ImageView iv_add;
    @BindView(R.id.tv_add)
    TextView tv_add;
    @BindView(R.id.ll_add)
    LinearLayout ll_add;
    @BindViews({R.id.tv_list, R.id.tv_cache, R.id.tv_introduc})
    List<TextView> viewList;
    List<Integer> intRecoure = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private MyPagerAdapter adapter;
    private List<String> datas = new ArrayList<>();
    public int selPosition = 0;
    public boolean isIntroduce = false;

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_create;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UBTPGApplication.getInstance().createActivity = true;
        intRecoure.clear();
        intRecoure.add(R.drawable.rect__round_bg_create1);
        intRecoure.add(R.drawable.rect__round_bg_create2);
        intRecoure.add(R.drawable.rect__round_bg_create3);
        datas.clear();
        datas.add("已提交");
        datas.add("草稿");
        datas.add("玩法说明");
        fragments.clear();
        fragments.add(new CreateListFragment());
        fragments.add(new CreateCacheFragment());
        fragments.add(new CreateIntroduceFragment());
        adapter = new MyPagerAdapter(getSupportFragmentManager(), datas, fragments);
        viewPager.setSlide(true);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                viewList.get(selPosition).setTextColor(getResources().getColor(R.color.ubt_white));
                viewList.get(selPosition).setBackground(null);
                viewList.get(position).setTextColor(getResources().getColor(R.color
                        .pic_239fed_color));
                viewList.get(position).setBackgroundResource(intRecoure.get(position));
                selPosition = position;
                try {
                    ((CreateListFragment) fragments.get(0)).checkGuide();
                } catch (Exception e) {
                }

                if (selPosition == 2) {
                    isIntroduce = true;
//                    SCADAHelper.recordEvent(SCADAHelper.EVENET_APP_ZCQA_TUTORIAL_ENTER);
                } else if (isIntroduce) {
                    isIntroduce = false;
//                    SCADAHelper.recordEvent(SCADAHelper.EVENET_APP_ZCQA_TUTORIAL_QUIT);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        if (isAdmin()) {
            iv_add.setImageResource(R.drawable.ic_add);
            tv_add.setTextColor(getResources().getColor(R.color.pic_remoind_main_color));
            ll_add.setBackgroundColor(getResources().getColor(R.color.ubt_white));
        } else {
            iv_add.setImageResource(R.drawable.ic_add2);
            tv_add.setTextColor(getResources().getColor(R.color.create_color));
            ll_add.setBackgroundColor(getResources().getColor(R.color.add_create_color));
        }
        //是否第一次打开
        if (!UBTPGApplication.getInstance().HASCREATE) {
            try {
                UBTPGApplication.getInstance().HASCREATE = true;
                SharedPreferencesUtils.putBoolean(this, SharedPreferencesUtils.CREATE, true);
                viewPager.setCurrentItem(2);
            } catch (Exception e) {
            }
        }
    }

    private boolean isAdmin(){

        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if(pigInfo != null && pigInfo.isAdmin){
            return true;
        }else{
            return false;
        }
    }




    @OnClick({R.id.iv_left, R.id.tv_list, R.id.tv_cache, R.id.ll_add, R.id.tv_introduc})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_list:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tv_cache:
                viewPager.setCurrentItem(1);
                break;
            case R.id.ll_add:
                if (isAdmin()) {
                    Intent it = new Intent(CreateActivity.this, AddCreateActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.tv_introduc:
                viewPager.setCurrentItem(2);
//                Intent it1 = new Intent(CreateActivity.this, CreateIntroduceActivity.class);
//                it1.putExtra("type", 1);
//                startActivity(it1);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UBTPGApplication.getInstance().createActivity = false;
        if (selPosition == 2) {
//            SCADAHelper.recordEvent(SCADAHelper.EVENET_APP_ZCQA_TUTORIAL_QUIT);
        }
    }

    @Override
    protected void onReceiveEvent(Event event) {
        super.onReceiveEvent(event);
        if (event.getCode() == EventBusUtil.SHOWCREATELIST) {
            if (viewPager != null) {
                try {
                    viewPager.setCurrentItem(0);
                } catch (Exception e) {
                }
            }
        } else if (event.getCode() == EventBusUtil.SHOWCREATECACHE) {

        }
    }

    public int getSelPosition() {
        return selPosition;
    }
}