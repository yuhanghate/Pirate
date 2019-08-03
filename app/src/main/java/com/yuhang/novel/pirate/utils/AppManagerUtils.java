package com.yuhang.novel.pirate.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/03/21
 * desc   : 应用程序Activity管理类：用于Activity管理和应用程序退出
 */
public class AppManagerUtils {
  private static Stack<Activity> activityStack;
  private static AppManagerUtils instance;

  /**
   * 单例模式 创建单一实例
   * @return
   */
  public static AppManagerUtils getAppManager() {
    if (instance == null) {
      instance = new AppManagerUtils();
    }
    return instance;
  }

  /**
   * 初始化Stack<Activity>
   */
  private void initActivityStack() {
    if (activityStack == null) {
      activityStack = new Stack<Activity>();
    }
  }

  /**
   * 添加Activity到堆栈
   * @param activity
   */
  public void addActivity(Activity activity) {
    initActivityStack();
    activityStack.add(activity);
  }

  /**
   * 获取当前Activity（堆栈中最后一个压入的）
   * @return
   */
  public Activity currentActivity() {
    Activity activity = activityStack.lastElement();
    return activity;
  }

  /**
   * 结束指定的Activity
   */
  public void finishActivity(Activity activity) {
    if (activity != null) {
      activityStack.remove(activity);
      activity.finish();
    }
  }

  /**
   * 结束当前Activity（堆栈中最后一个压入的）
   */
  public void finishActivity() {
    //获取到当前Activity
    Activity activity = activityStack.lastElement();
    //结束指定Activity
    finishActivity(activity);
  }

  /**
   * 结束指定类名的Activity
   */
  public void finishActivity(Class<?> cls) {
    List<Activity> activities = new ArrayList<Activity>();
    for (Activity activity : activityStack) {
      if (activity.getClass().equals(cls)) {
        // finishActivity(activity);
        activities.add(activity);
      }
    }
    // 结束所有类名相同activity
    activityStack.removeAll(activities);
    for (Activity activity : activities) {
      finishActivity(activity);
    }
  }

  /**
   * 结束所有Activity
   */
  public void finishAllActivity() {
    Log.i("","");
    for (int i = 0, size = activityStack.size(); i < size; i++) {
      if (null != activityStack.get(i)) {
        Activity activity = activityStack.get(i);
        if (!activity.isFinishing()) {
          activity.finish();
        }
      }
    }
    activityStack.clear();
  }

  /**
   * 退出应用程序
   * 这里关闭的是所有的Activity，没有关闭Activity之外的其他组件;
   * android.os.Process.killProcess(android.os.Process.myPid())
   * 杀死进程关闭了整个应用的所有资源，有时候是不合理的，通常是用
   * 堆栈管理Activity;System.exit(0)杀死了整个进程，这时候活动所占的
   * 资源也会被释放,它会执行所有通过Runtime.addShutdownHook注册的shutdown hooks.
   * 它能有效的释放JVM之外的资源,执行清除任务，运行相关的finalizer方法终结对象，
   * 而finish只是退出了Activity。
   */
  public void AppExit(Context context) {
    try {
      finishAllActivity();
      //DalvikVM的本地方法
      // 杀死该应用进程
      android.os.Process.killProcess(android.os.Process.myPid());
      System.exit(0);
      //这些方法如果是放到主Activity就可以退出应用，如果不是主Activity
      //就是退出当前的Activity
    } catch (Exception e) {
    }
  }
}
