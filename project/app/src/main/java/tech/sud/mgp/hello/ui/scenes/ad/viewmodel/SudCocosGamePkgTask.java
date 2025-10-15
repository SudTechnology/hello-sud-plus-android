package tech.sud.mgp.hello.ui.scenes.ad.viewmodel;

import android.os.Bundle;

import com.blankj.utilcode.util.LogUtils;
import com.cocos.game.CocosGamePackageManager;
import com.cocos.game.CocosGameRuntimeV2;

/** 游戏包下载安装的任务 */
public class SudCocosGamePkgTask {

    private CocosGameRuntimeV2 mRuntime;
    private String mGameId;
    private String mGameUrl;
    private String mGamePkgVersion;
    private GamePkgTaskListener mListener;
    private CocosGamePackageManager mPackageManager;
    private SudCocosGamePkgTask mTask = this;
    private long startDownloadTimestamp;
    private long startInstallPkgTimestamp;

    public SudCocosGamePkgTask(CocosGameRuntimeV2 runtime, String gameId, String gameUrl, String gamePkgVersion, GamePkgTaskListener listener) {
        mRuntime = runtime;
        mGameId = gameId;
        mGameUrl = gameUrl;
        mGamePkgVersion = gamePkgVersion;
        mListener = listener;
    }

    /** 开始 */
    public void start() {
        downloadGamePkg();
    }

    public String getGameId() {
        return mGameId;
    }

    public String getGamePkgVersion() {
        return mGamePkgVersion;
    }

    /** 下载游戏包 */
    private void downloadGamePkg() {
        LogUtils.d("CocosCalc 准备下载游戏包 gameId:" + mGameId);
        mPackageManager = (CocosGamePackageManager) mRuntime.getManager(CocosGameRuntimeV2.KEY_MANAGER_GAME_PACKAGE, null);
        // 下载游戏 判断是否已安装
        Bundle config = mPackageManager.getPackageInfo(mGameId);
        if (config == null || !mGamePkgVersion.equals(config.getString(CocosGamePackageManager.KEY_PACKAGE_VERSION))) {
            startDownloadTimestamp = System.currentTimeMillis();
            LogUtils.d("CocosCalc 游戏包未安装,需要下载 gameId:" + mGameId);
            // 没有安装过，或者游戏包版本不一致，那也去更新游戏包
            Bundle packageInfo = new Bundle();
            packageInfo.putString(CocosGamePackageManager.KEY_PACKAGE_URL, mGameUrl);
            mPackageManager.downloadPackage(packageInfo, _gamePackageDownloadListener);
        } else {
            LogUtils.d("CocosCalc 游戏包已安装，直接复用 gameId:" + mGameId);
            mListener.onSuccess(mTask, mGameId, mGamePkgVersion);
        }
    }

    //游戏包下载监听
    private final CocosGamePackageManager.PackageDownloadListener _gamePackageDownloadListener = new CocosGamePackageManager.PackageDownloadListener() {
        @Override
        public void onDownloadStart() {
            LogUtils.d("CocosCalc 游戏包开始下载 gameId:" + mGameId);

            LogUtils.d("onDownloadStart:" + mGameId);
            LogUtils.file("onDownloadStart:" + mGameId);
        }

        @Override
        public void onDownloadProgress(long downloadedSize, long totalSize) {
            LogUtils.d("PackageDownloadListener.onDownloadProgress " + downloadedSize + "/" + totalSize);
            LogUtils.file("PackageDownloadListener.onDownloadProgress " + downloadedSize + "/" + totalSize);
        }

        @Override
        public void onDownloadRetry(int retryNo) {
            LogUtils.d("PackageDownloadListener.onDownloadRetry: " + retryNo);
            LogUtils.file("PackageDownloadListener.onDownloadRetry: " + retryNo);
        }

        @Override
        public void onDownloadSuccess(String path) {
            startInstallPkgTimestamp = System.currentTimeMillis();
            LogUtils.d("CocosCalc 游戏包下载成功 gameId:" + mGameId + " 耗时：" + (System.currentTimeMillis() - startDownloadTimestamp));

            _installCpk(path, mGameId);
        }

        @Override
        public void onDownloadFailure(Throwable error) {
            LogUtils.d("CocosCalc 游戏包下载失败 gameId:" + mGameId + " 耗时：" + (System.currentTimeMillis() - startDownloadTimestamp));

            LogUtils.e("PackageDownloadListener.onFailure:", error);
            LogUtils.file("PackageDownloadListener.onFailure:" + error);
            mListener.onFailure(mTask, mGameId, mGamePkgVersion, error);
        }
    };

    // 游戏包安装监听
    private final CocosGamePackageManager.PackageInstallListener _gamePackageInstallListener = new CocosGamePackageManager
            .PackageInstallListener() {
        @Override
        public void onInstallStart() {
            LogUtils.d("CocosCalc 游戏包开始安装 gameId:" + mGameId);

            LogUtils.d("PackageInstallListener.onInstallStart");
            LogUtils.file("PackageInstallListener.onInstallStart");
        }

        @Override
        public void onInstallProgress(float percent) {
            LogUtils.d("PackageInstallListener.onInstallProgress " + percent);
            LogUtils.file("PackageInstallListener.onInstallProgress " + percent);
        }

        @Override
        public void onInstallSuccess() {
            LogUtils.d("CocosCalc 游戏包安装成功 gameId:" + mGameId + " 耗时：" + (System.currentTimeMillis() - startInstallPkgTimestamp));

            LogUtils.d("PackageInstallListener.onSuccess ");
            LogUtils.file("PackageInstallListener.onSuccess ");
            mListener.onSuccess(mTask, mGameId, mGamePkgVersion);
        }

        @Override
        public void onInstallFailure(Throwable error) {
            LogUtils.d("CocosCalc 游戏包安装失败 gameId:" + mGameId + " 耗时：" + (System.currentTimeMillis() - startInstallPkgTimestamp));

            LogUtils.e("PackageInstallListener.onFailure:", error);
            LogUtils.file("PackageInstallListener.onFailure:" + error);
            mListener.onFailure(mTask, mGameId, mGamePkgVersion, error);
        }
    };

    private void _installCpk(String path, String gameID) {
        // 安装游戏
        Bundle packageInfo = new Bundle();
        packageInfo.putString(CocosGamePackageManager.KEY_PACKAGE_PATH, path);
        packageInfo.putString(CocosGamePackageManager.KEY_PACKAGE_GAME_ID, gameID);
        packageInfo.putString(CocosGamePackageManager.KEY_PACKAGE_VERSION, mGamePkgVersion);
        packageInfo.putBoolean(CocosGamePackageManager.KEY_PACKAGE_DELETE_CPK, false);
        mPackageManager.installPackage(packageInfo, _gamePackageInstallListener);
    }

    public interface GamePkgTaskListener {
        void onSuccess(SudCocosGamePkgTask task, String gameId, String gamePkgVersion);

        void onFailure(SudCocosGamePkgTask task, String gameId, String gamePkgVersion, Throwable error);
    }

}
