package tech.sud.mgp.hello.ui.scenes.ai.manager;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

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

    @SuppressLint("MissingPermission")
    public AudioRecordManager() {
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);
    }

    // 开始录音
    public void startRecording() {
        if (isRecording) {
            return;
        }
        audioRecord.startRecording();
        isRecording = true;

        readAudioTask = new ReadAudioTask();
        readAudioTask.start();
    }

    // 停止录音，并返回读取到的音频录音，如果有
    public byte[] stopRecording() {
        if (!isRecording) {
            return null;
        }
        isRecording = false;

        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
        }

        if (readAudioTask != null) {
            return readAudioTask.stopAudioRecord();
        }
        return null;
    }

    private class ReadAudioTask extends Thread {
        ByteArrayOutputStream bos = null;
        private boolean taskIsRecording;

        @Override
        public void run() {
            super.run();
            taskIsRecording = true;
            writeAudioDataToBuffer();
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

        // 返回读取到的音频数据
        public byte[] stopAudioRecord() {
            taskIsRecording = false;
            if (bos != null) {
                return bos.toByteArray();
            }
            return null;
        }
    }

}

