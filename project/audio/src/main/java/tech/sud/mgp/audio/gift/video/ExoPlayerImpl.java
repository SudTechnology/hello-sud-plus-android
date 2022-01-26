package tech.sud.mgp.audio.gift.video;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import com.ss.ugc.android.alpha_player.model.VideoInfo;
import com.ss.ugc.android.alpha_player.player.AbsPlayer;

import java.io.IOException;

public class ExoPlayerImpl extends AbsPlayer {

    private SimpleExoPlayer exoPlayer;
    private MediaSource videoSource;
    private DefaultDataSourceFactory dataSourceFactory;
    private int currVideoWidth = 0;
    private int currVideoHeight = 0;
    private boolean isLooping = false;
    private Context context;

    public ExoPlayerImpl(Context context) {
        dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "player"));
        this.context = context;
    }

    private Player.EventListener exoPlayerListener = new Player.EventListener() {
        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        }
    };

    private VideoListener exoVideoListener = new VideoListener() {
        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

        }

        @Override
        public void onRenderedFirstFrame() {

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
        exoPlayer = ExoPlayerFactory.newSimpleInstance(context);
        exoPlayer.addListener(exoPlayerListener);
        exoPlayer.addVideoListener(exoVideoListener);
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
        if (isLooping) {
            ExtractorMediaSource extractorMediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(s));
            videoSource = new LoopingMediaSource(extractorMediaSource);
        } else {
            videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(s));
        }
        reset();
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
