package com.ubtechinc.goldenpig.creative;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseNewFragment;
import com.ubtechinc.goldenpig.comm.entity.UserInfo;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.model.CreateModel;
import com.ubtechinc.goldenpig.model.JsonCallback;
import com.ubtechinc.goldenpig.personal.interlocution.InterlocutionModel;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.popup.PopupWindowList;
import com.ubtechinc.goldenpig.utils.DialogUtil;
import com.ubtechinc.goldenpig.utils.SCADAHelper;
import com.ubtechinc.goldenpig.view.NewCircleImageView;
import com.ubtechinc.goldenpig.view.RecyclerItemClickListener;
import com.ubtechinc.goldenpig.view.RecyclerOnItemLongListener;
import com.ubtechinc.goldenpig.view.StateView;
import com.ubtechinc.goldenpig.view.TopDivider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.ADD_CREATE;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.ADD_INTERLO_SUCCESS;
import static com.ubtechinc.goldenpig.utils.SharedPreferencesUtils.CREATEGUIDE;


public class CreateListFragment extends BaseNewFragment {
    private static final String TAG = CreateListFragment.class.getSimpleName();
    Gson gson = new Gson();
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.rl_layout)
    RelativeLayout rl_layout;
    CreateAdapter adapter;
    private ArrayList<CreateModel> mList;
    private int page = 1;
    private HeaderAndFooterRecyclerViewAdapter mHeaderAndFooterRecyclerViewAdapter;
    public Boolean hasAddFooterView = false;
    InterlocutionModel requestModel;
    private Handler mHandler = new Handler();

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
        requestModel = new InterlocutionModel();
        initStateView(rl_layout, false);
        mStateView.setOnRetryClickListener(new StateView.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                onRefresh();
            }
        });
        mStateView.setEmptyResource(R.layout.adapter_create_empty);
        mList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setHasFixedSize(true);
        //添加白色分割线在顶部
//        GradientDrawable drawable = new GradientDrawable();
//        drawable.setColor(ContextCompat.getColor(getActivity(), R.color.ubt_main_bg_color));
//        drawable.setShape(GradientDrawable.RECTANGLE);
//        drawable.setSize(0, (int) getResources().getDimension(R.dimen.dp_12));
//        recycler.addItemDecoration(new MyDividerItemDecoration(drawable));
        TopDivider divider = new TopDivider(new ColorDrawable(getResources().getColor(R.color
                .ubt_main_bg_color)),
                OrientationHelper.VERTICAL);
        divider.setHeight((int) getResources().getDimension(R.dimen.dp_12));
        recycler.addItemDecoration(divider);
        adapter = new CreateAdapter(getActivity(), mList, new RecyclerOnItemLongListener(){

            @Override
            public void onItemLongClick(View v, int position) {
                showPopWindows(v, position);
                mList.get(position).select = 1;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemClick(View v, int position) {
                Intent it = new Intent(getActivity(), SeeCreateActivity.class);
                it.putExtra("strQuest", mList.get(position).question);
                it.putExtra("strAnswer", mList.get(position).answer);
                startActivity(it);
            }
        });
        mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(adapter);
        recycler.setAdapter(mHeaderAndFooterRecyclerViewAdapter);
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();
                    if (lastVisiblePosition >= linearLayoutManager.getItemCount() - 1) {
                        if ((getState() == FootState.Normal)) {
                            LogUtils.d("hdf", "onScrollStateChanged-onRefresh");
                            setState(FootState.Loading);
                            onRefresh();
                        }
                    }
                }
            }
        });
        mStateView.showLoading();
        onRefresh();
    }

    @Override
    protected void lazyLoadData() {

    }

    private void onRefresh() {

        UbtLogger.d(TAG, "page:" + page);
        new CreativeSpaceHttpProxy().getData(page, new CreativeSpaceHttpProxy.GetCreativeCallback() {
            @Override
            public void onError(String error) {
                UbtLogger.d(TAG, "onError:" + error);
                if(mHandler != null){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mList != null && mList.size() > 0) {
                                mStateView.showContent();
                            } else {
                                mStateView.showRetry();
                            }
                            setState(FootState.Normal);
                        }
                    });
                }


            }

            @Override
            public void onSuccess(List<CreateModel> data, int total) {

                if(mHandler != null){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            SPUtils.get().put("create_count", total);

                            if (page == 1) {
                                mList.clear();
                                if (data != null && data.size() > 0) {
                                    CreateModel model = new CreateModel();
                                    model.type = 1;
                                    model.sid = data.size();
                                    mList.add(model);
                                    mList.addAll(data);
                                    page++;
                                }
                                checkGuide();
                            } else {
                                if (data != null && data.size() > 0) {
                                    mList.addAll(data);
                                    page++;
                                    if (mList != null && mList.size() > 0 && mList.get(0).type == 1) {
                                        mList.get(0).sid = mList.size() - 1;
                                    }
                                }
                            }
                            if (data != null && data.size() < 10) {
                                setState(FootState.NoMore);
                            } else {
                                setState(FootState.Normal);
                            }
                            adapter.notifyDataSetChanged();

                            if (mList.size() > 0) {
                                mStateView.showContent();

                            } else {
                                mStateView.showEmpty();
                            }
                        }
                    });
                }


            }
        });



    }

    private void showPopWindows(View view, int deletePosition) {
        List<String> dataList = new ArrayList<>();
        dataList.add("同步到定制问答");
        dataList.add(getString(R.string.delete_interloc));
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
                switch (position) {
                    case 1:
                        deleteAlarm(deletePosition);
                        break;
                    case 0:
                        addInterloction(deletePosition);
                        break;
                }

            }
        });
    }

    private boolean isAdmin(){

        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if(pigInfo != null && pigInfo.isAdmin){
            return true;
        }else{
            return false;
        }
    }

    private void addInterloction(int deletePosition) {
        if (isAdmin()) {
            if (TextUtils.isEmpty(mList.get(deletePosition).question)) {
                ToastUtils.showShortToast("请先设置问句");
                return;
            }
            if (TextUtils.isEmpty(mList.get(deletePosition).answer)) {
                ToastUtils.showShortToast("请先设置回答");
                return;
            }
            LoadingDialog.getInstance(getActivity()).show();
//            SCADAHelper.recordEvent(EVENET_APP_QA_SAVE);
            requestModel.addInterlocutionRequest(mList.get(deletePosition).question, mList.get(deletePosition)
                    .answer, new JsonCallback<String>(String.class) {

                @Override
                public void onSuccess(String reponse) {
//                    SCADAHelper.recordEvent(EVENET_APP_QA_SAVE_SUCCESS);
                    if(mHandler != null){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                LoadingDialog.getInstance(getActivity()).dismiss();
                                ToastUtils.showShortToast("添加问答成功");
                            }
                        });
                    }

                    Event<String> event = new Event(ADD_INTERLO_SUCCESS);
                    EventBusUtil.sendEvent(event);
                }

                @Override
                public void onError(String str) {
//                    SCADAHelper.recordEvent(EVENET_APP_QA_SAVE_FAILURE);
                    if(mHandler != null){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                LoadingDialog.getInstance(getActivity()).dismiss();
                                if (!TextUtils.isEmpty(str) && str.equals("保存失败，已存在相同问句")) {
                                    ToastUtils.showShortToast("同步失败，已存在相同问句");
                                } else {
                                    ToastUtils.showShortToast(str);
                                }
                            }
                        });
                    }

                }
            });
        } else {
            ToastUtils.showShortToast("只有管理员才可以有此操作");
        }
    }

    private void deleteAlarm(int adapterPosition) {
        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        array.put(mList.get(adapterPosition).sid);
        try {
            obj.put("ids", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LoadingDialog.getInstance(getActivity()).show();

        new CreativeSpaceHttpProxy().deleteCreativeContent(obj, new CreativeSpaceHttpProxy.DeleteCreativeCallback() {
            @Override
            public void onError(String error) {
                UbtLogger.e(TAG, "onError:" + error);
                if(mHandler != null){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LoadingDialog.getInstance(getActivity()).dismiss();
                            ToastUtils.showShortToast(error);
                        }
                    });
                }

            }

            @Override
            public void onSuccess() {
                if(mHandler != null){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LoadingDialog.getInstance(getActivity()).dismiss();
                            mList.remove(adapterPosition);
                            adapter.notifyDataSetChanged();
                            mList.get(0).sid = mList.size() - 1;
                            if (mList.size() > 1) {
                                mStateView.showContent();
                            } else {
                                mList.clear();
                                mStateView.showEmpty();
                            }
                        }
                    });
                }

            }
        });


    }

    @Override
    protected void onReceiveEvent(Event event) {
        super.onReceiveEvent(event);
        if (event.getCode() == ADD_CREATE) {
            page = 1;
            onRefresh();
        }
    }

    public enum FootState {
        Normal, NoMore, Loading, NetWorkError
    }

    protected FootState mState = FootState.Normal;

    public FootState getState() {
        return mState;
    }

    public void setState(FootState status) {
        mState = status;
        switch (status) {
            case Normal:
                if (ll_loading != null) {
                    ll_loading.setVisibility(View.VISIBLE);
                }
                if (ll_nomore != null) {
                    ll_nomore.setVisibility(View.GONE);
                }
                if (ll_netWorkerror != null) {
                    ll_netWorkerror.setVisibility(View.GONE);
                }
                break;
            case Loading:
                if (ll_loading != null) {
                    ll_loading.setVisibility(View.VISIBLE);
                }
                if (ll_nomore != null) {
                    ll_nomore.setVisibility(View.GONE);
                }
                if (ll_netWorkerror != null) {
                    ll_netWorkerror.setVisibility(View.GONE);
                }
                break;
            case NoMore:
                if (ll_loading != null) {
                    ll_loading.setVisibility(View.GONE);
                }
                if (ll_nomore != null) {
                    ll_nomore.setVisibility(View.VISIBLE);
                }
                if (ll_netWorkerror != null) {
                    ll_netWorkerror.setVisibility(View.GONE);
                }
                break;
            case NetWorkError:
                if (ll_loading != null) {
                    ll_loading.setVisibility(View.GONE);
                }
                if (ll_nomore != null) {
                    ll_nomore.setVisibility(View.GONE);
                }
                if (ll_netWorkerror != null) {
                    ll_netWorkerror.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    public LinearLayout ll_loading, ll_nomore, ll_netWorkerror;

    public View initFooterView() {
        View footerView = LayoutInflater.from(getActivity())
                .inflate(R.layout.layout_footer, new LinearLayout(getActivity()), true);
        ll_loading = (LinearLayout) footerView.findViewById(R.id.ll_loading);
        ll_nomore = (LinearLayout) footerView.findViewById(R.id.ll_nomore);
        ll_netWorkerror = (LinearLayout) footerView.findViewById(R.id.ll_netWorkerror);

        return footerView;
    }

    Dialog picDialog;

    public void showFirstGuide() {
        UbtLogger.d(TAG, "showFirstGuide");
        if (picDialog == null) {
            View picView = LayoutInflater.from(getActivity()).inflate(
                    R.layout.dialog_create_guide, null);
            picDialog = DialogUtil.getMenuDialog(getActivity(), picView);
            picView.findViewById(R.id.iv_know).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    picDialog.dismiss();
                }
            });
            NewCircleImageView iv_ask = picView.findViewById(R.id.iv_ask);
            UserInfo currentUser = AuthLive.getInstance().getCurrentUser();

            String userIc = "";
            if (currentUser != null) {
                userIc = currentUser.getUserImage();
            }
            Glide.with(getActivity()).load(userIc).asBitmap().placeholder(R.drawable.ic_user_inter)
                    .error(R.drawable.ic_user_inter).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_ask);
        }
        if (!picDialog.isShowing()) {
            UbtLogger.d(TAG, "showFirstGuide show");
            picDialog.show();
        }
    }

    public void checkGuide() {
        UbtLogger.d(TAG, "checkGuide:" + UBTPGApplication.getInstance().HASCREATEGUIDE);
        if (UBTPGApplication.getInstance().HASCREATEGUIDE) {
            return;
        }
        try {
            if (((CreateActivity) getActivity()).getSelPosition() == 0 && mList.size() > 0) {
                UbtLogger.d(TAG, "checkGuide showFirstGuide");
                UBTPGApplication.getInstance().HASCREATEGUIDE = true;
                SPUtils.get().put(CREATEGUIDE, true);
                showFirstGuide();
            }
        } catch (Exception e) {
        }
    }
}
