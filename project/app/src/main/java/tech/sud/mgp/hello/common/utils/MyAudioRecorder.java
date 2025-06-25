package tech.sud.mgp.hello.common.utils;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.SystemClock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 录音工具
 */
public class MyAudioRecorder {

    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord audioRecord;
    private int bufferSize;
    private boolean isRecording = false;
    private File outputFile;
    private long startTime;
    private Thread recordThread;

    public interface OnStopResult {
        /**
         * 录音结束
         *
         * @param durationMillis 录音时长
         * @param filePath       录音文件保存路径
         */
        void onResult(long durationMillis, String filePath);
    }

    public MyAudioRecorder() {
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
    }

    /** 开始录音，调用前自行先处理权限申请的问题 */
    @SuppressLint("MissingPermission")
    public void startRecording(String filePath) throws IOException {
        outputFile = new File(filePath);
        if (outputFile.exists()) {
            outputFile.delete();
        }

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
        );

        isRecording = true;
        audioRecord.startRecording();
        startTime = SystemClock.elapsedRealtime();

        recordThread = new Thread(() -> {
            try (FileOutputStream os = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[bufferSize];
                while (isRecording) {
                    int read = audioRecord.read(buffer, 0, buffer.length);
                    if (read > 0) {
                        os.write(buffer, 0, read);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "AudioRecord Thread");

        recordThread.start();
    }

    /** 结束录音 */
    public void stopRecording(OnStopResult callback) {
        isRecording = false;
        if (audioRecord != null) {
            try {
                audioRecord.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace(); // 录音已停止
            }
            audioRecord.release();
            audioRecord = null;
        }

        try {
            if (recordThread != null) {
                recordThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long duration = SystemClock.elapsedRealtime() - startTime;
        if (callback != null) {
            callback.onResult(duration, outputFile != null ? outputFile.getAbsolutePath() : null);
        }
    }
}
