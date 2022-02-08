package tech.sud.mgp.audio.gift.manager;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.audio.R;
import tech.sud.mgp.audio.example.model.AudioRoomMicModel;
import tech.sud.mgp.audio.example.model.UserInfo;
import tech.sud.mgp.audio.gift.model.EffectAnimationFormat;
import tech.sud.mgp.audio.gift.model.GiftModel;
import tech.sud.mgp.audio.gift.model.MicUserInfoModel;
import tech.sud.mgp.audio.gift.utils.FileUtils;

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
            model3.resId = R.raw.audio_webp_600;
            model3.giftImage = R.drawable.audio_webp_600;
            model3.giftSmallImage = R.drawable.audio_webp_128;
            gifts.add(model3);

            copyMp4ToSdcrad(context);
            GiftModel model4 = new GiftModel();
            model4.giftId = 4;
            model4.giftName = "mp4";
            model4.animationType = EffectAnimationFormat.MP4;
            model4.resId = R.raw.audio_mp4_600;
            model4.path = context.getCacheDir().getAbsolutePath() + File.separator + "audio_mp4_600.mp4";
            model4.giftImage = R.drawable.audio_mp4_600;
            model4.giftSmallImage = R.drawable.audio_mp4_128;
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
        Log.i("getGift  ", testIndex + "");
        GiftModel model = this.gifts.get(testIndex);
        return model;
    }

    public UserInfo testCreatUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.icon = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2F2021%2Fedpic%2Fc4%2F9f%2F09%2Fc49f090757360f843141fe2bab2cfc8f_1.jpg";
        userInfo.name = "阿娇安静案件";
        userInfo.userID = 100866;
        inMic = false;
        underMicUser = userInfo;
        return userInfo;
    }

    public List<MicUserInfoModel> testMicsUser() {
        List<MicUserInfoModel> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MicUserInfoModel model = new MicUserInfoModel();
            model.checked = false;
            model.indexMic = i;
            AudioRoomMicModel micModel = new AudioRoomMicModel();
            if (i > 1) {
                micModel.avatar = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2F2021%2Fedpic%2Fc4%2F9f%2F09%2Fc49f090757360f843141fe2bab2cfc8f_1.jpg";
            } else {
                micModel.avatar = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fedpic%2Ff7%2F80%2F97%2Ff7809705cbe5c0fc580e401270522a0a.jpg";
            }
            micModel.userId = 12345 + i;
            micModel.micIndex = i;
            micModel.nickName = i + "Name";
            model.userInfo = micModel;
            users.add(model);
        }
        inMic = true;
        inMics.clear();
        inMics.addAll(users);
        return users;
    }
}