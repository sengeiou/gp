package com.ubtechinc.goldenpig.creative;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseNewFragment;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.model.CreateModel;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.popup.PopupWindowList;
import com.ubtechinc.goldenpig.view.RecyclerItemClickListener;
import com.ubtechinc.goldenpig.view.RecyclerOnItemLongListener;
import com.ubtechinc.goldenpig.view.TopDivider;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.ADD_CREATE;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SAVE_CREATE_CACHE;


public class CreateCacheFragment extends BaseNewFragment {
    private static final String TAG = CreateCacheFragment.class.getSimpleName();
    Gson gson = new Gson();
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.rl_layout)
    RelativeLayout rl_layout;
    CreateAdapter adapter;
    private ArrayList<CreateModel> mList;

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fra_create_list;
    }

    @Override
    protected void initView() {
        initStateView(rl_layout, false);
        mStateView.setEmptyResource(R.layout.adapter_create_empty);
        mList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setHasFixedSize(true);
        TopDivider divider = new TopDivider(new ColorDrawable(getResources().getColor(R.color
                .ubt_main_bg_color)),
                OrientationHelper.VERTICAL);
        divider.setHeight((int) getResources().getDimension(R.dimen.dp_12));
        recycler.addItemDecoration(divider);
        adapter = new CreateAdapter(getActivity(), mList, new RecyclerOnItemLongListener() {
            @Override
            public void onItemLongClick(View v, int position) {
                showPopWindows(v, position);
                mList.get(position).select = 1;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemClick(View v, int position) {
                if (isAdmin()) {
                    Intent it = new Intent(getActivity(), AddCreateActivity.class);
                    it.putExtra("strQuest", mList.get(position).question);
                    it.putExtra("strAnswer", mList.get(position).answer);

                    it.putExtra("position", position);
                    startActivity(it);
                } else {
                    Intent it = new Intent(getActivity(), SeeCreateActivity.class);
                    it.putExtra("strQuest", mList.get(position).question);
                    it.putExtra("strAnswer", mList.get(position).answer);
                    startActivity(it);
                }
            }
        }, 1);
        recycler.setAdapter(adapter);
        onRefresh();
    }

    private boolean isAdmin(){

        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if(pigInfo != null && pigInfo.isAdmin){
            return true;
        }else{
            return false;
        }
    }

    @Override
    protected void lazyLoadData() {

    }

    private int getCacheSize() {
        String data = SPUtils.get().getString("createCache", "");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<CreateModel>>() {
        }.getType();
        List<CreateModel> list = gson.fromJson(data, listType);
        return list.size();
    }

    private void onRefresh() {
        String data = SPUtils.get().getString("createCache", "");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<CreateModel>>() {
        }.getType();
        List<CreateModel> list = gson.fromJson(data, listType);
        mList.clear();
        if (list != null && list.size() > 0) {
            Collections.sort(list);
            CreateModel model = new CreateModel();
            model.type = 1;
            model.sid = list.size();
            mList.add(model);
            mList.addAll(list);
        }
        if (mList.size() > 0) {
            mStateView.showContent();
        } else {
            mStateView.showEmpty();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onReceiveEvent(Event event) {
        super.onReceiveEvent(event);
        if (event.getCode() == ADD_CREATE) {
            int position = (int) event.getData();
            if (position < 0) {
                return;
            }
            mList.remove(position);
            if (mList.size() == 1 && mList.get(0).type == 1) {
                mList.clear();
            } else if (mList.size() > 1 && mList.get(0).type == 1) {
                mList.get(0).sid = mList.size() - 1;
            } else {

            }
            if (mList.size() > 0) {
                mStateView.showContent();
            } else {
                mStateView.showEmpty();
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            List<CreateModel> cache = new ArrayList<>();
            if (mList == null) {
                return;
            }
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).type == 0) {
                    cache.add(mList.get(i));
                }
            }
            if(getCacheSize() <100){
                Gson gson = new Gson();
                String data1 = gson.toJson(cache);
                SPUtils.get().put("createCache", data1);
            }else{
                ToastUtils.showShortToast("草稿数量已达到上限");
            }


        } else if (event.getCode() == SAVE_CREATE_CACHE) {
            try {
                CreateModel model = (CreateModel) event.getData();
                if (mList.size() == 0) {
                    CreateModel model0 = new CreateModel();
                    model0.type = 1;
                    model0.sid = 1;
                    mList.add(model0);
                    mList.add(model);
                } else {
                    if (model.sid >= 0) {
                        mList.remove(model.sid);
                    }
                    mList.add(1, model);
                    if (mList.get(0).type == 1) {
                        mList.get(0).sid = mList.size() - 1;
                    }
                }
            } catch (Exception e) {
            }
            if (mList.size() > 0) {
                mStateView.showContent();
            } else {
                mStateView.showEmpty();
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            List<CreateModel> cache = new ArrayList<>();
            if (mList == null || mList.size() == 0) {
                return;
            }
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).type == 0) {
                    cache.add(mList.get(i));
                }
            }

            if(getCacheSize()<100){
                Gson gson = new Gson();
                String data1 = gson.toJson(cache);
                SPUtils.get().put("createCache", data1);
            }else{
                ToastUtils.showShortToast("草稿数量已达到上限");
            }


            //onRefresh();
        }
    }

    private void showPopWindows(View view, int deletePosition) {
        List<String> dataList = new ArrayList<>();
        dataList.add("删除该草稿");
        PopupWindowList mPopupWindowList = new PopupWindowList(view.getContext());
        mPopupWindowList.setDissListener(new PopupWindowList.DissListener() {
            @Override
            public void onDissListener() {
                mList.get(deletePosition).select = 0;
                adapter.notifyItemChanged(deletePosition);
            }
        });
        mPopupWindowList.setAnchorView(view);
        mPopupWindowList.setItemData(dataList);
        mPopupWindowList.setModal(true);
        mPopupWindowList.show();
        mPopupWindowList.setOnItemClickListener(new RecyclerItemClickListener(getActivity()) {
            @Override
            protected void onItemClick(View view, int position) {
                mPopupWindowList.hide();
                try {
                    String data = SPUtils.get().getString("createCache", "");
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<CreateModel>>() {
                    }.getType();
                    List<CreateModel> list = gson.fromJson(data, listType);
                    if (list != null && deletePosition >= 0) {
                        if (mList.get(0).type != 0) {
                            list.remove(deletePosition - 1);
                        } else {
                            list.remove(deletePosition);
                        }

                        if(getCacheSize() <100){
                            String data1 = gson.toJson(list);
                            SPUtils.get().put("createCache", data1);
                        }else{
                            ToastUtils.showShortToast("草稿数量已达到上限");
                        }



                    }
                    mList.remove(deletePosition);
                    if (mList.size() > 0 && mList.get(0).type == 1) {
                        mList.get(0).sid = mList.size() - 1;
                    }
                    if (mList.size() > 1) {
                        mStateView.showContent();
                    } else {
                        mList.clear();
                        mStateView.showEmpty();
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                }
            }
        });
    }
}
