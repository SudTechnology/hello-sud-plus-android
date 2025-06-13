package tech.sud.mgp.hello.ui.scenes.base.utils;

import android.media.MediaDataSource;

import java.io.IOException;

public class ByteArrayMediaDataSource extends MediaDataSource {
    private final byte[] data;
    private final int length;
    private int position = 0;

    public ByteArrayMediaDataSource(byte[] data) {
        this.data = data;
        this.length = data.length;
    }

    @Override
    public int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
        if (position >= length) {
            return -1; // 读取完毕
        }

        int readSize = Math.min(size, length - (int) position);
        System.arraycopy(data, (int) position, buffer, offset, readSize);
        return readSize;
    }

    @Override
    public long getSize() {
        return length;
    }

    @Override
    public void close() {
        // 释放资源（如果有的话）
    }
}

