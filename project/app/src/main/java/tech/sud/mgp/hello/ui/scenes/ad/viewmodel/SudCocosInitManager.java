package tech.sud.mgp.hello.ui.scenes.ad.viewmodel;

import android.content.Context;
import android.os.Bundle;

import com.blankj.utilcode.util.LogUtils;
import com.cocos.game.CocosGameCoreHandle;
import com.cocos.game.CocosGameRuntimeV2;
import com.cocos.game.CocosGameV2;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** 用于初始化cocos环境 */
public class SudCocosInitManager {

    private static CocosGameRuntimeV2 sCocosGameRuntime;
    private static CocosGameCoreHandle sCocosGameCoreHandle;
    private static boolean isInitializingRuntime;
    private static boolean isInitializingCore;
    private static List<InitRuntimeListener> sRuntimeListenerList;
    private static List<InitCoreListener> sCoreListenerList;

    /** 初始化runtime */
    public static void initCocosRuntime(Context context, InitRuntimeListener listener) {
        if (listener == null) {
            return;
        }
        if (context == null) {
            listener.onFailure(new RuntimeException("context can not be empty"));
            return;
        }
        if (sCocosGameRuntime != null) {
            listener.onSuccess(sCocosGameRuntime);
            return;
        }
        synchronized (BaseCocosGameViewModel.class) {
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
            CocosGameV2.createRuntime(context, getRuntimeBundle(context), new CocosGameRuntimeV2.RuntimeCreateListener() {
                @Override
                public void onSuccess(CocosGameRuntimeV2 runtime) {
                    LogUtils.d("CocosCalc createRuntime 成功耗时：" + (System.currentTimeMillis() - start));
                    sCocosGameRuntime = runtime;
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
        if (sCocosGameCoreHandle != null) {
            listener.onSuccess(sCocosGameCoreHandle);
            return;
        }
        synchronized (BaseCocosGameViewModel.class) {
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
            sCocosGameRuntime.loadCore(null, new CocosGameRuntimeV2.CoreLoadListener() {
                @Override
                public void onSuccess(CocosGameCoreHandle coreHandle) {
                    LogUtils.d("CocosCalc loadCore 成功耗时：" + (System.currentTimeMillis() - start));
                    sCocosGameCoreHandle = coreHandle;
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
        bundle.putString(CocosGameRuntimeV2.KEY_RUNTIME_STORAGE_PATH_APP, parentDir + File.separator + "app");
        bundle.putString(CocosGameRuntimeV2.KEY_RUNTIME_STORAGE_PATH_CACHE, cacheParentDir + File.separator + "runtime");
        bundle.putString(CocosGameRuntimeV2.KEY_RUNTIME_STORAGE_PATH_CORE, parentDir + File.separator + "core");
        bundle.putString(CocosGameRuntimeV2.KEY_RUNTIME_STORAGE_PATH_USER, parentDir + File.separator + "user");
        bundle.putString(CocosGameRuntimeV2.KEY_RUNTIME_STORAGE_PATH_PLUGIN, parentDir + File.separator + "plugin");
        return bundle;
    }

    private static void notifyInitRuntimeOnSuccess() {
        if (sRuntimeListenerList == null) {
            return;
        }
        Iterator<InitRuntimeListener> iterator = sRuntimeListenerList.iterator();
        while (iterator.hasNext()) {
            InitRuntimeListener next = iterator.next();
            next.onSuccess(sCocosGameRuntime);
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
            next.onSuccess(sCocosGameCoreHandle);
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
        void onSuccess(CocosGameRuntimeV2 runtime);

        void onFailure(Throwable error);
    }

    public interface InitCoreListener {
        void onSuccess(CocosGameCoreHandle coreHandle);

        void onFailure(Throwable error);
    }

}
