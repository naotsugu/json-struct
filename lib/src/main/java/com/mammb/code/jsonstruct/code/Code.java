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
package com.mammb.code.jsonstruct.code;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Code block with import considerations.
 * @author Naotsugu Kobayashi
 */
public class Code {

    /** The line feed char. */
    private static final String LF = "\n";

    /** The code lines. */
    private final List<String> lines;

    /** imports. */
    private final Imports imports;


    /**
     * Constructor.
     */
    public Code(List<String> lines, Imports imports) {
        this.lines = lines;
        this.imports = imports;
    }


    /**
     * Create the empty Code.
     * @return the empty code
     */
    public static Code of() {
        return new Code(new ArrayList<>(), Imports.of());
    }


    /**
     * Create the Code for the given code string.
     * @param code the code string
     * @return the code
     */
    public static Code of(String code) {
        return new Code(trimLines(code.lines().toList()), Imports.of());
    }


    /**
     * Join the given templates.
     * as the new line
     * @param codes the codes
     * @return joined code
     */
    public static Code join(Code... codes) {
        List<String> lines = new ArrayList<>();
        Imports imports = Imports.of();
        for (Code code : codes) {
            lines.addAll(code.lines);
            imports.marge(code.imports);
        }
        return new Code(lines, imports);
    }


    /**
     * Join the given codes with the given delimiter.
     * @param delimiter the delimiter
     * @param codes the codes
     * @return joined code
     */
    public static Code join(String delimiter, Code... codes) {

        List<String> list = new ArrayList<>();
        Imports imports = Imports.of();

        for (Code code : codes) {

            imports.marge(code.imports);

            if (code.lines.isEmpty()) {
                continue;
            }
            if (!list.isEmpty()) {
                // add delimiter at last line
                int index = list.size() - 1;
                list.set(index, list.get(index) + delimiter);
            }
            list.addAll(code.lines);

        }
        return new Code(list, imports);
    }


    /**
     * Add the code at the head.
     * @param code the code
     * @return this code
     */
    public Code addHead(String code) {
        lines.addAll(0, code.lines().toList());
        return this;
    }


    /**
     * Add the code at the tail.
     * @param str the code
     * @return this code
     */
    public Code addTail(String str) {
        lines.addAll(lines.size(), str.lines().toList());
        return this;
    }


    /**
     * Bind the value to the code by given key.
     * @param key the key name
     * @param value the value to be bind
     * @return this code
     */
    public Code bind(String key, String value) {
        applySubstitution(key, value);
        return this;
    }


    /**
     * Bind the value to the code by given key.
     * @param key the key name
     * @param other the code to be bind
     * @return this code
     */
    public Code bind(String key, Code other) {
        applySubstitution(key, other.content());
        return this;
    }


    /**
     * Clear the key.
     * @param key the name of key
     */
    public void clear(String key) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).trim().equals(key)) {
                lines.remove(i--);
            } else if (lines.get(i).contains(key)) {
                lines.set(i, lines.get(i).replace(key, ""));
            }
        }
    }


    /**
     * Add the code block in this template(maybe methods).
     * @param codeBlock the block of code
     */
    public void addAsEnclosed(String codeBlock) {
        for (int i = lines.size() - 1; i > 0; i--) {
            if (lines.get(i).trim().endsWith("}")) {
                var indent = " ".repeat((lines.get(i).indexOf('}') + 1) * 4);
                var codes = codeBlock.split(LF);
                for (int j = 0; j < codes.length; j++) {
                    lines.add(i + j, indent + codes[j]);
                }
                lines.add(i, ""); // add empty line
                break;
            }
        }
        throw new RuntimeException("can not find curly close.");
    }


    /**
     * Add the code block in this template(maybe methods).
     * @param codeBlock the block of code
     */
    public void addAsEnclosed(Code codeBlock) {
        addAsEnclosed(codeBlock.content());
    }


    /**
     * Get the content.
     * @return the content
     */
    public String content() {
        if (lines.isEmpty()) {
            return "";
        }
        if (lines.size() == 1) {
            return lines.get(0);
        }
        return lines.stream()
            .map(line -> line.endsWith(LF) ? line : line + LF)
            .collect(Collectors.joining());
    }


    /**
     * Apply value in the code.
     * @param key the bind key
     * @param value the value to be bind
     */
    private void applySubstitution(String key, String value) {

        if (value.isBlank()) {
            return;
        }

        var code = value.split(LF);
        if (code.length == 1) {
            // single line
            for (int i = 0; i < lines.size(); i++) {
                if (!lines.get(i).contains(key)) {
                    continue;
                }
                lines.set(i, lines.get(i).replace(key, code[0]));
            }

        } else {
            // if multi line, we treat the next line indent
            for (int i = 0; i < lines.size(); i++) {
                if (!lines.get(i).trim().equals(key)) {
                    continue;
                }
                var indent = " ".repeat(lines.get(i).indexOf(key));
                lines.set(i, lines.get(i).replace(key, code[0]));
                for (int j = 1; j < code.length; j++) {
                    lines.add(i + j, indent + code[j]);
                }
                i += code.length;
            }
        }
    }


    /**
     * Trim the blank lines at head and tail.
     * @param lines the target lines
     * @return the trimmed lines
     */
    private static List<String> trimLines(List<String> lines) {

        var ret = new ArrayList<>(lines);

        if (ret.isEmpty()) {
            return ret;
        }

        while (true) {
            if (ret.isEmpty()) break;
            if (ret.get(0).isBlank()) ret.remove(0);
            else break;
        }

        while (true) {
            int tail = ret.size() - 1;
            if (tail <= 0) break;
            if (ret.get(tail).isBlank()) ret.remove(tail);
            else break;
        }

        return ret;
    }

}
