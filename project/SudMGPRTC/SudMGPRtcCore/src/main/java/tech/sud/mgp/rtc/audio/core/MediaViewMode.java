package tech.sud.mgp.rtc.audio.core;

public enum MediaViewMode {
    ASPECT_FIT, // 等比缩放，可能有黑边
    ASPECT_FILL, // 等比缩放填充整个 View，可能有部分被裁减
    SCALE_TO_FILL; // 填充整个 View，图像可能被拉伸
}
