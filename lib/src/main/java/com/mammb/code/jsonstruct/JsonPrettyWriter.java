/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mammb.code.jsonstruct;

import java.io.IOException;

/**
 * JsonPrettyWriter.
 *
 * @author Naotsugu Kobayashi
 */
public class JsonPrettyWriter implements Appendable {

    /** the appendable peer. */
    private final Appendable peer;

    /** the indent string. */
    private final String indent;

    /** the level of nest. */
    private int level;

    /** the level of nest. */
    private boolean inQuote;

    /** the previous character . */
    private char prev;


    /**
     * Constructor.
     * @param peer the appendable peer
     * @param indent the size of indent
     */
    private JsonPrettyWriter(Appendable peer, int indent) {
        this.peer = peer;
        this.indent = " ".repeat(indent);
    }


    /**
     * Create a new JsonPrettyWriter.
     * @param writer the appendable would be written to
     * @param indent the size of indent
     * @return a new JsonPrettyWriter
     */
    public static JsonPrettyWriter of(Appendable writer, int indent) {
        return new JsonPrettyWriter(writer, indent);
    }


    /**
     * Create a new JsonPrettyWriter.
     * @param writer the appendable would be written to
     * @return a new JsonPrettyWriter
     */
    public static JsonPrettyWriter of(Appendable writer) {
        return new JsonPrettyWriter(writer, 2);
    }


    /**
     * Generate pretty json string.
     * @param csq the source CharSequence
     * @return pretty json string
     */
    public static String toPrettyString(CharSequence csq) {
        return toPrettyString(csq, 2);
    }


    /**
     * Generate pretty json string.
     * @param csq the source CharSequence
     * @param indent the size of indent
     * @return pretty json string
     */
    public static String toPrettyString(CharSequence csq, int indent) {
        var sb = new StringBuilder();
        var writer = new JsonPrettyWriter(sb, indent);
        try {
            writer.append(csq);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }


    /**
     * Append a character with indent.
     * @param ch a character
     * @throws IOException If an I/O error occurs
     */
    private void appendInternal(char ch) throws IOException {
        switch (ch) {
            case '"' -> {
                if (!inQuote || prev != '\\') {
                    inQuote = !inQuote;
                }
                peer.append(ch);
            }
            case ' ' -> {
                if (inQuote)  peer.append(ch);
            }
            case '{', '[' -> {
                peer.append(ch);
                level++;
                peer.append('\n').append(indent.repeat(level));
            }
            case '}', ']' -> {
                level--;
                peer.append('\n').append(indent.repeat(level));
                peer.append(ch);
            }
            case ',' -> {
                peer.append(ch);
                if (!inQuote) {
                    peer.append('\n').append(indent.repeat(level));
                }
            }
            case ':' -> {
                peer.append(ch);
                if (!inQuote) {
                    peer.append(' ');
                }
            }
            default -> peer.append(ch);
        }
        prev = ch;
    }


    @Override
    public Appendable append(CharSequence csq) throws IOException {
        append(csq, 0, csq.length());
        return this;
    }


    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        for (int i = start; i < end; i++) {
            append(csq.charAt(i));
        }
        return this;
    }


    @Override
    public Appendable append(char c) throws IOException {
        appendInternal(c);
        return this;
    }

}
