package com.ubtech.utilcode.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 *
 *     @author: logic.peng
 *     @email  : pdlogic1987@gmail.com
 *     @time  : 2016/8/2
 *     desc  : SP相关工具类
 *
 */
public class SPUtils {

    private   SharedPreferences pref;
    private  SharedPreferences.Editor editor;
    private static final String TAG = "SPUtils";

    /**
     * SPUtils构造函数
     * <p>在Application中初始化</p>
     *
     * @param spName spName
     */
    public SPUtils(String spName) {
        pref = Utils.getContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.apply();
    }
    private SPUtils() {

        pref =  PreferenceManager.getDefaultSharedPreferences(Utils.getContext());
        editor = pref.edit();
        editor.apply();
    }
    //使用volatile关键字保其可见性
    volatile private static SPUtils INSTANCE = null;
    public static SPUtils get() {

        if (INSTANCE == null) {
            //创建实例之前可能会有一些准备性的耗时工作
            synchronized (SPUtils.class) {
                if (INSTANCE == null) {//二次检查
                    INSTANCE = new SPUtils();
                }
            }
        }

        return INSTANCE;
    }

    /**
     * SP中写入String类型value
     *
     * @param key   键
     * @param value 值
     */
    public void put(String key, @Nullable String value) {

        checkPrefAndEditor();
        editor.putString(key, value).apply();
    }

    public Object readObject(String key) {
        checkPrefAndEditor();
        try {
            String string = getString(key, "");
            if (TextUtils.isEmpty(string)) {
                return null;
            } else {
                // 将16进制的数据转为数组，准备反序列化
                byte[] stringToBytes = StringToBytes(string);
                ByteArrayInputStream bis = new ByteArrayInputStream(
                        stringToBytes);
                ObjectInputStream is = new ObjectInputStream(bis);
                // 返回反序列化得到的对象
                Object readObject = is.readObject();
                return readObject;
            }
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 所有异常返回null
        return null;

    }

    public static byte[] StringToBytes(String data) {
        String hexString = data.toUpperCase().trim();
        if (hexString.length() % 2 != 0) {
            return null;
        }
        byte[] retData = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i++) {
            int int_ch; // 两位16进制数转化后的10进制数
            char hex_char1 = hexString.charAt(i); // //两位16进制数中的第一位(高位*16)
            int int_ch1;
            if (hex_char1 >= '0' && hex_char1 <= '9')
                int_ch1 = (hex_char1 - 48) * 16; // // 0 的Ascll - 48
            else if (hex_char1 >= 'A' && hex_char1 <= 'F')
                int_ch1 = (hex_char1 - 55) * 16; // // A 的Ascll - 65
            else
                return null;
            i++;
            char hex_char2 = hexString.charAt(i); // /两位16进制数中的第二位(低位)
            int int_ch2;
            if (hex_char2 >= '0' && hex_char2 <= '9')
                int_ch2 = (hex_char2 - 48); // // 0 的Ascll - 48
            else if (hex_char2 >= 'A' && hex_char2 <= 'F')
                int_ch2 = hex_char2 - 55; // // A 的Ascll - 65
            else
                return null;
            int_ch = int_ch1 + int_ch2;
            retData[i / 2] = (byte) int_ch;// 将转化后的数放入Byte里
        }
        return retData;
    }

    /**
     * 保存序列化对象
     * @param key
     * @param obj
     */
    public void saveObject(String key, Object obj) {
        try {

            // 先将序列化结果写到byte缓存中，其实就分配一个内存空间
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            // 将对象序列化写入byte缓存
            os.writeObject(obj);
            // 将序列化的数据转为16进制保存
            String bytesToHexString = bytesToHexString(bos.toByteArray());
            // 保存该16进制数组
            editor.putString(key, bytesToHexString).commit();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("", "保存obj失败");
        }
    }

    public static String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return null;
        }
        if (bArray.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    private void checkPrefAndEditor() {
        if (pref == null)
        {
            pref = PreferenceManager.getDefaultSharedPreferences(Utils.getContext());
        }
        if (editor == null)
        {
            editor = pref.edit();
        }
    }

    /**
     * SP中读取String
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值{@code null}
     */
    public String getString(String key) {

        return getString(key, null);
    }

    /**
     * SP中读取String
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值{@code defaultValue}
     */
    public String getString(String key, String defaultValue) {
        checkPrefAndEditor();
        return pref.getString(key, defaultValue);
    }

    /**
     * SP中写入int类型value
     *
     * @param key   键
     * @param value 值
     */
    public void put(String key, int value) {
        checkPrefAndEditor();
        editor.putInt(key, value).apply();
    }

    /**
     * SP中读取int
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    public int getInt(String key) {
        return getInt(key, -1);
    }

    /**
     * SP中读取int
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值{@code defaultValue}
     */
    public int getInt(String key, int defaultValue) {
        checkPrefAndEditor();
        return pref.getInt(key, defaultValue);
    }

    /**
     * SP中写入long类型value
     *
     * @param key   键
     * @param value 值
     */
    public void put(String key, long value) {
        checkPrefAndEditor();
        editor.putLong(key, value).apply();
    }

    /**
     * SP中读取long
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    public long getLong(String key) {
        return getLong(key, -1L);
    }

    /**
     * SP中读取long
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值{@code defaultValue}
     */
    public long getLong(String key, long defaultValue) {
        checkPrefAndEditor();
        return pref.getLong(key, defaultValue);
    }

    /**
     * SP中写入float类型value
     *
     * @param key   键
     * @param value 值
     */
    public void put(String key, float value) {
        checkPrefAndEditor();
        editor.putFloat(key, value).apply();
    }

    /**
     * SP中读取float
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    public float getFloat(String key) {
        return getFloat(key, -1f);
    }

    /**
     * SP中读取float
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值{@code defaultValue}
     */
    public float getFloat(String key, float defaultValue) {

        checkPrefAndEditor();
        return pref.getFloat(key, defaultValue);
    }

    /**
     * SP中写入boolean类型value
     *
     * @param key   键
     * @param value 值
     */
    public void put(String key, boolean value) {
        checkPrefAndEditor();
        editor.putBoolean(key, value).apply();
    }

    /**
     * SP中读取boolean
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值{@code false}
     */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * SP中读取boolean
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值{@code defaultValue}
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        checkPrefAndEditor();
        return pref.getBoolean(key, defaultValue);
    }

    /**
     * SP中写入String集合类型value
     *
     * @param key    键
     * @param values 值
     */
    public void put(String key, @Nullable Set<String> values) {
        checkPrefAndEditor();
        editor.putStringSet(key, values).apply();
    }

    /**
     * SP中读取StringSet
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值{@code null}
     */
    public Set<String> getStringSet(String key) {
        return getStringSet(key, null);
    }

    /**
     * SP中读取StringSet
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值{@code defaultValue}
     */
    public Set<String> getStringSet(String key, @Nullable Set<String> defaultValue) {
        checkPrefAndEditor();
        return pref.getStringSet(key, defaultValue);
    }

    /**
     * SP中获取所有键值对
     *
     * @return Map对象
     */
    public Map<String, ?> getAll() {
        checkPrefAndEditor();
        return pref.getAll();
    }

    /**
     * SP中移除该key
     *
     * @param key 键
     */
    public void remove(String key) {
        checkPrefAndEditor();
        editor.remove(key).apply();
    }

    /**
     * SP中是否存在该key
     *
     * @param key 键
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public boolean contains(String key) {
        checkPrefAndEditor();
        return pref.contains(key);
    }

    /**
     * SP中清除所有数据
     */
    public void clear() {
        checkPrefAndEditor();
        editor.clear().apply();
    }


    /**
     * 直接存放对象，反射将根据对象的属性作为key，并将对应的值保存。
     *
     * @param t
     */
    @SuppressWarnings("rawtypes")
    public  <T> void putObject(T t) {
        try {
            checkPrefAndEditor();
            String methodName = "";
            String savekey = "";
            String saveValue = "";
            SharedPreferences.Editor edit = pref.edit();
            Class cls = t.getClass();

            if (edit != null) {
                Method[] methods = cls.getDeclaredMethods();

                for (Method method : methods) {

                    methodName = method.getName();
                    if (methodName != null && methodName.startsWith("get")) {

                        Object value = method.invoke(t);
                        if (!TextUtils.isEmpty(String.valueOf(value))) {
                            saveValue = String.valueOf(value);
                        }

                        savekey = methodName.replace("get", "");
                        savekey = savekey.toLowerCase();
                        edit.putString(savekey, saveValue);
                    }
                }
                edit.commit();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取整个对象，跟put(T t)对应使用， 利用反射得到对象的属性，然后从preferences获取
     *
     * @param cls
     * @return
     */
    public  <T> Object getObject(Class<T> cls) {
        Object obj = null;
        String fieldName = "";
        try {
            obj = cls.newInstance();
            Field[] fields = cls.getDeclaredFields();
            for (Field f : fields) {
                fieldName = f.getName();
                if (!"serialVersionUID".equals(fieldName)) {
                    f.setAccessible(true);
                    f.set(obj, getString(f.getName().toLowerCase()));
                    // TODO: 2017/11/2  如果在同一个xml文件中，存在对象重名，可能会导致问题
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return obj;
    }

}