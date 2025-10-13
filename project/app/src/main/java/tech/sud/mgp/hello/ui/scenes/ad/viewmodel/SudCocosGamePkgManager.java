package tech.sud.mgp.hello.ui.scenes.ad.viewmodel;

import android.text.TextUtils;

import com.cocos.game.CocosGameRuntimeV2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 游戏包管理
 * cocos的下载和安装管理器，不支持并发，需要做队列
 */
public class SudCocosGamePkgManager {

    private static List<PkgModel> sPkgModelList = new ArrayList<>(); // 请求队列
    private static List<SudCocosGamePkgTask> sGamePkgTaskList = new ArrayList<>(); // 正在干活的队列
    private static CocosGameRuntimeV2 sRuntime;

    /** 下载并安装游戏包 */
    public static synchronized void downloadAndInstallGamePkg(CocosGameRuntimeV2 runtime, String gameId, String gameUrl, String gamePkgVersion, GamePkgListener listener) {
        if (listener == null) {
            return;
        }
        if (runtime == null || TextUtils.isEmpty(gameId) || TextUtils.isEmpty(gameUrl) || TextUtils.isEmpty(gamePkgVersion)) {
            listener.onFailure(gameId, gamePkgVersion, new RuntimeException("downloadAndInstallGamePkg params can not be empty"));
            return;
        }
        if (sRuntime == null) {
            sRuntime = runtime;
        }
        // 加入到请求队列当中
        PkgModel pkgModel = new PkgModel();
        pkgModel.runtime = runtime;
        pkgModel.gameId = gameId;
        pkgModel.gameUrl = gameUrl;
        pkgModel.gamePkgVersion = gamePkgVersion;
        pkgModel.listener = listener;
        sPkgModelList.add(pkgModel);

        startNext();
    }

    // 检查判断是否可以开启一个新的任务
    private static synchronized void startNext() {
        if (sGamePkgTaskList.size() > 0) { // 任务不做并发，运行时决定了这一块
            return;
        }
        if (sPkgModelList.size() == 0) { // 没有需要做的任务
            return;
        }
        PkgModel pkgModel = sPkgModelList.get(0);

        // 启动一个任务
        SudCocosGamePkgTask task = new SudCocosGamePkgTask(sRuntime, pkgModel.gameId, pkgModel.gameUrl, pkgModel.gamePkgVersion, new SudCocosGamePkgTask.GamePkgTaskListener() {
            @Override
            public void onSuccess(SudCocosGamePkgTask task, String gameId, String gamePkgVersion) {
                notifyOnSuccess(task, gameId, gamePkgVersion);
                startNext();
            }

            @Override
            public void onFailure(SudCocosGamePkgTask task, String gameId, String gamePkgVersion, Throwable error) {
                notifyOnFailure(task, gameId, gamePkgVersion, error);
                startNext();
            }
        });
        sGamePkgTaskList.add(task);
        task.start();
    }

    private static synchronized void notifyOnSuccess(SudCocosGamePkgTask task, String gameId, String gamePkgVersion) {
        Iterator<PkgModel> iterator = sPkgModelList.iterator();
        while (iterator.hasNext()) {
            PkgModel next = iterator.next();
            if (gameId.equals(next.gameId) && gamePkgVersion.equals(next.gamePkgVersion)) {
                next.listener.onSuccess(gameId, gamePkgVersion);
                iterator.remove();
            }
        }
        sGamePkgTaskList.remove(task);
    }

    private static synchronized void notifyOnFailure(SudCocosGamePkgTask task, String gameId, String gamePkgVersion, Throwable error) {
        Iterator<PkgModel> iterator = sPkgModelList.iterator();
        while (iterator.hasNext()) {
            PkgModel next = iterator.next();
            if (gameId.equals(next.gameId) && gamePkgVersion.equals(next.gamePkgVersion)) {
                next.listener.onFailure(gameId, gamePkgVersion, error);
                iterator.remove();
            }
        }
        sGamePkgTaskList.remove(task);
    }

    private static class PkgModel {
        public CocosGameRuntimeV2 runtime;
        public String gameId;
        public String gameUrl;
        public String gamePkgVersion;
        public GamePkgListener listener;
    }

}
