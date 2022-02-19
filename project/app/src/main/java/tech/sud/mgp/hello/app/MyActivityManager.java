/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package tech.sud.mgp.hello.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;

/**
 * activity管理
 */
public class MyActivityManager implements Application.ActivityLifecycleCallbacks {

    private static final MyActivityManager instance = new MyActivityManager();

    private MyActivityManager() {
    }

    public static MyActivityManager getInstance() {
        return instance;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        LogUtils.d("onActivityCreated：" + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        LogUtils.d("onActivityStarted：" + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        LogUtils.d("onActivityResumed：" + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        LogUtils.d("onActivityPaused：" + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        LogUtils.d("onActivityStopped：" + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        LogUtils.d("onActivitySaveInstanceState：" + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        LogUtils.d("onActivityDestroyed：" + activity.getClass().getSimpleName());
    }
}
