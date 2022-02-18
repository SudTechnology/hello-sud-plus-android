package tech.sud.mgp.hello.ui.scenes.common.gift.manager;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.scenes.audio.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.EffectAnimationFormat;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.MicUserInfoModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.utils.FileUtils;

public class GiftHelper {

    private static GiftHelper helper;
    private List<GiftModel> gifts = new ArrayList<>();
    private int testIndex = 0;
    public boolean inMic = false;
    public UserInfo underMicUser;
    public List<MicUserInfoModel> inMics = new ArrayList<>();

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

    public List<GiftModel> creatGifts(Context context) {
        if (this.gifts.size() == 0) {
            List<GiftModel> gifts = new ArrayList<>();
            GiftModel model1 = new GiftModel();
            model1.giftId = 1;
            model1.giftName = "svga";
            model1.animationType = EffectAnimationFormat.SVGA;
            model1.resId = R.raw.audio_svga_600;
            model1.giftImage = R.drawable.svga_600;
            model1.giftSmallImage = R.drawable.svga_128;
            model1.checkState = true;
            gifts.add(model1);

            GiftModel model2 = new GiftModel();
            model2.giftId = 2;
            model2.giftName = "lottie";
            model2.animationType = EffectAnimationFormat.JSON;
            model2.path = "audio_lottie_600.json";
            model2.giftImage = R.drawable.lottie_600;
            model2.giftSmallImage = R.drawable.lottie_128;
            gifts.add(model2);

            GiftModel model3 = new GiftModel();
            model3.giftId = 3;
            model3.giftName = "webp";
            model3.animationType = EffectAnimationFormat.WEBP;
            model3.resId = R.raw.audio_webp_600;
            model3.giftImage = R.drawable.webp_600;
            model3.giftSmallImage = R.drawable.webp_128;
            gifts.add(model3);

            copyMp4ToSdcrad(context);
            GiftModel model4 = new GiftModel();
            model4.giftId = 4;
            model4.giftName = "mp4";
            model4.animationType = EffectAnimationFormat.MP4;
            model4.resId = R.raw.audio_mp4_600;
            model4.path = context.getCacheDir().getAbsolutePath() + File.separator + "audio_mp4_600.mp4";
            model4.giftImage = R.drawable.mp4_600;
            model4.giftSmallImage = R.drawable.mp4_128;
            gifts.add(model4);

            this.gifts.clear();
            this.gifts.addAll(gifts);
        }
        return gifts;
    }

    public void copyMp4ToSdcrad(Context context) {
        File file = new File(context.getCacheDir().getAbsolutePath() + File.separator + "audio_mp4_600.mp4");
        if (!file.exists() || !file.isFile()) {
            FileUtils.copyFilesFromRaw(context, R.raw.audio_mp4_600, "audio_mp4_600.mp4", context.getCacheDir().getAbsolutePath());
        }
    }

    public GiftModel getGift(int giftId) {
        for (GiftModel model : gifts) {
            if (giftId == model.giftId) {
                return model;
            }
        }
        return null;
    }

    public GiftModel getCheckedGift() {
        for (GiftModel giftModel : gifts) {
            if (giftModel.checkState) {
                return giftModel;
            }
        }
        return null;
    }

    public GiftModel getGift() {
        if (testIndex < this.gifts.size() - 1) {
            testIndex++;
        } else {
            testIndex = 0;
        }
        GiftModel model = this.gifts.get(testIndex);
        return model;
    }

}