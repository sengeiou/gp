package com.ubtechinc.goldenpig.app;

import android.app.Activity;
import android.os.Process;

import java.util.Iterator;
import java.util.Stack;

/**
 * <br>类描述:activity管理类，便于一些跳转时管理activity
 * <br>功能详细描述:
 */
public class ActivityManager {

    private Stack<Activity> mActivityStack;
    private static ActivityManager sInstance;

    private ActivityManager() {
    }

    public boolean contains(Class<?> cls) {
        for(Activity activity : mActivityStack) {
            if(activity.getClass() == cls) {
                return true;
            }
        }
        return false;
    }

    public  static ActivityManager getInstance() {
        if (sInstance == null) {
            synchronized (ActivityManager.class){
                if(sInstance == null){
                    sInstance = new ActivityManager();
                }
            }
        }
        return sInstance;
    }

    //退出栈顶Activity
    public void popActivity(Activity activity) {
        if (mActivityStack != null) {
            if (activity != null) {
                //在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作
                activity.finish();
                mActivityStack.remove(activity);
                activity = null;
            }
        }
    }

    //获得当前栈顶Activity
    public Activity currentActivity() {
        if (mActivityStack == null) {
            return null;
        }
        Activity activity = null;
        if (!mActivityStack.empty()) {
            activity = mActivityStack.lastElement();
        }
        return activity;
    }

    //将当前Activity推入栈中
    public void pushActivity(Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<Activity>();
        }
        mActivityStack.add(activity);
    }

    //退出栈中所有Activity
    public void popAllActivity() {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            popActivity(activity);
        }
    }

    //退出栈中所有Activity,除exceptActivity外
    public void popAllActivityExcept(Activity exceptActivity) {
        if (mActivityStack != null) {
            Iterator iterator = mActivityStack.iterator();
            while (iterator.hasNext()) {
                Activity activity = (Activity) iterator.next();
                if (activity != null && activity != exceptActivity) {
                    activity.finish();
                }
            }
            mActivityStack.removeAllElements();
            mActivityStack.add(exceptActivity);
        }

    }

    //退出栈中所有Activity
    public void popAllActivityExcept(String className) {
        if (mActivityStack != null && className != null) {
            Iterator iterator = mActivityStack.iterator();
            while (iterator.hasNext()) {
                Activity activity = (Activity) iterator.next();
                if (activity != null && !activity.getClass().getName().equals(className)) {
                    activity.finish();
                    popActivity(activity);
                    break;
                }
            }
        }

    }

    public void clearRecord(Activity activity) {
        if (mActivityStack != null && mActivityStack.contains(activity)) {
            mActivityStack.remove(activity);
        }
    }

    public void popActivity(String className) {
        if (mActivityStack != null) {
            Iterator iterator = mActivityStack.iterator();
            while (iterator.hasNext()) {
                Activity activity = (Activity) iterator.next();
                if (activity != null && activity.getClass().getName().equals(className)) {
                    activity.finish();
                    popActivity(activity);
                    break;
                }
            }
        }
    }
    /**
     * 退出程序的方法
     */
    public void exit(boolean flag) {
        for (Activity activity : mActivityStack) {
            activity.finish();
        }
        if (flag){
            try {
                Thread.sleep(500);
                Process.killProcess(Process.myPid());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
