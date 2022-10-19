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
package com.mammb.code.jsonstruct.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a code block.
 * @author Naotsugu Kobayashi
 */
public class CodeTemplate {

    /** The line feed char. */
    private static final String LF = "\n";

    /** The code lines. */
    private final List<String> lines;


    /**
     * Constructor.
     * @param lines the string lines
     */
    private CodeTemplate(List<String> lines) {
        this.lines = lines;
    }


    /**
     * Create the empty CodeTemplate.
     * @return the empty CodeTemplate
     */
    public static CodeTemplate of() {
        return new CodeTemplate(new ArrayList<>());
    }


    /**
     * Create the CodeTemplate for the given code string.
     * @param code the code string
     * @return the CodeTemplate
     */
    public static CodeTemplate of(String code) {
        return new CodeTemplate(
            trimLines(code.lines().collect(Collectors.toList())));
    }


    /**
     * Create the CodeTemplate for the given code strings.
     * @param codes the code strings
     * @return the CodeTemplate
     */
    public static CodeTemplate of(List<String> codes) {
        return new CodeTemplate(trimLines(new ArrayList<>(codes)));
    }


    /**
     * Join the given templates.
     * as the new line
     * @param codeTemplates the templates
     * @return joined template
     */
    public static CodeTemplate join(CodeTemplate... codeTemplates) {
        return new CodeTemplate(Arrays.stream(codeTemplates)
            .map(CodeTemplate::lines)
            .flatMap(Collection::stream)
            .collect(Collectors.toList()));
    }


    /**
     * Join the given templates with the given delimiter.
     * @param delimiter the delimiter
     * @param codeTemplates the templates
     * @return joined template
     */
    public static CodeTemplate join(String delimiter, CodeTemplate... codeTemplates) {
        List<String> list = new ArrayList<>();
        for (CodeTemplate codeTemplate : codeTemplates) {
            if (codeTemplate.lines().isEmpty()) {
                continue;
            }
            if (!list.isEmpty()) {
                // add delimiter at last line
                int index = list.size() - 1;
                list.set(index, list.get(index) + delimiter);
            }
            list.addAll(codeTemplate.lines());
        }
        return CodeTemplate.of(String.join("", list));
    }


    /**
     * Add the code at the head.
     * @param code the code
     * @return this template
     */
    public CodeTemplate addHead(String code) {
        lines.addAll(0, code.lines().toList());
        return this;
    }


    /**
     * Add the code at the tail.
     * @param str the code
     * @return this template
     */
    public CodeTemplate addTail(String str) {
        lines.addAll(lines.size(), str.lines().toList());
        return this;
    }


    /**
     * Add the code at the tail.
     * @param list the codes
     * @return this template
     */
    CodeTemplate add(List<String> list) {
        lines.addAll(trimLines(new ArrayList<>(list)));
        return this;
    }


    /**
     * Bind the value to the template by given key.
     * @param key the key name
     * @param value the value to be bind
     * @return this template
     */
    public CodeTemplate bind(String key, String value) {
        applySubstitution(key, value);
        return this;
    }


    /**
     * Bind the value to the template by given key.
     * @param key the key name
     * @param other the template to be bind
     * @return this template
     */
    public CodeTemplate bind(String key, CodeTemplate other) {
        applySubstitution(key, other.content());
        return this;
    }


    /**
     * Clear the key.
     * @param key the name of key
     * @return this template
     */
    public CodeTemplate clear(String key) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).trim().equals(key)) {
                lines.remove(i--);
            } else if (lines.get(i).contains(key)) {
                lines.set(i, lines.get(i).replace(key, ""));
            }
        }
        return this;
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
    public void addAsEnclosed(CodeTemplate codeBlock) {
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
     * Get the lines.
     * @return the lines
     */
    public List<String> lines() {
        return new ArrayList<>(lines);
    }


    /**
     * Trim the blank lines at head and tail.
     * @param lines the target lines
     * @return the trimmed lines
     */
    private static List<String> trimLines(List<String> lines) {

        if (lines.isEmpty()) {
            return lines;
        }

        while (true) {
            if (lines.isEmpty()) break;
            if (lines.get(0).isBlank()) lines.remove(0);
            else break;
        }

        while (true) {
            int tail = lines.size() - 1;
            if (tail <= 0) break;
            if (lines.get(tail).isBlank()) lines.remove(tail);
            else break;
        }

        return lines;

    }

}
