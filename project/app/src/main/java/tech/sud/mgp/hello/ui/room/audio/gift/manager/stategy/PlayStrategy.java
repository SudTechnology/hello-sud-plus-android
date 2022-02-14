package tech.sud.mgp.hello.ui.room.audio.gift.manager.stategy;

import tech.sud.mgp.hello.ui.room.audio.gift.model.PlayGiftModel;

public abstract class PlayStrategy<T extends PlayGiftModel> {
    public abstract void play(T t);
}
