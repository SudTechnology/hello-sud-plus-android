package tech.sud.mgp.hello.ui.scenes.common.gift.manager;

import android.app.Application;
import android.content.Context;

import androidx.annotation.RawRes;

import com.blankj.utilcode.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.app.APPConfig;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
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

            GiftModel model2 = new GiftModel();
            model2.giftId = 2;
            model2.giftName = "lottie";
            model2.animationType = EffectAnimationFormat.JSON;
            model2.path = "sud_lottie_600.json";
            model2.giftImage = R.drawable.icon_gift_600;
            model2.giftSmallImage = R.drawable.icon_gift_128;
            model2.giftPrice = 100;
            model2.isFeature = true;
            gifts.add(model2);

            GiftModel model3 = new GiftModel();
            model3.giftId = 3;
            model3.giftName = "webp";
            model3.animationType = EffectAnimationFormat.WEBP;
            model3.resId = R.raw.sud_webp_600;
            model3.giftImage = R.drawable.icon_gift_600;
            model3.giftSmallImage = R.drawable.icon_gift_128;
            model3.giftPrice = 1000;
            model3.isFeature = true;
            gifts.add(model3);

            copyFileToSdcrad(context, R.raw.sud_mp4_600, "sud_mp4_600.mp4");
            GiftModel model4 = new GiftModel();
            model4.giftId = 4;
            model4.giftName = "mp4";
            model4.animationType = EffectAnimationFormat.MP4;
            model4.resId = R.raw.sud_mp4_600;
            model4.path = context.getCacheDir().getAbsolutePath() + File.separator + "sud_mp4_600.mp4";
            model4.giftImage = R.drawable.icon_gift_600;
            model4.giftSmallImage = R.drawable.icon_gift_128;
            model4.giftPrice = 10000;
            model4.isFeature = true;
            model4.isEffect = true;
            gifts.add(model4);

            GiftModel model9 = new GiftModel();
            model9.giftId = GiftId.ROCKET;
            model9.giftName = Utils.getApp().getString(R.string.custom_rocket);
            model9.animationType = EffectAnimationFormat.ROCKET;
            model9.giftImage = R.drawable.ic_rocket;
            model9.giftSmallImage = R.drawable.ic_rocket;
            model9.giftPrice = APPConfig.ROCKET_FIRE_PRICE;
            gifts.add(model9);

            GiftModel model1 = new GiftModel();
            model1.giftId = 1;
            model1.giftName = "svga";
            model1.animationType = EffectAnimationFormat.SVGA;
            model1.path = context.getCacheDir().getAbsolutePath() + File.separator + "sud_svga_600.svga";
            model1.resId = R.raw.sud_svga_600;
            model1.giftImage = R.drawable.icon_gift_600;
            model1.giftSmallImage = R.drawable.icon_gift_128;
            model1.checkState = true;
            model1.giftPrice = 1;
            gifts.add(model1);

            this.gifts.clear();
            this.gifts.addAll(gifts);
        }
        return gifts;
    }

    public GiftModel getGift(long giftId) {
        for (GiftModel model : gifts) {
            if (giftId == model.giftId) {
                return model;
            }
        }
        return createGiftModel(giftId);
    }

    /**
     * 除去通用的内置礼物
     * id:5 为蹦迪场景 跳舞1分钟
     * id:6 为蹦迪场景 跳舞3分钟
     * id:7 为蹦迪场景 跳舞插队
     *
     * @param giftId
     * @return
     */
    public GiftModel createGiftModel(long giftId) {
        Application context = Utils.getApp();
        if (giftId == 5) {
            GiftModel model = new GiftModel();
            model.giftId = giftId;
            model.giftName = context.getString(R.string.dancing_minute_count, "1");
            model.animationType = EffectAnimationFormat.SVGA;
            model.path = context.getCacheDir().getAbsolutePath() + File.separator + "sud_svga_600.svga";
            model.resId = R.raw.sud_svga_600;
            model.giftImage = R.drawable.icon_gift_600;
            model.giftSmallImage = R.drawable.icon_gift_128;
            model.giftPrice = 50;
            return model;
        }
        if (giftId == 6) {
            GiftModel model = new GiftModel();
            model.giftId = giftId;
            model.giftName = context.getString(R.string.dancing_minute_count, "3");
            model.animationType = EffectAnimationFormat.SVGA;
            model.path = context.getCacheDir().getAbsolutePath() + File.separator + "sud_svga_600.svga";
            model.resId = R.raw.sud_svga_600;
            model.giftImage = R.drawable.icon_gift_600;
            model.giftSmallImage = R.drawable.icon_gift_128;
            model.giftPrice = 150;
            return model;
        }
        if (giftId == 7) {
            GiftModel model = new GiftModel();
            model.giftId = giftId;
            model.giftName = context.getString(R.string.dance_top);
            model.animationType = EffectAnimationFormat.SVGA;
            model.path = context.getCacheDir().getAbsolutePath() + File.separator + "sud_svga_600.svga";
            model.resId = R.raw.sud_svga_600;
            model.giftImage = R.drawable.icon_gift_600;
            model.giftSmallImage = R.drawable.icon_gift_128;
            model.giftPrice = 1500;
            return model;
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