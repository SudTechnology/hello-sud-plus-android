package tech.sud.mgp.hello.ui.scenes.common.gift.manager;

import android.content.Context;

import androidx.annotation.RawRes;

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
            model1.path = context.getCacheDir().getAbsolutePath() + File.separator + "sud_svga_600.svga";
            model1.resId = R.raw.sud_svga_600;
            model1.giftImage = R.drawable.icon_gift_600;
            model1.giftSmallImage = R.drawable.icon_gift_128;
            model1.checkState = true;
            gifts.add(model1);

            GiftModel model2 = new GiftModel();
            model2.giftId = 2;
            model2.giftName = "lottie";
            model2.animationType = EffectAnimationFormat.JSON;
            model2.path = "sud_lottie_600.json";
            model2.giftImage = R.drawable.icon_gift_600;
            model2.giftSmallImage = R.drawable.icon_gift_128;
            gifts.add(model2);

            GiftModel model3 = new GiftModel();
            model3.giftId = 3;
            model3.giftName = "webp";
            model3.animationType = EffectAnimationFormat.WEBP;
            model3.resId = R.raw.sud_webp_600;
            model3.giftImage = R.drawable.icon_gift_600;
            model3.giftSmallImage = R.drawable.icon_gift_128;
            gifts.add(model3);

            copyFileToSdcrad(context,R.raw.sud_mp4_600,"sud_mp4_600.mp4");
            GiftModel model4 = new GiftModel();
            model4.giftId = 4;
            model4.giftName = "mp4";
            model4.animationType = EffectAnimationFormat.MP4;
            model4.resId = R.raw.sud_mp4_600;
            model4.path = context.getCacheDir().getAbsolutePath() + File.separator + "sud_mp4_600.mp4";
            model4.giftImage = R.drawable.icon_gift_600;
            model4.giftSmallImage = R.drawable.icon_gift_128;
            gifts.add(model4);

            this.gifts.clear();
            this.gifts.addAll(gifts);
        }
        return gifts;
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

    public void copyFileToSdcrad(Context context, @RawRes int resId, String fileName) {
        File file = new File(context.getCacheDir().getAbsolutePath() + File.separator + fileName);
        if (!file.exists() || !file.isFile()) {
            FileUtils.copyFilesFromRaw(context, resId, fileName, context.getCacheDir().getAbsolutePath());
        }
    }

}