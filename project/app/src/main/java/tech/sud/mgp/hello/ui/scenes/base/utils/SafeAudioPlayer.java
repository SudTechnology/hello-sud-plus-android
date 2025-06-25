package tech.sud.mgp.hello.ui.scenes.base.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SafeAudioPlayer {
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private final Context context;
    private File tempFile = null;

    public SafeAudioPlayer(Context context) {
        this.context = context.getApplicationContext();
    }

    // 播放指定路径的音频文件
    public void playFromPath(String filePath) {
        if (isPlaying) return;
        File file = new File(filePath);
        if (!file.exists()) return;

        playInternal(Uri.fromFile(file));
    }

    // 播放二进制数据（会写入临时文件）
    public void playFromBytes(byte[] audioBytes, String fileExtension) {
        if (isPlaying) return;

        try {
            tempFile = File.createTempFile("audio_temp", fileExtension, context.getCacheDir());
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(audioBytes);
            fos.close();

            playInternal(Uri.fromFile(tempFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 核心播放逻辑（内部使用）
    private void playInternal(Uri uri) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.setOnPreparedListener(mp -> {
                isPlaying = true;
                mp.start();
            });
            mediaPlayer.setOnCompletionListener(mp -> releasePlayer());
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                releasePlayer();
                return true;
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            releasePlayer();
        }
    }

    // 停止播放 + 清理资源
    private void releasePlayer() {
        isPlaying = false;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // 删除临时文件
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
            tempFile = null;
        }
    }

    // 外部可调用停止
    public void stop() {
        releasePlayer();
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}

