package com.ubtechinc.goldenpig.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.ubtech.utilcode.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class JsonCallback<T> implements Callback {
    private Class<T> clazz;
    private Type type;

    public JsonCallback(Class<T> clazz) {
        this.clazz = clazz;
    }

    public JsonCallback(Type type) {
        this.type = type;
    }

    public void onFailure(Call call, IOException e) {
        onError(e.getMessage());
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        String result = response.body().string();
        LogUtils.d("hdf", result);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
            int code = jsonObject.getInt("code");
            if (code == 207 || code == 505 || code == -505) {
                onError("问句重复，请重新输入");
                return;
            } else if (code == 504 || code == -504) {
                onError("问答中包容敏感词，请重新输入");
            } else if (code != 0) {
                if (TextUtils.isEmpty(jsonObject.getString("errMsg"))) {
                    onError("数据异常，请重试");
                } else {
                    onError(jsonObject.getString("errMsg"));
                }
                return;
            }
            if (clazz == String.class) {
                //只需要String数据
                onSuccess(null);
                return;
            }
            Gson gson = new Gson();
            T data = null;
            if (clazz != null) data = gson.fromJson(jsonObject.getString("skills"), clazz);
            else if (type != null) data = gson.fromJson(jsonObject.getString("skills"), type);
            else if (clazz == null && type == null) data = (T) jsonObject;
            onSuccess(data);
        } catch (JSONException e) {
            e.printStackTrace();
            onError("数据异常，请重试");
        }
    }

    public abstract void onSuccess(T reponse);

    public abstract void onError(String str);
}
