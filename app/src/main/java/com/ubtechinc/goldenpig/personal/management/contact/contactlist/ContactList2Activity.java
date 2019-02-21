//package com.ubtechinc.goldenpig.personal.management.contact.contactlist;
//
//import android.os.Bundle;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.AbsListView;
//import android.widget.AdapterView;
//import android.widget.TextView;
//
//import com.google.protobuf.InvalidProtocolBufferException;
//import com.tencent.TIMCustomElem;
//import com.tencent.TIMElem;
//import com.tencent.TIMMessage;
//import com.ubt.imlibv2.bean.MyContact;
//import com.ubt.imlibv2.bean.UbtTIMManager;
//import com.ubt.imlibv2.bean.listener.OnUbtTIMConverListener;
//import com.ubt.improtolib.GPResponse;
//import com.ubtech.utilcode.utils.network.NetworkHelper;
//import com.ubtechinc.goldenpig.R;
//import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewTv;
//import com.ubtechinc.goldenpig.base.BaseNewActivity;
//import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
//import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
//import com.ubtechinc.goldenpig.eventbus.modle.Event;
//import com.ubtechinc.goldenpig.login.observable.AuthLive;
//import com.ubtechinc.goldenpig.model.AddressBookmodel;
//import com.ubtechinc.goldenpig.personal.management.contact.ContactUtil;
//import com.ubtechinc.goldenpig.personal.management.contact.PinyinComparator;
//import com.ubtechinc.goldenpig.personal.management.contact.SideBar;
//import com.ubtechinc.goldenpig.personal.management.contact.SortAdapter;
//import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
//import com.ubtechinc.goldenpig.utils.UbtToastUtils;
//import com.ubtrobot.channelservice.proto.ChannelMessageContainer;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Observable;
//import java.util.Observer;
//
//import butterknife.BindView;
//
//import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.CONTACT_CHECK_SUCCESS;
//import static com.ubtechinc.goldenpig.personal.management.AddressBookActivity.MAXADD;
//
//public class ContactList2Activity extends BaseNewActivity implements Observer {
//    @BindView(R.id.rl_titlebar)
//    SecondTitleBarViewTv rl_titlebar;
//    @BindView(R.id.lv_contact)
//    RecyclerView sortListView;
//
//    @BindView(R.id.dialog)
//    TextView dialog;
//
//    @BindView(R.id.sidrbar)
//    SideBar sideBar;
//    private SortAdapter adapter;
//    private List<MyContact> SourceDateList;
//    private ArrayList<AddressBookmodel> oldList;
//
//    @Override
//    protected int getContentViewId() {
//        return R.layout.activity_contact_list2;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        oldList = getIntent().getParcelableArrayListExtra("list");
//        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
//        if (pigInfo != null) {
//            UbtTIMManager.getInstance().setPigAccount(pigInfo.getRobotName());
//        } else {
////            UbtTIMManager.getInstance().setPigAccount("2cb9b9a3");
//        }
//        UbtTIMManager.getInstance().setMsgObserve(this);
//        UbtTIMManager.getInstance().setOnUbtTIMConverListener(new OnUbtTIMConverListener() {
//            @Override
//            public void onError(int i, String s) {
//                if (AuthLive.getInstance().getCurrentPig() != null) {
//                    UbtToastUtils.showCustomToast(getApplication(),"八戒未登录");
//                } else {
//                    UbtToastUtils.showCustomToast(getApplication(),"未绑定八戒");
//                }
//                LoadingDialog.getInstance(ContactList2Activity.this).dismiss();
//            }
//
//            @Override
//            public void onSuccess() {
//
//            }
//        });
//        rl_titlebar.setLeftOnclickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        rl_titlebar.setTitleText("导入手机联系人");
//        rl_titlebar.setTvRightName("导入");
//        rl_titlebar.setRightOnclickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                List<MyContact> list = new ArrayList<>();
//                Boolean flag = true;
//                for (int i = 0; i < SourceDateList.size(); i++) {
//                    if (SourceDateList.get(i).select) {
//                        list.add(SourceDateList.get(i));
//                    }
//                }
//                for (int i = 0; i < list.size(); i++) {
//                    if (!flag) {
//                        break;
//                    }
//                    if (!checkOldList(list.get(i).lastname, list.get(i).mobile)) {
//                        flag = false;
//                        break;
//                    }
//                    for (int j = i + 1; j < list.size(); j++) {
//                        if (list.get(i).lastname.equals(list.get(j).lastname)) {
//                            UbtToastUtils.showCustomToast(getApplication(),"昵称重复，请先取消重复号码再选择");
//                            flag = false;
//                            break;
//                        }
//                        if (list.get(i).mobile.equals(list.get(j).mobile)) {
//                            UbtToastUtils.showCustomToast(getApplication(),"号码重复，请先取消重复号码再选择");
//                            flag = false;
//                            break;
//                        }
//                    }
//                }
//                if (MAXADD < list.size() + oldList.size()) {
//                    UbtToastUtils.showCustomToast(getApplication(),"八戒机器人最多能储存30人，请重新选择");
//                } else if (flag) {
//                    if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
//                        UbtToastUtils.showCustomToast(getApplication(), getString(R.string.network_error));
//                        return;
//                    }
//                    LoadingDialog.getInstance(ContactList2Activity.this).setShowToast(true).setToastTye(1).show();
//                    UbtTIMManager.getInstance().addUser(list);
//                }
//            }
//        });
//        rl_titlebar.getTvRight().setEnabled(false);
//        rl_titlebar.getTvRight().setTextColor(getResources().getColor(R.color
//                .empty_color));
//
//        sideBar.setTextView(dialog);
//        SourceDateList = ContactUtil.getInstance(this).getContactList();
//        Collections.sort(SourceDateList, new PinyinComparator());
//        sideBar.setIndexText(ContactUtil.getInstance(this).getIndexString());
//        adapter = new SortAdapter(this, SourceDateList);
//        sortListView.setAdapter(adapter);
//        sortListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int i) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int
//                    totalItemCount) {
//                //当滑动列表的时候，更新右侧字母列表的选中状态
//                try {
//                    sideBar.setTouchIndex(SourceDateList.get(firstVisibleItem).sortLetter);
//                } catch (Exception e) {
//                }
//
//            }
//        });
//        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
//            @Override
//            public void onTouchingLetterChanged(String s) {
//                int position = adapter.getPositionForSection(s.charAt(0));
//                if (position != -1) {
//                    sortListView.setSelection(position);
//                }
//            }
//        });
//        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                SourceDateList.get(position).select = !SourceDateList.get(position).select;
//                adapter.notifyDataSetChanged();
//                for (int i = 0; i < SourceDateList.size(); i++) {
//                    if (SourceDateList.get(i).select) {
//                        rl_titlebar.getTvRight().setTextColor(getResources().getColor(R.color
//                                .ubt_tab_btn_txt_checked_color));
//                        rl_titlebar.getTvRight().setEnabled(true);
//                        break;
//                    }
//                    if (i == SourceDateList.size() - 1) {
//                        rl_titlebar.getTvRight().setEnabled(false);
//                        rl_titlebar.getTvRight().setTextColor(getResources().getColor(R.color
//                                .empty_color));
//                    }
//                }
//            }
//        });
//    }
//
//    private boolean checkOldList(String strName, String strPhone) {
//        try {
//            for (int i = 0; i < oldList.size(); i++) {
//                if (TextUtils.isEmpty(oldList.get(i).phone) || TextUtils.isEmpty(oldList.get(i).name)) {
//                    continue;
//                }
//                if (oldList.get(i).type != 0) {
//                    continue;
//                }
//                if (oldList.get(i).phone.equals(strPhone)) {
//                    UbtToastUtils.showCustomToast(getApplication(),"该号码已存在，请选择其他号码");
//                    return false;
//                }
//                if (oldList.get(i).name.equals(strName)) {
//                    UbtToastUtils.showCustomToast(getApplication(),"昵称重复，导入请前往八戒通讯录修改昵称");
//                    return false;
//                }
//            }
//        } catch (Exception e) {
//        }
//        return true;
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        UbtTIMManager.getInstance().deleteMsgObserve(this);
//    }
//
//    @Override
//    public void update(Observable o, Object arg) {
//        LoadingDialog.getInstance(ContactList2Activity.this).dismiss();
//        try {
//            TIMMessage msg = (TIMMessage) arg;
//            for (int i = 0; i < msg.getElementCount(); ++i) {
//                TIMElem tIMElem = msg.getElement(i);
//                if (tIMElem instanceof TIMCustomElem) {
//                    TIMCustomElem elem = (TIMCustomElem) tIMElem;
//                    dealMsg(elem.getData());
//                }
//            }
//        } catch (Exception e) {
//
//        }
//    }
//
//    private void dealMsg(Object arg) throws InvalidProtocolBufferException {
//        ChannelMessageContainer.ChannelMessage msg = ChannelMessageContainer.ChannelMessage
//                .parseFrom((byte[]) arg);
//        String action = msg.getHeader().getAction();
//        switch (action) {
//            case "/im/mail/query":
//                break;
//            case "/im/mail/add":
//                Boolean flag = msg.getPayload().unpack(GPResponse.Response.class).getResult();
//                if (flag) {
//                    UbtToastUtils.showCustomToast(getApplication(),"导入成功");
//                    EventBusUtil.sendEvent(new Event<String>(CONTACT_CHECK_SUCCESS));
//                    finish();
//                } else {
//                    UbtToastUtils.showCustomToast(getApplication(),"请求异常，请重试");
//                }
//                break;
//
//            case "/im/mail/delete":
//                break;
//            case "/im/mail/update":
//                Boolean flag2 = msg.getPayload().unpack(GPResponse.Response.class).getResult();
//                if (flag2) {
//                    UbtToastUtils.showCustomToast(getApplication(),"编辑成功");
//                    EventBusUtil.sendEvent(new Event<String>(CONTACT_CHECK_SUCCESS));
//                    finish();
//                } else {
//                    UbtToastUtils.showCustomToast(getApplication(),"导入失败，请重试");
//                }
//                break;
//            default:
//        }
//    }
//
//    public static Boolean isGB2312(String str) {
//        for (int i = 0; i < str.length(); i++) {
//            String bb = str.substring(i, i + 1);
//            // 生成一个Pattern,同时编译一个正则表达式,其中的u4E00("一"的unicode编码)-\u9FA5("龥"的unicode编码)
//            boolean cc = java.util.regex.Pattern.matches("[\u4E00-\u9FA5]", bb);
//            if (cc == false) {
//                return cc;
//            }
//        }
//        return true;
//    }
//}
