package tech.sud.mgp.hello.ui.scenes.common.gift.manager.stategy.svga;

import com.opensource.svgaplayer.SVGAImageView;

import tech.sud.mgp.hello.ui.scenes.common.gift.model.PlayGiftModel;

public class GiftSVGAModel extends PlayGiftModel {
    private SVGAImageView svgaView;

    public SVGAImageView getSvgaView() {
        return svgaView;
    }

    public void setSvgaView(SVGAImageView svgaView) {
        this.svgaView = svgaView;
    }
}
