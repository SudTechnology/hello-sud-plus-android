package tech.sud.mgp.rtc.audio.impl.agora;

import android.util.Base64;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;

import java.util.ArrayList;
import java.util.List;

import io.agora.mediaplayer.Constants;
import io.agora.mediaplayer.IMediaPlayer;
import io.agora.mediaplayer.data.MediaPlayerSource;
import io.agora.rtc2.RtcEngine;
import tech.sud.mgp.rtc.audio.core.SudAudioPlayListener;

public class AgoraAudioMsgPlayer {
    private List<Object> mPlayerList = new ArrayList<>();

    public synchronized void play(RtcEngine engine, byte[] mp3Buffer, SudAudioPlayListener sudAudioPlayListener) {
        if (engine == null || mp3Buffer == null || mp3Buffer.length == 0) {
            return;
        }
        playMp3(engine, mp3Buffer, sudAudioPlayListener);
    }

    private void playMp3(RtcEngine engine, byte[] mp3Buffer, SudAudioPlayListener sudAudioPlayListener) {
        LogUtils.d("playMp3：" + mp3Buffer.length);
        IMediaPlayer mediaPlayer = engine.createMediaPlayer();
        try {
            mediaPlayer.registerPlayerObserver(new SimpleIMediaPlayerObserver() {
                @Override
                public void onPlayerStateChanged(Constants.MediaPlayerState state, Constants.MediaPlayerReason reason) {
                    LogUtils.d("onPlayerStateChanged:" + state);
                    if (state == Constants.MediaPlayerState.PLAYER_STATE_OPEN_COMPLETED) {
                        ThreadUtils.runOnUiThread(() -> {
                            mediaPlayer.play();
                            if (sudAudioPlayListener != null) {
                                sudAudioPlayListener.onPlaying();
                            }
                        });
                    } else if (state == Constants.MediaPlayerState.PLAYER_STATE_PLAYBACK_COMPLETED
                            || state == Constants.MediaPlayerState.PLAYER_STATE_FAILED
                            || state == Constants.MediaPlayerState.PLAYER_STATE_PLAYBACK_ALL_LOOPS_COMPLETED
                            || state == Constants.MediaPlayerState.PLAYER_STATE_STOPPED) {
                        ThreadUtils.runOnUiThread(() -> {
                            mediaPlayer.destroy();
                            mPlayerList.remove(mediaPlayer);
                            if (sudAudioPlayListener != null) {
                                sudAudioPlayListener.onCompleted();
                            }
                        });
                    }
                }
            });
            MediaPlayerSource mediaPlayerSource = new MediaPlayerSource();
            mediaPlayerSource.setProvider(new ByteArrayMediaPlayerDataProvider(mp3Buffer));
            mediaPlayer.openWithMediaSource(mediaPlayerSource);
            mPlayerList.add(mediaPlayer);
        } catch (Exception e) {
            LogUtils.d("音频播放失败：" + e.getMessage());
            mPlayerList.remove(mediaPlayer);
        }
    }

    /**
     * 将base64的字符串，转成byte数组
     *
     * @param base64Str
     * @return
     */
    public byte[] base64Decode(String base64Str) {
        if (base64Str == null || base64Str.length() == 0)
            return null;
        return Base64.decode(base64Str, Base64.NO_WRAP);
    }
}
