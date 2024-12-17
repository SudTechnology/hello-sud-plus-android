package tech.sud.mgp.hello.ui.scenes.common.gift.video;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.video.VideoSize;
import com.ss.ugc.android.alpha_player.model.VideoInfo;
import com.ss.ugc.android.alpha_player.player.AbsPlayer;

import java.io.IOException;

public class ExoPlayerImpl extends AbsPlayer {

    private ExoPlayer exoPlayer;
    private MediaSource videoSource;
    private int currVideoWidth = 0;
    private int currVideoHeight = 0;
    private boolean isLooping = false;
    private Context context;

    public ExoPlayerImpl(Context context) {
        super(context);
        this.context = context;
    }

    private Player.Listener exoPlayerListener = new Player.Listener() {
        @Override
        public void onPlaybackStateChanged(@Player.State int playbackState) {
            if (playbackState == Player.STATE_READY) {
                if (exoPlayer.getPlayWhenReady() && getPreparedListener() != null) {
                    getPreparedListener().onPrepared();
                }
            } else if (playbackState == Player.STATE_ENDED) {
                if (getCompletionListener() != null) {
                    getCompletionListener().onCompletion();
                }
            }
        }

        @Override
        public void onPlayerError(PlaybackException error) {
            if (getErrorListener() != null) {
                getErrorListener().onError(0, 0, "ExoPlayer on error: " + Log.getStackTraceString(error));
            }
        }

        @Override
        public void onVideoSizeChanged(VideoSize videoSize) {
            // Handle video size change
            if (videoSize != null) {
                currVideoWidth = videoSize.width;
                currVideoHeight = videoSize.height;
            }
        }

        @Override
        public void onRenderedFirstFrame() {
            Player.Listener.super.onRenderedFirstFrame();
            if (getFirstFrameListener() != null) {
                getFirstFrameListener().onFirstFrame();
            }
        }
    };

    @NonNull
    @Override
    public String getPlayerType() {
        return "ExoPlayerImpl";
    }

    @NonNull
    @Override
    public VideoInfo getVideoInfo() {
        return new VideoInfo(currVideoWidth, currVideoHeight);
    }

    @Override
    public void initMediaPlayer() {
        exoPlayer = new ExoPlayer.Builder(context)
                .setTrackSelector(new DefaultTrackSelector(context))
                .build();
        exoPlayer.addListener(exoPlayerListener);
    }

    @Override
    public void pause() {
        exoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void prepareAsync() {
        exoPlayer.prepare(videoSource);
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void release() {
        exoPlayer.release();
    }

    @Override
    public void reset() {
        exoPlayer.stop(true);
    }

    @Override
    public void setDataSource(@NonNull String s) throws IOException {
        // 创建默认的数据源工厂
        DefaultDataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(context);

        // 创建媒体项
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(s));

        // 根据是否循环设置播放源
        if (isLooping) {
            videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem);
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL); // 设置循环模式
        } else {
            videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem);
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF); // 关闭循环模式
        }

        // 准备播放器
        exoPlayer.setMediaSource(videoSource);
        exoPlayer.prepare();
    }

    @Override
    public void setLooping(boolean b) {
        this.isLooping = b;
    }

    @Override
    public void setScreenOnWhilePlaying(boolean b) {

    }

    @Override
    public void setSurface(@NonNull Surface surface) {
        exoPlayer.setVideoSurface(surface);
    }

    @Override
    public void start() {
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void stop() {
        exoPlayer.stop();
    }
}
