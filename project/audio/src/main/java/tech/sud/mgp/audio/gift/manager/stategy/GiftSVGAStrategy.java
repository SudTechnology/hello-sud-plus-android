package tech.sud.mgp.audio.gift.manager.stategy;

import android.view.View;

import androidx.annotation.NonNull;

import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import tech.sud.mgp.audio.gift.callback.PlayResultCallback;
import tech.sud.mgp.audio.gift.model.PlayResult;

public class GiftSVGAStrategy extends PlayStrategy<GiftSVGAModel>{
    @Override
    public void play(GiftSVGAModel giftSVGAModel) {
        playSvgaAsset(giftSVGAModel.getPath(), giftSVGAModel.getSvgaView(), giftSVGAModel.getCallback());
    }

    /**
     * 播放svga
     */
    public void playSvgaAsset(String svgaPath, SVGAImageView svgaView,PlayResultCallback callback) {
        SVGAParser parser = SVGAParser.Companion.shareParser();
        parser.decodeFromAssets(svgaPath, new SVGAParser.ParseCompletion() {
            @Override
            public void onError() {
                if (callback != null) {
                    callback.result(PlayResult.PLAYERROR);
                }
            }

            @Override
            public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                if (callback != null) {
                    callback.result(PlayResult.START);
                }
                svgaView.setVisibility(View.VISIBLE);
                SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
                svgaView.setImageDrawable(drawable);
                svgaView.startAnimation();
            }
        }, null);
    }

    /**
     * 播放svga
     */
    public void playSvga(String svgaPath, SVGAImageView svgaView,PlayResultCallback callback) {
        SVGAParser parser = SVGAParser.Companion.shareParser();
        try {
            FileInputStream input = new FileInputStream(svgaPath);
            parser.decodeFromInputStream(input, svgaPath, new SVGAParser.ParseCompletion() {
                @Override
                public void onError() {
                    if (callback != null) {
                        callback.result(PlayResult.PLAYERROR);
                    }
                }

                @Override
                public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                    if (callback != null) {
                        callback.result(PlayResult.START);
                    }
                    svgaView.setVisibility(View.VISIBLE);
                    SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
                    svgaView.setImageDrawable(drawable);
                    svgaView.startAnimation();
                }
            }, true, null, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.result(PlayResult.PLAYERROR);
            }
        }
    }

}
