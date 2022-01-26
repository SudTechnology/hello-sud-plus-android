package tech.sud.mgp.audio.gift.model;

import tech.sud.mgp.audio.gift.callback.PlayResultCallback;

public class PlayGiftModel {
    private String path;
    private PlayResultCallback callback;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public PlayResultCallback getCallback() {
        return callback;
    }

    public void setCallback(PlayResultCallback callback) {
        this.callback = callback;
    }
}
