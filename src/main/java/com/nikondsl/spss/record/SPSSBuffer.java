package com.nikondsl.spss.record;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
class SPSSBuffer {
    private final int max_length;
    private final ByteArray compressed;
    private final ByteArray data;

    SPSSBuffer(int max_length) {
        this.max_length = max_length;
        int max = max_length >= 16 ? max_length : 16;
        compressed = ByteArray.createByteArray("compressed", max);
        data = ByteArray.createByteArray("data", max * 8);
    }

    void clear() {
        compressed.clear();
        data.clear();
    }

    private void delFromBuffer(int length) {
        if (length <= 0) return;
        int totalBytesToBeDeletedFromCompressed = 0;
        int totalBytesToBeDeletedFromData = 0;
        for (int i = 0; i < length; i++) {
            byte fromCompressed = compressed.getByteAt(i);
            totalBytesToBeDeletedFromCompressed++;
            if (fromCompressed == CompressBufferPredefinedCodes.COMPRESS_NOT_COMPRESSED.getCode()) {
                totalBytesToBeDeletedFromData += 8;
            }
        }
        if (totalBytesToBeDeletedFromCompressed > 0) compressed.shrinkFromStart(totalBytesToBeDeletedFromCompressed);
        if (totalBytesToBeDeletedFromData > 0) data.shrinkFromStart(totalBytesToBeDeletedFromData);
    }

    void addCompressedByte(byte value) {
        compressed.addByte(value);
    }

    void addCompressedBytes(byte[] values) {
        compressed.addBytes(values);
    }

    void addDataBytes(byte[] values) {
        data.addBytes(values);
    }

    byte[] getCompressedBytes() {
        return compressed.getArray();
    }

    byte[] getDataBytes() {
        return data.getArray();
    }

    int getCompressedBytesLength() {
        return compressed.getLength();
    }

    void flushBuffer(OutputStream outputStream) throws IOException {
        if (compressed.getLength() < 8) return;
        while (compressed.getLength() >= 8) {
            compressed.write(outputStream, 0, 8);
            int offset = 0;
            for (int i = 0; i < 8; i++) {
                if (compressed.getByteAt(i) != CompressBufferPredefinedCodes.COMPRESS_NOT_COMPRESSED.getCode())
                    continue;
                if (data.getLength() < offset + 8) {
                    break;
                }
                try {
                    data.write(outputStream, offset, 8);
                    offset += 8;
                } catch (Exception ex) {
                    log.error("Fatal buffer status while writing to output.", ex);
                }
            }
            delFromBuffer(8);
        }
    }

    public int getCurrentLength() {
        return data.current_length;
    }

    public int getMaxLength() {
        return max_length;
    }
}