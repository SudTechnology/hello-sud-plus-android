package tech.sud.mgp.audio.gift.manager.stategy;

import androidx.lifecycle.LifecycleOwner;

import tech.sud.mgp.audio.gift.model.PlayGiftModel;
import tech.sud.mgp.audio.gift.view.GiftVideoView;

public class GiftMp4Model extends PlayGiftModel {
    private GiftVideoView mp4View;
    private LifecycleOwner lifecycleOwner;

    public GiftVideoView getMp4View() {
        return mp4View;
    }

    public void setMp4View(GiftVideoView mp4View) {
        this.mp4View = mp4View;
    }

    public LifecycleOwner getLifecycleOwner() {
        return lifecycleOwner;
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }
}