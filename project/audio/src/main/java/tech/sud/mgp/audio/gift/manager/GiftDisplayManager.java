package tech.sud.mgp.audio.gift.manager;

import android.content.Context;
import android.net.http.HttpResponseCache;
import android.widget.ImageView;

import com.opensource.svgaplayer.SVGAParser;

import java.io.File;
import java.io.IOException;

import tech.sud.mgp.audio.gift.listener.PlayResultListener;
import tech.sud.mgp.audio.gift.manager.stategy.PlayStrategy;
import tech.sud.mgp.audio.gift.model.PlayGiftModel;
import tech.sud.mgp.audio.gift.model.PlayResult;

public class GiftDisplayManager extends GiftBaseManager {

    public GiftDisplayManager(Context context) {
        //后面需要改成app的上下文
        File cacheDir = new File(context.getCacheDir().getName());
        try {
            HttpResponseCache.install(cacheDir, 1024 * 1024 * 512);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SVGAParser.Companion.shareParser().init(context);
    }

    public void showEffect(PlayGiftModel info, PlayStrategy strategy) {
        strategy.play(info);
    }

    /**
     * 展示一张图片给imageview
     */
    public void loadDefualtImageInImageView(int imageId, boolean showEnable, ImageView imageView, PlayResultListener listener) {
        if (showEnable) {
           imageView.setImageResource(imageId);
        }
        imageView.postDelayed(() -> {
            if (listener != null) {
                listener.onResult(PlayResult.PLAYEND);
            }
        }, 2000);
    }
}