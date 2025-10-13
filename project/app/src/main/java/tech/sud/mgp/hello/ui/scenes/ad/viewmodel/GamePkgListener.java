package tech.sud.mgp.hello.ui.scenes.ad.viewmodel;

public interface GamePkgListener {
    void onSuccess(String gameId, String gamePkgVersion);

    void onFailure(String gameId, String gamePkgVersion, Throwable error);
}
