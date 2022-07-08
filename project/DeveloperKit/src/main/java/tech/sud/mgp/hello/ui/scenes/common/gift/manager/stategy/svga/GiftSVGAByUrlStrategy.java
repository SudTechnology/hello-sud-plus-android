package tech.sud.mgp.hello.ui.scenes.common.gift.manager.stategy.svga;

import tech.sud.mgp.hello.common.utils.SVGAUtils;
import tech.sud.mgp.hello.ui.scenes.common.gift.listener.PlayResultListener;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.stategy.PlayStrategy;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.PlayResult;

/**
 * 通过url播放svga的策略
 */
public class GiftSVGAByUrlStrategy extends PlayStrategy<GiftSVGAByUrlModel> {

    @Override
    public void play(GiftSVGAByUrlModel giftSVGAModel) {
        PlayResultListener playResultListener = giftSVGAModel.getPlayResultListener();
        try {
            SVGAUtils.loadByUrl(null, giftSVGAModel.giftModel.animationUrl, giftSVGAModel.svgaView, playResultListener);
        } catch (Exception e) {
            e.printStackTrace();
            if (playResultListener != null) {
                playResultListener.onResult(PlayResult.PLAYERROR);
            }
        }
    }

}
