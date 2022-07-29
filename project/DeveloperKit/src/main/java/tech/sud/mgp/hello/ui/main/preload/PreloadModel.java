package tech.sud.mgp.hello.ui.main.preload;

import tech.sud.mgp.core.PkgDownloadStatus;

/**
 * 预加载进度
 */
public class PreloadModel {
    public long gameId;
    public int iconResId;
    public String gameName;
    public PkgDownloadStatus status;
    public long downloadedSize;
    public long totalSize;
    public int errorCode;
    public String errorMsg;

    public int progress; // 进度百分比
}
