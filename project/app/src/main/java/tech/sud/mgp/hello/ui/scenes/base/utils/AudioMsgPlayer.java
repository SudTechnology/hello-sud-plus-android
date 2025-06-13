package tech.sud.mgp.hello.ui.scenes.base.utils;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;

import com.blankj.utilcode.util.LogUtils;

import java.util.LinkedList;
import java.util.Queue;

public class AudioMsgPlayer {

    private final Queue<byte[]> audioQueue = new LinkedList<>();
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public synchronized void play(String base64) {
        play(base64Decode(base64));
    }

    public synchronized void play(byte[] mp3Buffer) {
        if (mp3Buffer == null || mp3Buffer.length == 0) {
            return;
        }
        audioQueue.offer(mp3Buffer);
        if (!isPlaying) {
            playNext();
        }
    }

    private void playNext() {
        byte[] nextAudio = audioQueue.poll();
        if (nextAudio == null) {
            isPlaying = false;
            return;
        }
        isPlaying = true;
        playMp3(nextAudio);
    }

    private void playMp3(byte[] mp3Buffer) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            // 用 MemoryFile 作为临时存储
//            MemoryFile memoryFile = new MemoryFile("temp_audio", mp3Buffer.length);
//            memoryFile.writeBytes(mp3Buffer, 0, 0, mp3Buffer.length);
//
//            // 获取 FileDescriptor
//            FileDescriptor fd = ReflectUtils.reflect(memoryFile).method("getFileDescriptor").get();

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(new ByteArrayMediaDataSource(mp3Buffer));
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);

            // 播放完成时，继续播放队列中的下一个音频
            mediaPlayer.setOnCompletionListener(mp -> mainHandler.post(this::playNext));

            // 处理播放错误，防止队列阻塞
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                LogUtils.d("播放音频时发生错误，跳过当前音频：" + what);
                playNext(); // 继续播放队列中的下一个
                return true; // 表示我们已经处理了错误
            });

        } catch (Exception e) {
            LogUtils.d("音频播放失败：" + e.getMessage());
            isPlaying = false;
            playNext(); // 跳过当前音频，继续播放队列中的下一个
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
