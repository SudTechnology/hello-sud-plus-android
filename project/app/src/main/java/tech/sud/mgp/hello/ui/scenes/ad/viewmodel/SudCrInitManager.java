package tech.sud.mgp.hello.ui.scenes.ad.viewmodel;

import android.content.Context;
import android.os.Bundle;

import com.blankj.utilcode.util.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tech.sud.cr.core.SudCr;
import tech.sud.cr.core.SudCrGameCoreHandle;
import tech.sud.cr.core.SudCrGameRuntime;

/** 用于初始化环境 */
public class SudCrInitManager {

    private static SudCrGameRuntime sGameRuntime;
    private static SudCrGameCoreHandle sGameCoreHandle;
    private static boolean isInitializingRuntime;
    private static boolean isInitializingCore;
    private static List<InitRuntimeListener> sRuntimeListenerList;
    private static List<InitCoreListener> sCoreListenerList;

    /** 初始化runtime */
    public static void initRuntime(Context context, InitRuntimeListener listener) {
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
        synchronized (BaseSudCrGameViewModel.class) {
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
            SudCr.createRuntime(context, getRuntimeBundle(context), new SudCrGameRuntime.RuntimeCreateListener() {
                @Override
                public void onSuccess(SudCrGameRuntime runtime) {
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
    public static void initCocosCore(InitCoreListener listener) {
        if (listener == null) {
            return;
        }
        if (sGameCoreHandle != null) {
            listener.onSuccess(sGameCoreHandle);
            return;
        }
        synchronized (BaseSudCrGameViewModel.class) {
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
            sGameRuntime.loadCore(null, new SudCrGameRuntime.CoreLoadListener() {
                @Override
                public void onSuccess(SudCrGameCoreHandle coreHandle) {
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
        bundle.putString(SudCrGameRuntime.KEY_RUNTIME_STORAGE_PATH_APP, parentDir + File.separator + "app");
        bundle.putString(SudCrGameRuntime.KEY_RUNTIME_STORAGE_PATH_CACHE, cacheParentDir + File.separator + "runtime");
        bundle.putString(SudCrGameRuntime.KEY_RUNTIME_STORAGE_PATH_CORE, parentDir + File.separator + "core");
        bundle.putString(SudCrGameRuntime.KEY_RUNTIME_STORAGE_PATH_USER, parentDir + File.separator + "user");
        bundle.putString(SudCrGameRuntime.KEY_RUNTIME_STORAGE_PATH_PLUGIN, parentDir + File.separator + "plugin");
        return bundle;
    }

    private static void notifyInitRuntimeOnSuccess() {
        if (sRuntimeListenerList == null) {
            return;
        }
        Iterator<InitRuntimeListener> iterator = sRuntimeListenerList.iterator();
        while (iterator.hasNext()) {
            InitRuntimeListener next = iterator.next();
            next.onSuccess(sGameRuntime);
            iterator.remove();
        }
    }

    private static void notifyInitRuntimeOnFailure(Throwable error) {
        if (sRuntimeListenerList == null) {
            return;
        }
        Iterator<InitRuntimeListener> iterator = sRuntimeListenerList.iterator();
        while (iterator.hasNext()) {
            InitRuntimeListener next = iterator.next();
            next.onFailure(error);
            iterator.remove();
        }
    }

    private static void notifyInitCoreOnSuccess() {
        if (sCoreListenerList == null) {
            return;
        }
        Iterator<InitCoreListener> iterator = sCoreListenerList.iterator();
        while (iterator.hasNext()) {
            InitCoreListener next = iterator.next();
            next.onSuccess(sGameCoreHandle);
            iterator.remove();
        }
    }

    private static void notifyInitCoreOnFailure(Throwable error) {
        if (sCoreListenerList == null) {
            return;
        }
        Iterator<InitCoreListener> iterator = sCoreListenerList.iterator();
        while (iterator.hasNext()) {
            InitCoreListener next = iterator.next();
            next.onFailure(error);
            iterator.remove();
        }
    }

    public interface InitRuntimeListener {
        void onSuccess(SudCrGameRuntime runtime);

        void onFailure(Throwable error);
    }

    public interface InitCoreListener {
        void onSuccess(SudCrGameCoreHandle coreHandle);

        void onFailure(Throwable error);
    }

}
