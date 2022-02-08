package tech.sud.mgp.audio.gift.manager.stategy;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.DeviceUtils;
import com.ss.ugc.android.alpha_player.IMonitor;
import com.ss.ugc.android.alpha_player.IPlayerAction;
import com.ss.ugc.android.alpha_player.model.ScaleType;

import java.io.File;

import tech.sud.mgp.audio.gift.callback.PlayResultCallback;
import tech.sud.mgp.audio.gift.model.PlayResult;
import tech.sud.mgp.audio.gift.view.GiftVideoView;

public class GiftMp4Strategy extends PlayStrategy<GiftMp4Model> {
    @Override
    public void play(GiftMp4Model giftMp4Model) {
        loadMp4(giftMp4Model.getPath(), giftMp4Model.getMp4View(), giftMp4Model.getLifecycleOwner(), giftMp4Model.getCallback());
    }

    /**
     * 加载mp4
     */
    public void loadMp4(String filePath,
                        GiftVideoView mp4View,
                        LifecycleOwner lifecycleOwner,
                        PlayResultCallback callback) {
        if (lifecycleOwner != null) {
            mp4View.initPlayerController(mp4View.getContext(), lifecycleOwner, new IPlayerAction() {
                @Override
                public void onVideoSizeChanged(int i, int i1, @NonNull ScaleType scaleType) {

                }

                @Override
                public void startAction() {
                    callback.result(PlayResult.START);
                }

                @Override
                public void endAction() {
                    callback.result(PlayResult.PLAYEND);
                }
            }, new IMonitor() {
                @Override
                public void monitor(boolean b, @NonNull String s, int i, int i1, @NonNull String s1) {
                    if (!b) {
                        //加载mp4错误时候进行处理
                        callback.result(PlayResult.PLAYERROR);
                    }
                }
            });
        }

        if (lifecycleOwner == null) {
            callback.result(PlayResult.PLAYERROR);
        } else {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                mp4View.attachView();
                mp4View.startVideoGift(filePath, () -> callback.result(PlayResult.PLAYERROR));
            }else {
                callback.result(PlayResult.PLAYERROR);
            }
        }
    }

    /**
     * 是否展示静态图
     */
    public boolean showImage() {
        //模拟器显示静态图片
        boolean isEmulator = DeviceUtils.isEmulator();
        if (isEmulator) {
            return true;
        }
        return false;
    }

}