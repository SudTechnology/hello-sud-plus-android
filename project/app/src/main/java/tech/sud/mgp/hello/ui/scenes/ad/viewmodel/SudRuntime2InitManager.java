package tech.sud.mgp.hello.ui.scenes.ad.viewmodel;

import android.content.Context;
import android.os.Bundle;

import com.blankj.utilcode.util.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tech.sud.gip.r2.core.SudRuntime2;
import tech.sud.gip.r2.core.SudRuntime2GameCoreHandle;
import tech.sud.gip.r2.core.SudRuntime2GameRuntime;


/** 用于初始化环境 */
public class SudRuntime2InitManager {

    private static SudRuntime2GameRuntime sGameRuntime;
    private static SudRuntime2GameCoreHandle sGameCoreHandle;
    private static boolean isInitializingRuntime;
    private static boolean isInitializingCore;
    private static List<CreateRuntimeListener> sRuntimeListenerList;
    private static List<LoadCoreListener> sCoreListenerList;

    /** 初始化runtime */
    public static void createRuntime(Context context, CreateRuntimeListener listener) {
        if (listener == null) {
            return;
        }
        if (context == null) {
            listener.onFailure(new RuntimeException("context can not be empty"));
            return;
        }
        if (sGameRuntime != null) {
            listener.onSuccess(sGameRuntime);
            return;
        }
        synchronized (BaseRuntime2GameViewModel.class) {
            if (isInitializingRuntime) {
                if (sRuntimeListenerList == null) {
                    sRuntimeListenerList = new ArrayList<>();
                }
                sRuntimeListenerList.add(listener);
                return;
            }
            isInitializingRuntime = true;
            long start = System.currentTimeMillis();
            LogUtils.d("CocosCalc createRuntime 开始");
            SudRuntime2.createRuntime(context, getRuntimeBundle(context), new SudRuntime2GameRuntime.RuntimeCreateListener() {
                @Override
                public void onSuccess(SudRuntime2GameRuntime runtime) {
                    LogUtils.d("CocosCalc createRuntime 成功耗时：" + (System.currentTimeMillis() - start));
                    sGameRuntime = runtime;
                    listener.onSuccess(runtime);
                    notifyInitRuntimeOnSuccess();
                    isInitializingRuntime = false;
                }

                @Override
                public void onFailure(Throwable error) {
                    LogUtils.d("CocosCalc createRuntime 失败耗时：" + (System.currentTimeMillis() - start));
                    listener.onFailure(error);
                    notifyInitRuntimeOnFailure(error);
                    isInitializingRuntime = false;
                }
            });
        }
    }

    /** 初始化core */
    public static void loadCore(LoadCoreListener listener) {
        if (listener == null) {
            return;
        }
        if (sGameCoreHandle != null) {
            listener.onSuccess(sGameCoreHandle);
            return;
        }
        synchronized (BaseRuntime2GameViewModel.class) {
            if (isInitializingCore) {
                if (sCoreListenerList == null) {
                    sCoreListenerList = new ArrayList<>();
                }
                sCoreListenerList.add(listener);
                return;
            }
            isInitializingCore = true;
            long start = System.currentTimeMillis();
            LogUtils.d("CocosCalc loadCore 开始");
            // 不是动态core 不传 core 的 options
            sGameRuntime.loadCore(null, new SudRuntime2GameRuntime.CoreLoadListener() {
                @Override
                public void onSuccess(SudRuntime2GameCoreHandle coreHandle) {
                    LogUtils.d("CocosCalc loadCore 成功耗时：" + (System.currentTimeMillis() - start));
                    sGameCoreHandle = coreHandle;
                    listener.onSuccess(coreHandle);
                    notifyInitCoreOnSuccess();
                    isInitializingCore = false;
                }

                @Override
                public void onFailure(Throwable error) {
                    LogUtils.d("CocosCalc loadCore 失败耗时：" + (System.currentTimeMillis() - start));
                    listener.onFailure(error);
                    notifyInitCoreOnFailure(error);
                    isInitializingCore = false;
                }
            });
        }
    }

    private static Bundle getRuntimeBundle(Context context) {
        Bundle bundle = new Bundle();
        String parentDir = new File(context.getFilesDir(), "sud" + File.separator + "cocos").getAbsolutePath();
        String cacheParentDir = new File(context.getCacheDir(), "sud" + File.separator + "cocos").getAbsolutePath();
        bundle.putString(SudRuntime2GameRuntime.KEY_RUNTIME_STORAGE_PATH_APP, parentDir + File.separator + "app");
        bundle.putString(SudRuntime2GameRuntime.KEY_RUNTIME_STORAGE_PATH_CACHE, cacheParentDir + File.separator + "runtime");
        bundle.putString(SudRuntime2GameRuntime.KEY_RUNTIME_STORAGE_PATH_CORE, parentDir + File.separator + "core");
        bundle.putString(SudRuntime2GameRuntime.KEY_RUNTIME_STORAGE_PATH_USER, parentDir + File.separator + "user");
        bundle.putString(SudRuntime2GameRuntime.KEY_RUNTIME_STORAGE_PATH_PLUGIN, parentDir + File.separator + "plugin");
        return bundle;
    }

    private static void notifyInitRuntimeOnSuccess() {
        if (sRuntimeListenerList == null) {
            return;
        }
        Iterator<CreateRuntimeListener> iterator = sRuntimeListenerList.iterator();
        while (iterator.hasNext()) {
            CreateRuntimeListener next = iterator.next();
            next.onSuccess(sGameRuntime);
            iterator.remove();
        }
    }

    private static void notifyInitRuntimeOnFailure(Throwable error) {
        if (sRuntimeListenerList == null) {
            return;
        }
        Iterator<CreateRuntimeListener> iterator = sRuntimeListenerList.iterator();
        while (iterator.hasNext()) {
            CreateRuntimeListener next = iterator.next();
            next.onFailure(error);
            iterator.remove();
        }
    }

    private static void notifyInitCoreOnSuccess() {
        if (sCoreListenerList == null) {
            return;
        }
        Iterator<LoadCoreListener> iterator = sCoreListenerList.iterator();
        while (iterator.hasNext()) {
            LoadCoreListener next = iterator.next();
            next.onSuccess(sGameCoreHandle);
            iterator.remove();
        }
    }

    private static void notifyInitCoreOnFailure(Throwable error) {
        if (sCoreListenerList == null) {
            return;
        }
        Iterator<LoadCoreListener> iterator = sCoreListenerList.iterator();
        while (iterator.hasNext()) {
            LoadCoreListener next = iterator.next();
            next.onFailure(error);
            iterator.remove();
        }
    }

    public interface CreateRuntimeListener {
        void onSuccess(SudRuntime2GameRuntime runtime);

        void onFailure(Throwable error);
    }

    public interface LoadCoreListener {
        void onSuccess(SudRuntime2GameCoreHandle coreHandle);

        void onFailure(Throwable error);
    }

}
