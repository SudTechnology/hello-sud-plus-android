package tech.sud.mgp.audio.gift.manager;

import android.widget.ImageView;

import tech.sud.mgp.audio.gift.callback.PlayResultCallback;
import tech.sud.mgp.audio.gift.manager.stategy.PlayStrategy;
import tech.sud.mgp.audio.gift.model.PlayGiftModel;
import tech.sud.mgp.audio.gift.model.PlayResult;
import tech.sud.mgp.common.utils.ImageLoader;

public class GiftDisplayManager extends GiftBaseManager {

    public void showEffect(PlayGiftModel info, PlayStrategy strategy) {
        strategy.play(info);
    }

    /**
     * 展示一张图片给imageview
     */
    public void loadDefualtImageInImageView(String path, boolean showEnable, ImageView imageView, PlayResultCallback resultBack) {
        if (showEnable) {
            ImageLoader.loadImage(imageView, path);
        }
        imageView.postDelayed(() -> {
            if (resultBack != null) {
                resultBack.result(PlayResult.PLAYEND);
            }
        }, 2000);
    }
}