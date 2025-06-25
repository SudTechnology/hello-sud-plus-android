package tech.sud.mgp.hello.common.utils;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ThreadUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WavRecorder {
    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private int totalPcmBytes = 0; // 累加录音数据长度
    private AudioRecord audioRecord;
    private boolean isRecording = false;
    private Thread recordingThread;
    private File wavFile;
    private OnRecordListener mOnRecordListener;
    private int maxDuraionSeconds = 20;

    public WavRecorder(File outputFile, int maxDuraionSeconds) {
        this.wavFile = outputFile;
        this.maxDuraionSeconds = maxDuraionSeconds;
    }

    @SuppressLint("MissingPermission")
    public void startRecording() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize);

        audioRecord.startRecording();
        isRecording = true;

        recordingThread = new Thread(() -> writeAudioDataToFile(bufferSize), "AudioRecorder Thread");
        recordingThread.start();
    }

    /** 会返回时长 */
    public void stopRecording() {
        if (audioRecord != null) {
            isRecording = false;
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            recordingThread = null;

            // 录制完成后补写 WAV 头部
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        writeWavHeader(wavFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ThreadUtils.runOnUiThread(() -> {
                        if (mOnRecordListener != null) {
                            mOnRecordListener.onRecordCompleted(getDurationSeconds(), getWavFile());
                        }
                    });
                }
            }.start();
        }
    }

    private void writeAudioDataToFile(int bufferSize) {
        byte[] data = new byte[bufferSize];
        if (wavFile.exists()) {
            wavFile.delete();
        } else {
            FileUtils.createOrExistsDir(wavFile);
        }
        try (FileOutputStream os = new FileOutputStream(wavFile)) {
            // 先写一个占位头
            os.write(new byte[44]);

            while (isRecording) {
                int read = audioRecord.read(data, 0, bufferSize);
                if (read > 0) {
                    os.write(data, 0, read);
                    totalPcmBytes += read;
                }
                if (getDurationSeconds() >= maxDuraionSeconds) {
                    ThreadUtils.runOnUiThread(this::stopRecording);
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeWavHeader(File wavFile) throws IOException {
        byte[] pcmData = readFileToByteArray(wavFile);
        int totalAudioLen = pcmData.length - 44;
        int byteRate = SAMPLE_RATE * 2; // 16位单声道

        ByteBuffer header = ByteBuffer.allocate(44);
        header.order(ByteOrder.LITTLE_ENDIAN);

        header.put("RIFF".getBytes());
        header.putInt(36 + totalAudioLen);
        header.put("WAVE".getBytes());
        header.put("fmt ".getBytes());
        header.putInt(16); // Subchunk1Size for PCM
        header.putShort((short) 1); // PCM = 1
        header.putShort((short) 1); // Mono
        header.putInt(SAMPLE_RATE);
        header.putInt(byteRate);
        header.putShort((short) 2); // block align
        header.putShort((short) 16); // bits per sample
        header.put("data".getBytes());
        header.putInt(totalAudioLen);

        // 把 header + pcm 重新写入文件
        try (FileOutputStream out = new FileOutputStream(wavFile)) {
            out.write(header.array());
            out.write(pcmData, 44, totalAudioLen);
        }
    }

    private byte[] readFileToByteArray(File file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FileInputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        return outputStream.toByteArray();
    }

    public int getDurationSeconds() {
        return (int) (totalPcmBytes / (float) (SAMPLE_RATE * 2)); // 每秒字节数 = 16000×2
    }

    public File getWavFile() {
        return wavFile;
    }

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        mOnRecordListener = onRecordListener;
    }

    public interface OnRecordListener {
        void onRecordCompleted(int durationSeconds, File file);
    }

}

