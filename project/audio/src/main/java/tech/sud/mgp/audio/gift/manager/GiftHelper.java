package tech.sud.mgp.audio.gift.manager;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.audio.R;
import tech.sud.mgp.audio.gift.model.EffectAnimationFormat;
import tech.sud.mgp.audio.gift.model.GiftModel;

public class GiftHelper {

    private static GiftHelper helper;
    private List<GiftModel> gifts = new ArrayList<>();

    private GiftHelper() {
    }

    public static GiftHelper getInstance() {
        if (helper == null) {
            synchronized (GiftHelper.class) {
                if (helper == null) {
                    helper = new GiftHelper();
                }
            }
        }
        return helper;
    }

    public List<GiftModel> creatGifts() {
        if (this.gifts.size() == 0){
            List<GiftModel> gifts = new ArrayList<>();
            GiftModel model1 = new GiftModel();
            model1.giftId = 1;
            model1.giftName = "svga";
            model1.animationType = EffectAnimationFormat.SVGA;
            model1.path = "audio_svga_600.svga";
            model1.giftImage = R.drawable.audio_svga_600;
            model1.giftSmallImage = R.drawable.audio_svga_128;
            gifts.add(model1);

            GiftModel model2 = new GiftModel();
            model2.giftId = 2;
            model2.giftName = "lottie";
            model2.animationType = EffectAnimationFormat.JSON;
            model2.path = "audio_lottie_600.json";
            model2.giftImage = R.drawable.audio_lottie_600;
            model2.giftSmallImage = R.drawable.audio_lottie_128;
            gifts.add(model2);

            GiftModel model3 = new GiftModel();
            model3.giftId = 3;
            model3.giftName = "webp";
            model3.animationType = EffectAnimationFormat.WEBP;
            model3.path = "audio_webp_600.webp";
            model3.giftImage = R.drawable.audio_webp_600;
            model3.giftSmallImage = R.drawable.audio_webp_128;
            gifts.add(model3);

            GiftModel model4 = new GiftModel();
            model4.giftId = 4;
            model4.giftName = "mp4";
            model4.animationType = EffectAnimationFormat.MP4;
            model4.path = "audio_mp4_600.mp4";
            model4.giftImage = R.drawable.audio_mp4_128;
            model4.giftSmallImage = R.drawable.audio_mp4_128;
            gifts.add(model4);

            this.gifts.clear();
            this.gifts.addAll(gifts);
        }
        return gifts;
    }
}