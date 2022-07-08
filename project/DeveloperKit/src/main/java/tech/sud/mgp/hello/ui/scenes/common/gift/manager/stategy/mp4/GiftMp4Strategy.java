package tech.sud.mgp.hello.ui.scenes.common.gift.manager.stategy.mp4;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.DeviceUtils;
import com.ss.ugc.android.alpha_player.IMonitor;
import com.ss.ugc.android.alpha_player.IPlayerAction;
import com.ss.ugc.android.alpha_player.model.ScaleType;

import java.io.File;

import tech.sud.mgp.hello.ui.scenes.common.gift.listener.PlayResultListener;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.stategy.PlayStrategy;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.PlayResult;
import tech.sud.mgp.hello.ui.scenes.common.gift.view.GiftVideoView;

public class GiftMp4Strategy extends PlayStrategy<GiftMp4Model> {
    @Override
    public void play(GiftMp4Model giftMp4Model) {
        loadMp4(giftMp4Model.getPath(), giftMp4Model.getMp4View(), giftMp4Model.getLifecycleOwner(), giftMp4Model.getPlayResultListener());
    }

    /**
     * 加载mp4
     */
    public void loadMp4(String filePath,
                        GiftVideoView mp4View,
                        LifecycleOwner lifecycleOwner,
                        PlayResultListener listener) {
        if (lifecycleOwner != null) {
            mp4View.initPlayerController(mp4View.getContext(), lifecycleOwner, new IPlayerAction() {
                @Override
                public void onVideoSizeChanged(int i, int i1, @NonNull ScaleType scaleType) {

                }

                @Override
                public void startAction() {
                    listener.onResult(PlayResult.START);
                }

                @Override
                public void endAction() {
                    listener.onResult(PlayResult.PLAYEND);
                }
            }, new IMonitor() {
                @Override
                public void monitor(boolean b, @NonNull String s, int i, int i1, @NonNull String s1) {
                    if (!b) {
                        //加载mp4错误时候进行处理
                        listener.onResult(PlayResult.PLAYERROR);
                    }
                }
            });
        }

        if (lifecycleOwner == null) {
            listener.onResult(PlayResult.PLAYERROR);
        } else {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                mp4View.attachView();
                mp4View.startVideoGift(filePath, () -> listener.onResult(PlayResult.PLAYERROR));
            } else {
                listener.onResult(PlayResult.PLAYERROR);
            }
        }
    }

    /**
     * 是否模拟器
     */
    public boolean isEmulator() {
        return DeviceUtils.isEmulator();
    }
}