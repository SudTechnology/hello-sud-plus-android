package tech.sud.mgp.hello.ui.room.audio.gift.manager.stategy;

import android.view.View;

import androidx.annotation.NonNull;

import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import java.io.InputStream;

import tech.sud.mgp.hello.ui.room.audio.gift.listener.PlayResultListener;
import tech.sud.mgp.hello.ui.room.audio.gift.model.PlayResult;

public class GiftSVGAStrategy extends PlayStrategy<GiftSVGAModel> {
    @Override
    public void play(GiftSVGAModel giftSVGAModel) {
        playSvgaAsset(giftSVGAModel.getResId(), giftSVGAModel.getSvgaView(), giftSVGAModel.getPlayResultListener());
    }

    /**
     * 播放svga
     */
    public void playSvgaAsset(int svgaRes, SVGAImageView svgaView, PlayResultListener listener) {
        SVGAParser parser = SVGAParser.Companion.shareParser();
        svgaView.setCallback(new SVGACallback() {
            @Override
            public void onPause() {

            }

            @Override
            public void onFinished() {
                if (listener != null) {
                    listener.onResult(PlayResult.PLAYEND);
                }
            }

            @Override
            public void onRepeat() {

            }

            @Override
            public void onStep(int i, double v) {

            }
        });
        InputStream inputStream = svgaView.getResources().openRawResource(svgaRes);
        parser.decodeFromInputStream(inputStream, "svgaCacheKey", new SVGAParser.ParseCompletion() {
            @Override
            public void onError() {
                if (listener != null) {
                    listener.onResult(PlayResult.PLAYERROR);
                }
            }

            @Override
            public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                if (listener != null) {
                    listener.onResult(PlayResult.START);
                }
                svgaView.setVisibility(View.VISIBLE);
                SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
                svgaView.setImageDrawable(drawable);
                svgaView.startAnimation();
            }
        }, true, null, null);
    }
}
