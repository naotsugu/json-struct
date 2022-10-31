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
package com.mammb.code.jsonstruct.processor.assembly;

import com.mammb.code.jsonstruct.lang.Iterate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Code block with import considerations.
 * @author Naotsugu Kobayashi
 */
public class Code {

    /** The line feed char. */
    private static final String LF = "\n";

    /** The indent string. */
    private static final String INDENT = "    ";

    /** The code lines. */
    private final List<String> lines;

    /** The imports. */
    private final Imports imports;


    /**
     * Constructor.
     */
    private Code(List<String> lines, Imports imports) {
        this.lines = Objects.requireNonNull(lines);
        this.imports = Objects.requireNonNull(imports);
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
     * @param literal the code string
     * @return the code
     */
    public static Code of(String literal) {
        return new Code(trimLines(literal.lines().toList()), Imports.of());
    }


    /**
     * Set level one indent.
     * @return this code
     */
    public Code indent() {
        return indent(1);
    }


    /**
     * Indent.
     * @param level the level of indent
     * @return this code
     */
    public Code indent(int level) {
        if (level <= 0) {
            return this;
        }
        for (int i = 0; i < lines.size(); i++) {
            lines.set(i, INDENT.repeat(level) + lines.get(i));
        }
        return this;
    }


    /**
     * Append the given literal at the end of last line.
     * @param literal to be appended literal
     * @return this code
     */
    public Code append(String literal) {
        int index = lines.size() - 1;
        lines.set(index, lines.get(index) + literal);
        return this;
    }


    /**
     * Interpolate the type value to this code by given key.
     * @param key the key name
     * @param type the type value to be interpolated
     * @return this code
     */
    public Code interpolateType(String key, String type) {
        applySubstitution(key, imports.apply(type));
        return this;
    }


    /**
     * Interpolate the code to this code by given key.
     * @param key the key name
     * @param content the content to be interpolated
     * @return this code
     */
    public Code interpolate(String key, String content) {
        applySubstitution(key, content);
        return this;
    }


    /**
     * Interpolate the code to this code by given key.
     * @param key the key name
     * @param other the code to be interpolated
     * @return this code
     */
    public Code interpolate(String key, Code other) {
        applySubstitution(key, other.content());
        add(other.imports);
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
     * {@code
     *   clazz.addBeforeLast("}", method.indent().addHead(""));
     * }
     * @param mark the point of line
     * @param code the code to be added
     */
    public void addBeforeLast(String mark, Code code) {
        for (int i = lines.size() - 1; i > 0; i--) {
            if (lines.get(i).trim().startsWith(mark)) {
                lines.addAll(i, code.lines);
                break;
            }
        }
        throw new RuntimeException("can not find curly close.");
    }


    /**
     * Add code.
     * @param code the code
     * @return this code
     */
    public Code add(Code code) {
        lines.addAll(code.lines);
        imports.marge(code.imports);
        return this;
    }


    /**
     * Add imports.
     * @param other Imports
     * @return this code
     */
    public Code add(Imports other) {
        imports.marge(other);
        return this;
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
        var content = new StringBuilder();
        for (Iterate.Entry<String> line : Iterate.of(lines)) {
            content.append(line.value());
            if (line.hasNext() && !line.value().endsWith(LF)) {
                content.append(LF);
            }
        }
        return content.toString();
    }


    /**
     * Get the imports.
     * @return the imports
     */
    public Imports imports() {
        return imports;
    }


    /**
     * Apply value in the code.
     * @param key the bind key
     * @param value the value to be bind
     */
    private void applySubstitution(String key, String value) {

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
                if (!lines.get(i).contains(key)) {
                    continue;
                }
                var indent = " ".repeat(lines.get(i).indexOf(key));
                var multi = lines.get(i).replace(key, value).split(LF);
                lines.set(i, multi[0]);
                for (int j = 1; j < multi.length; j++) {
                    lines.add(i + j, indent + multi[j]);
                }
                i += code.length;
            }
        }
    }


    /**
     * Trim the blank lines at head and tail.
     * <pre>
     *     [SOF]   ->    [SOF]
     *                   aa
     *       aa          bb
     *       bb          [EOF]
     *
     *     [EOF]
     * </pre>
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
