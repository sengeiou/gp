package com.ubtechinc.goldenpig.personal.management;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.model.AddressBookmodel;
import com.ubtechinc.goldenpig.view.Divider;
import com.ubtechinc.goldenpig.view.RecyclerItemClickListener;
import com.ubtechinc.goldenpig.view.RecyclerOnItemLongListener;
import com.ubtechinc.goldenpig.view.swipe_menu.SwipeMenuLayout;

import java.util.ArrayList;
import java.util.List;

import static com.ubtechinc.goldenpig.personal.management.AddressBookActivity.MAXADD;

/**
 * Created by MQ on 2017/6/9.
 */

public class AddressBookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<AddressBookmodel> mList;
    private int hasCard = 0;
    private RecyclerOnItemLongListener recyclerOnItemLongListener;

    public AddressBookAdapter(Context mContext, ArrayList<AddressBookmodel> mList, RecyclerOnItemLongListener
            recyclerOnItemLongListener) {
        this.mContext = mContext;
        this.mList = mList;
        this.recyclerOnItemLongListener = recyclerOnItemLongListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .adapter_addressbook, parent, false);
            return new AddressBookHolder(view);
        } else if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .adapter_no_more, parent, false);
            return new AddressBookHolder2(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .adapter_addressbook_header, parent, false);
            return new AddressBookHolder3(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (mList.get(position).type == 0) {
            AddressBookHolder aHolder = (AddressBookHolder) holder;
            AddressBookmodel model = mList.get(position);
            aHolder.tv_content.setText(model.name);
            aHolder.tv_number.setText(model.phone);
            aHolder.iv_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, AddAndSetContactActivity
                            .class);
                    it.putParcelableArrayListExtra("list", mList);
                    it.putExtra("type", 1);
                    it.putExtra("position", position);
                    mContext.startActivity(it);
                }
            });
            if (mList.size() - 1 == position) {
                aHolder.view_curline.setVisibility(View.GONE);
                //aHolder.view_curline_bottom.setVisibility(View.VISIBLE);
            } else {
                aHolder.view_curline.setVisibility(View.VISIBLE);
                //aHolder.view_curline_bottom.setVisibility(View.GONE);
            }
        } else if (mList.get(position).type == 1) {
            AddressBookHolder2 aHolder = (AddressBookHolder2) holder;
            aHolder.tv_content.setText(mContext.getString(R.string.contact_limit));
        } else {
            AddressBookHolder3 aHolder = (AddressBookHolder3) holder;
            if (mList.get(position).noCard) {
                aHolder.tv_has_card.setText("未插入SIM卡");
                aHolder.tv_has_card.setTextColor(mContext.getResources().getColor(R.color
                        .ubt_tab_btn_txt_checked_color));
            } else if (TextUtils.isEmpty(mList.get(position).phone)) {
                aHolder.tv_has_card.setText("无法获取号码");
                aHolder.tv_has_card.setTextColor(mContext.getResources().getColor(R.color.empty_color));
            } else {
                aHolder.tv_has_card.setText(mList.get(position).phone);
                aHolder.tv_has_card.setTextColor(mContext.getResources().getColor(R.color
                        .ubt_tab_btn_txt_checked_color));
            }
            aHolder.iv_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerOnItemLongListener!=null){
                        recyclerOnItemLongListener.onItemClick(v,position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).type;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class AddressBookHolder extends RecyclerView.ViewHolder {
        private TextView tv_content, tv_number;//, tv_set, tv_delete;
        private ImageView iv_edit;
        private View view_curline;

        public AddressBookHolder(View itemView) {
            super(itemView);
//            swipe_menu = (SwipeMenuLayout) itemView.findViewById(R.id.swipe_menu);
            iv_edit = itemView.findViewById(R.id.iv_edit);
//            view_curline_bottom = itemView.findViewById(R.id.view_curline_bottom);
            view_curline = itemView.findViewById(R.id.view_curline);
            iv_edit = itemView.findViewById(R.id.iv_edit);
            tv_number = (TextView) itemView.findViewById(R.id.tv_number);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }

    public class AddressBookHolder2 extends RecyclerView.ViewHolder {
        TextView tv_content;

        public AddressBookHolder2(View itemView) {
            super(itemView);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }

    public class AddressBookHolder3 extends RecyclerView.ViewHolder {
        TextView tv_has_card;
        ImageView iv_add;

        public AddressBookHolder3(View itemView) {
            super(itemView);
            tv_has_card = itemView.findViewById(R.id.tv_has_card);
            iv_add = itemView.findViewById(R.id.iv_add);
        }
    }

}
