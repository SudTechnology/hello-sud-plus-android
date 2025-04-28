package tech.sud.mgp.rtc.audio.impl.agora;

import java.nio.ByteBuffer;

import io.agora.mediaplayer.IMediaPlayerCustomDataProvider;

public class ByteArrayMediaPlayerDataProvider implements IMediaPlayerCustomDataProvider {

    private final byte[] data;
    private int position = 0; // 当前读取的位置（偏移量）

    public ByteArrayMediaPlayerDataProvider(byte[] data) {
        this.data = data;
    }

    @Override
    public int onReadData(ByteBuffer buffer, int bufferSize) {
        if (data == null || buffer == null || position >= data.length) {
            return 0; // 读取失败或没有数据了
        }

        // 计算还能读多少字节
        int remaining = data.length - position;
        int readSize = Math.min(bufferSize, remaining);

        // 把数据写入到 ByteBuffer 中
        buffer.put(data, position, readSize);

        // 更新当前读取位置
        position += readSize;

        return readSize;
    }

    @Override
    public long onSeek(long offset, int whence) {
        if (data == null) {
            return -1; // 定位失败
        }

        switch ((int) whence) {
            case 0: // SEEK_SET，起点是头部
                if (offset >= 0 && offset <= data.length) {
                    position = (int) offset;
                    return position;
                }
                break;

            case 1: // SEEK_CUR，起点是当前位置
                long newPos = position + offset;
                if (newPos >= 0 && newPos <= data.length) {
                    position = (int) newPos;
                    return position;
                }
                break;

            case 2: // SEEK_END，起点是尾部
                long endPos = data.length + offset;
                if (endPos >= 0 && endPos <= data.length) {
                    position = (int) endPos;
                    return position;
                }
                break;

            case 65536: // 返回文件大小
                return data.length;

            default:
                break;
        }

        // 定位失败
        return -1;
    }
}

