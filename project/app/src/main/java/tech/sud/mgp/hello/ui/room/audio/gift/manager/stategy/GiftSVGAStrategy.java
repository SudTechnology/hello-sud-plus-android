package tech.sud.mgp.hello.ui.room.audio.gift.manager.stategy;

import android.view.View;

import androidx.annotation.NonNull;

import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import tech.sud.mgp.hello.ui.room.audio.gift.listener.PlayResultListener;
import tech.sud.mgp.hello.ui.room.audio.gift.model.PlayResult;

public class GiftSVGAStrategy extends PlayStrategy<GiftSVGAModel> {
    @Override
    public void play(GiftSVGAModel giftSVGAModel) {
        playSvgaAsset(giftSVGAModel.getPath(), giftSVGAModel.getSvgaView(), giftSVGAModel.getPlayResultListener());
    }

    /**
     * 播放svga（asset资源文件
     */
    public void playSvgaAsset(String svgaPath, SVGAImageView svgaView, PlayResultListener listener) {
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
        parser.decodeFromAssets(svgaPath, new SVGAParser.ParseCompletion() {
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
        }, null);
    }
}