package tech.sud.mgp.audio.gift.manager.stategy;

import tech.sud.mgp.audio.gift.manager.GiftDisplayManager;
import tech.sud.mgp.audio.gift.model.PlayGiftModel;

public abstract class PlayStrategy<T extends PlayGiftModel> {
    public abstract void play(T t);
}
