package com.mammb.code.jsonstruct;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

public class Buffer implements Closeable {

    private final Reader reader;
    private char[] elements;
    private int readPos;
    private int readEnd;

    public Buffer(Reader reader, char[] elements) {
        this.reader = reader;
        this.elements = elements;
    }

    public static Buffer of(Reader reader) {
        return new Buffer(reader, new char[1024]);
    }

    public int read() {
        if (readPos == readEnd) {
            fillBuffer();
        }
        if (readPos >= readEnd) {
            return -1;
        }
        return elements[readPos++];
    }


    private int fillBuffer() {
        try {
            return readEnd += reader.read(elements, 0, elements.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void close() throws IOException {
        reader.close();
    }

}
