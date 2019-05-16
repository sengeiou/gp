package com.ubtechinc.goldenpig.utils.share;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.commlib.network.NetworkHelper;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;

import java.util.Arrays;
import java.util.List;

/**
 *@auther        :zzj
 *@email         :zhijun.zhou@ubtrobot.com
 *@Description:  :分享对话框
 *@time          :2019/1/21 20:58
 *@change        :
 *@changetime    :2019/1/21 20:58
*/
public class ShareDialog extends Dialog {

    private Activity mContext;
    String iconPath;
    GridView mShareGrid;
    ShareUtility shareUtility;
    TextView btn_cancel;
    ShareGridviewAdapter adapter;
    List<ShareItemEnum> shareItemEnums;
    OnClickShareListener onClickShareListener;

    public ShareDialog(Activity context) {
        super(context, R.style.dialogstyle);
        this.mContext = context;
        initShareItem();
    }

    public ShareDialog(Activity context, ShareItemEnum... params) {
        super(context, R.style.dialogstyle);
        this.mContext = context;
        initShareItem(params);
    }

    private void initShareItem(ShareItemEnum... params) {
        if (params == null || params.length == 0) {
            shareItemEnums = Arrays.asList(ShareItemEnum.values().clone());
        } else {
            shareItemEnums = Arrays.asList(params);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.share_dialog, null);
        mShareGrid = view.findViewById(R.id.share_grid);
        mShareGrid.setNumColumns(shareItemEnums.size());
        btn_cancel = view.findViewById(R.id.btn_cancel);
        setContentView(view);
        Window dialogWindow = this.getWindow();
        setCanceledOnTouchOutside(true);
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.dialog_bottom_Anim);
        WindowManager m = mContext.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth());
        dialogWindow.setAttributes(p);
        initData();
    }

    private void initData() {
        shareUtility = ShareUtility.getInstance();
        mShareGrid.setAdapter(new ShareGridviewAdapter(mContext, shareItemEnums));
        btn_cancel.setOnClickListener(v -> dismiss());
    }

    public class ShareGridviewAdapter extends BaseAdapter {

        private Activity mContext;
        private List<ShareItemEnum> mList;
        private ViewHolder holder;

        public ShareGridviewAdapter(Activity mContext, List<ShareItemEnum> mList) {
            this.mContext = mContext;
            this.mList = mList;
        }

        public void setData(List<ShareItemEnum> mList) {
            this.mList = mList;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ShareItemEnum mShareItemEnum = mList.get(position);
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.share_item, null);
                holder.txtDes = convertView.findViewById(R.id.tv_share_label);
                holder.imageView = convertView.findViewById(R.id.iv_share_icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.imageView.setImageResource(mShareItemEnum
                    .getImageID());
            holder.txtDes.setText(mShareItemEnum.getValue());
            convertView.setOnClickListener(v -> {
                if (UBTPGApplication.isNetAvailable) {
                    if (onClickShareListener != null) {
                        switch (mShareItemEnum) {
                            case WECHAT:
                                onClickShareListener.onShareWx(shareUtility);
                                break;
                            case WECHATMOMENTS:
                                onClickShareListener.onShareWxTimeline(shareUtility);
                                break;
                            case QQ:
                                onClickShareListener.onShareQQ(shareUtility);
                                break;
                        }
                    }
                    dismiss();
                }else{
                    ToastUtils.showShortToast(R.string.network_error);
                }
            });
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView txtDes;
        }
    }

    public void setOnClickShareListener(OnClickShareListener onClickShareListener) {
        this.onClickShareListener = onClickShareListener;
    }
}
