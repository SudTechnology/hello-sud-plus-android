package tech.sud.mgp.audio.gift.model;

import tech.sud.mgp.audio.gift.listener.PlayResultListener;

public class PlayGiftModel {
    private String path;
    private int resId;
    private PlayResultListener playResultListener;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public PlayResultListener getPlayResultListener() {
        return playResultListener;
    }

    public void setPlayResultListener(PlayResultListener playResultListener) {
        this.playResultListener = playResultListener;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
