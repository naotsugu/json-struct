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
package com.mammb.code.jsonstruct.processor;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The utility of the code template.
 * @author Naotsugu Kobayashi
 */
public class CodeTemplate {

    private String packageName;
    private Set<String> imports;
    private List<String> codes;

    public CodeTemplate(String packageName, Set<String> imports, List<String> codes) {
        this.packageName = Objects.requireNonNull(packageName);
        this.imports = Objects.requireNonNull(imports);
        this.codes = Objects.requireNonNull(codes);
    }

    public static CodeTemplate of(String packageName, String code) {
        var tmpl = new CodeTemplate(packageName, new HashSet<>(), new ArrayList<>());
        code.lines().forEach(line -> {
            var trimmed = line.trim();
            if (trimmed.startsWith("package ") && trimmed.endsWith(";")) {
                if (tmpl.packageName.isEmpty()) {
                    tmpl.packageName = trimmed.substring(8, trimmed.length() - 1);
                }
            } else if (trimmed.startsWith("import ") && trimmed.endsWith(";")) {
                tmpl.imports.add(trimmed.substring(7, trimmed.length() - 1));
            } else {
                tmpl.codes.add(line);
            }
        });
        return tmpl;
    }

    public CodeTemplate bind(String key, String value) {
        for (int i = 0; i < codes.size(); i++) {
            if (codes.get(i).trim().equals(key) && value.contains("\n")) {
                var indent = codes.get(i).indexOf(key);
                var lines = value.split("\n");
                codes.set(i, codes.get(i).replace(key, lines[0]));
                for (int j = 1; j < lines.length; j++) {
                    codes.add(i + j, " ".repeat(indent) + lines[j]);
                }
            } else if (codes.get(i).contains(key)) {
                codes.set(i, codes.get(i).replace(key, value));
            }
        }
        return this;
    }

    public void writeTo(PrintWriter pw) {
        pw.println("package " + packageName + ";");
        pw.println();
        normalizeImports().forEach(s -> pw.println("import " + s + ";"));
        pw.println();
        codes.forEach(pw::println);
        pw.flush();
    }

    private List<String> normalizeImports() {
        return imports.stream()
            .filter(s -> !s.startsWith("java.lang."))
            .filter(s -> !s.equals(packageName))
            .sorted()
            .toList();
    }

}
