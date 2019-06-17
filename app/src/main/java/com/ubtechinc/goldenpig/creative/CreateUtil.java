package com.ubtechinc.goldenpig.creative;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.model.CreateModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 众创空间工具类
 * @Author: zhijunzhou
 * @CreateDate: 2019/6/17 14:26
 */
public class CreateUtil {

    protected static final String KEY_CREATE_DRAFT = "createCache";

    protected static final int MAX_CREATE_DRAFT = 3;

    /**
     * 获取草稿
     *
     * @return
     */
    public static List<CreateModel> getCreateDraft() {
        String data = SPUtils.get().getString(KEY_CREATE_DRAFT, "");
        if (TextUtils.isEmpty(data)) {
            return null;
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<CreateModel>>() {
        }.getType();
        return gson.fromJson(data, listType);
    }

    /**
     * 保存草稿
     * @param cache
     * @return
     */
    public static boolean saveCreateDraft(CreateModel cache) {
        List<CreateModel> originalCache = getCreateDraft();
        int sid = cache.sid;
        if (originalCache == null) {
            originalCache = new ArrayList<>();
            originalCache.add(cache);
        } else if (originalCache.size() < MAX_CREATE_DRAFT) {
            if (sid == -1) {
                originalCache.add(cache);
            } else {
                originalCache.set(sid - 1, cache);
            }
        } else if (originalCache.size() == MAX_CREATE_DRAFT) {
            if (sid == -1) {
                ToastUtils.showShortToast("草稿数量已达到上限");
                return false;
            } else {
                originalCache.set(sid - 1, cache);
            }
        } else {
            ToastUtils.showShortToast("草稿数量已达到上限");
            return false;
        }
        Gson gson = new Gson();
        SPUtils.get().put(KEY_CREATE_DRAFT, gson.toJson(originalCache));
        return true;
    }

    /**
     * 更新草稿
     * @param list
     * @return
     */
    public static boolean updateCreateDraftList(List<CreateModel> list) {
        List<CreateModel> originList = new ArrayList<>(list);
        originList.remove(0);
//        ListIterator<CreateModel> listIterator = originList.listIterator();
//        while (listIterator.hasNext()) {
//            CreateModel createModel = listIterator.next();
//            if (createModel != null && createModel.type == 1) {
//                listIterator.remove();
//            }
//        }
        if (originList.size() > MAX_CREATE_DRAFT) {
            ToastUtils.showShortToast("草稿数量已达到上限");
            return false;
        }
        Gson gson = new Gson();
        SPUtils.get().put(KEY_CREATE_DRAFT, gson.toJson(originList));
        return true;
    }


}
