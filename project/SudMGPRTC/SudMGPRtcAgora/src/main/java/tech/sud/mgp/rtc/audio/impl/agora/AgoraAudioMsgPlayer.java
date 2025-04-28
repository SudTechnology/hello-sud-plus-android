package tech.sud.mgp.rtc.audio.impl.agora;

import android.util.Base64;

import com.blankj.utilcode.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.agora.mediaplayer.Constants;
import io.agora.mediaplayer.IMediaPlayer;
import io.agora.mediaplayer.data.MediaPlayerSource;
import io.agora.rtc2.RtcEngine;

public class AgoraAudioMsgPlayer {
    private List<Object> mPlayerList = new ArrayList<>();

    public synchronized void play(RtcEngine engine, byte[] mp3Buffer) {
        if (engine == null || mp3Buffer == null || mp3Buffer.length == 0) {
            return;
        }
        playMp3(engine, mp3Buffer);
    }

    private void playMp3(RtcEngine engine, byte[] mp3Buffer) {
        LogUtils.d("playMp3：" + mp3Buffer.length);
        IMediaPlayer mediaPlayer = engine.createMediaPlayer();
        try {
            mediaPlayer.registerPlayerObserver(new SimpleIMediaPlayerObserver() {
                @Override
                public void onPlayerStateChanged(Constants.MediaPlayerState state, Constants.MediaPlayerReason reason) {
                    if (state == Constants.MediaPlayerState.PLAYER_STATE_OPEN_COMPLETED) {
                        mediaPlayer.play();
                    } else if (state == Constants.MediaPlayerState.PLAYER_STATE_PLAYBACK_COMPLETED
                            || state == Constants.MediaPlayerState.PLAYER_STATE_FAILED) {
                        mediaPlayer.destroy();
                        mPlayerList.remove(mediaPlayer);
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
