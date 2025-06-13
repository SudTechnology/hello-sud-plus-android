package tech.sud.mgp.rtc.audio.impl.agora;

import io.agora.mediaplayer.Constants;
import io.agora.mediaplayer.IMediaPlayerObserver;
import io.agora.mediaplayer.data.CacheStatistics;
import io.agora.mediaplayer.data.PlayerPlaybackStats;
import io.agora.mediaplayer.data.PlayerUpdatedInfo;
import io.agora.mediaplayer.data.SrcInfo;

public class SimpleIMediaPlayerObserver implements IMediaPlayerObserver {
    @Override
    public void onPlayerStateChanged(Constants.MediaPlayerState state, Constants.MediaPlayerReason reason) {

    }

    @Override
    public void onPositionChanged(long positionMs, long timestampMs) {

    }

    @Override
    public void onPlayerEvent(Constants.MediaPlayerEvent eventCode, long elapsedTime, String message) {

    }

    @Override
    public void onMetaData(Constants.MediaPlayerMetadataType type, byte[] data) {

    }

    @Override
    public void onPlayBufferUpdated(long playCachedBuffer) {

    }

    @Override
    public void onPreloadEvent(String src, Constants.MediaPlayerPreloadEvent event) {

    }

    @Override
    public void onAgoraCDNTokenWillExpire() {

    }

    @Override
    public void onPlayerSrcInfoChanged(SrcInfo from, SrcInfo to) {

    }

    @Override
    public void onPlayerInfoUpdated(PlayerUpdatedInfo info) {

    }

    @Override
    public void onPlayerCacheStats(CacheStatistics stats) {

    }

    @Override
    public void onPlayerPlaybackStats(PlayerPlaybackStats stats) {

    }

    @Override
    public void onAudioVolumeIndication(int volume) {

    }
}
