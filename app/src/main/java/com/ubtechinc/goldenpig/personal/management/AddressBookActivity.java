package com.ubtechinc.goldenpig.personal.management;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewImg;
import com.ubtechinc.goldenpig.model.AddressBookmodel;
import com.ubtechinc.goldenpig.mvp.MVPBaseActivity;
import com.ubtechinc.goldenpig.view.Divider;
import com.ubtechinc.goldenpig.view.swipe_menu.SwipeMenuLayout;
import com.ubtechinc.goldenpig.view.swipe_menu.SwipeRecycleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class AddressBookActivity extends MVPBaseActivity<AddressBookContract.View,
        AddressBookPrestener> implements OnRefreshListener, AddressBookContract.View {
    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewImg rl_titlebar;
    @BindView(R.id.recycler)
    SwipeRecycleView recycler;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    AddressBookAdapter adapter;
    private List<AddressBookmodel> mList;
    Handler mHandler = new Handler();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_address_book;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rl_titlebar.setTitleText(getString(R.string.address_book));
        rl_titlebar.setLeftOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rl_titlebar.setIvRight(R.drawable.ic_add);
        rl_titlebar.setRightOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setOnRefreshListener(this);
        mList = new ArrayList<>();
        // adapter = new AddressBookAdapter(this, mList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setHasFixedSize(true);
        Divider divider = new Divider(new ColorDrawable(getResources().getColor(R.color
                .ubt_wifi_list_divider)),
                OrientationHelper.VERTICAL);
        divider.setHeight((int) getResources().getDimension(R.dimen.ubt_1px));
        recycler.addItemDecoration(divider);
        adapter = new AddressBookAdapter(this, mList);
        recycler.setAdapter(adapter);
//        recycler.setAdapter(adapter = new BaseQuickAdapter<AddressBookmodel, BaseViewHolder>(
//                R.layout.adapter_addressbook, mList) {
//            @Override
//            protected void convert(BaseViewHolder helper, AddressBookmodel item) {
//                helper.setText(R.id.tv_content, item.name);
//                helper.setOnClickListener(R.id.tv_set, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (((SwipeMenuLayout) helper.getView(R.id.swipe_menu)).isMenuOpen()) {
//                            ((SwipeMenuLayout) helper.getView(R.id.swipe_menu))
// .smoothToCloseMenu();
//                        }
//                        Toast.makeText(mContext, "编辑", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                helper.setOnClickListener(R.id.tv_delete, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (((SwipeMenuLayout) helper.getView(R.id.swipe_menu)).isMenuOpen()) {
//                            ((SwipeMenuLayout) helper.getView(R.id.swipe_menu))
// .smoothToCloseMenu();
//                        }
//                        mList.remove(helper.getPosition());
//                        notifyDataSetChanged();
//                    }
//                });
//            }
//        });
        refreshLayout.autoRefresh();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mPresenter.refreshData();
    }

    @Override
    public void onRefreshSuccess(List<AddressBookmodel> list) {
        refreshLayout.finishRefresh(true);
        mList.clear();
        mList.addAll(list);
        if (mList.size() >= 10) {
            AddressBookmodel ab = new AddressBookmodel();
            ab.type = 1;
            mList.add(ab);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public Handler getHandler() {
        return mHandler;
    }

//    @OnClick({R.id.rl_my_pig, R.id.rl_pairing, R.id.rl_member_group, R.id.rl_addressbook})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.rl_my_pig:
//                break;
//            case R.id.rl_pairing:
//                break;
//            case R.id.rl_member_group:
//                break;
//            case R.id.rl_addressbook:
//                break;
//        }
//    }
}
