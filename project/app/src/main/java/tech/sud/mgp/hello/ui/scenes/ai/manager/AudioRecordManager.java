package tech.sud.mgp.hello.ui.scenes.ai.manager;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


// 录音使用前先申请系统权限
public class AudioRecordManager {
    private static final int SAMPLE_RATE = 16000; // 采样率
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO; // 单声道输入
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT; // 16位PCM编码
    private AudioRecord audioRecord;
    private boolean isRecording = false;
    private int bufferSize;
    private ReadAudioTask readAudioTask;
    private OnAudioRecordListener onAudioRecordListener;

    public AudioRecordManager() {
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
    }

    @SuppressLint("MissingPermission")
    // 开始录音
    public void startRecording() {
        if (isRecording) {
            return;
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);
        audioRecord.startRecording();
        isRecording = true;

        readAudioTask = new ReadAudioTask();
        readAudioTask.start();
    }

    // 停止录音，并返回读取到的音频录音，如果有
    public void stopRecording() {
        if (!isRecording) {
            return;
        }
        isRecording = false;

        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
        }

        if (readAudioTask != null) {
            readAudioTask.stopAudioRecord();
        }
    }

    private class ReadAudioTask extends Thread {
        ByteArrayOutputStream bos = null;
        private boolean taskIsRecording;

        @Override
        public void run() {
            super.run();
            taskIsRecording = true;
            writeAudioDataToBuffer();
            new Handler(Looper.getMainLooper()).post(() -> {
                if (onAudioRecordListener != null) {
                    onAudioRecordListener.onAudioData(bos == null ? null : bos.toByteArray());
                }
            });
        }

        // 将音频数据写入文件
        private void writeAudioDataToBuffer() {
            byte[] audioData = new byte[bufferSize];
            try {
                bos = new ByteArrayOutputStream(bufferSize * 10);
                while (taskIsRecording) {
                    int read = audioRecord.read(audioData, 0, audioData.length);
                    if (read > 0) {
                        bos.write(audioData, 0, read);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bos != null) {
                        bos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopAudioRecord() {
            taskIsRecording = false;
        }
    }

    /** 设置音频数据监听，在结束时会回调 */
    public void setOnAudioRecordListener(OnAudioRecordListener onAudioRecordListener) {
        this.onAudioRecordListener = onAudioRecordListener;
    }

    public interface OnAudioRecordListener {
        /** 返回录音数据，注意判空 */
        void onAudioData(byte[] audioData);
    }

}

