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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * SourceBuilder.
 * @author Naotsugu Kobayashi
 */
public class SourceBuilder {

    /** The line feed char. */
    private static final String LF = "\n";

    /** The name of package. */
    private final String packageName;

    /** The name of class. */
    private final String className;

    /** The fqcn of import. */
    private final Set<String> imports;

    /** The code template. */
    private CodeTemplate code;


    /**
     * Constructor.
     * @param sourceFqcn
     */
    private SourceBuilder(String sourceFqcn) {
        if (countDot(sourceFqcn) > 0) {
            this.packageName = sourceFqcn.substring(0, sourceFqcn.lastIndexOf('.'));
            this.className = sourceFqcn.substring(sourceFqcn.lastIndexOf('.') + 1);
        } else {
            this.packageName = "";
            this.className = sourceFqcn;
        }
        this.imports = new HashSet<>();
        this.code = CodeTemplate.of();
    }


    public static SourceBuilder of(String sourceFqcn, String code) {
        return new SourceBuilder(sourceFqcn).add(code);
    }


    private SourceBuilder add(String str) {

        if (str == null || str.isEmpty()) {
            return this;
        }

        List<String> lines = new ArrayList<>();

        str.lines().forEach(line -> {

            var trimmed = line.trim();

            if (trimmed.startsWith("package ") && trimmed.endsWith(";")) {
                // if contains the package statement, check the name
                var name = trimmed.substring("package ".length(), trimmed.length() - 1);
                if (!Objects.equals(packageName, name)) {
                    throw new IllegalArgumentException(name);
                }

            } else if (trimmed.startsWith("import ") && trimmed.endsWith(";")) {
                // if contains the import statement, add to the imports
                var name = trimmed.substring("import ".length(), trimmed.length() - 1);
                imports.add(name);

            } else {
                // add as a code line
                if (!trimmed.isBlank() || !lines.isEmpty()) {
                    lines.add(line);
                }
            }
        });

        code.add(lines);
        return this;
    }


    /**
     * Apply import.
     * @param fqcn the type fqcn
     * @return the type name applied import
     */
    public String applyImport(String fqcn) {
        if (fqcn.isBlank() || fqcn.contains(" ") || fqcn.contains(LF)) {
            throw new IllegalArgumentException(fqcn);
        }
        if (countDot(fqcn) == 0) {
            return fqcn;
        }
        imports.add(fqcn);
        return simpleName(fqcn);
    }


    /**
     * Remove the default import, and package local import.
     * @return the import strings
     */
    private List<String> normalizeImports() {
        return imports.stream()
            .filter(s -> !s.startsWith("java.lang."))
            .filter(s -> !isInPackage(s))
            .filter(s -> !s.isBlank())
            .sorted()
            .toList();
    }


    /**
     * Gets whether the given fqcn belongs to a package.
     * @param fqcn the fqcn
     * @return {@code true} if the given name belongs to a package
     */
    private boolean isInPackage(String fqcn) {
        if (fqcn.startsWith(packageName) &&
            !fqcn.endsWith(".*") &&
            fqcn.length() > packageName.length()) {
            var typeName = fqcn.replace(packageName + '.', "");
            return typeName.length() > 1 && countDot(typeName) == 0;
        }
        return false;
    }


    /**
     * Get the simple name from the given fqcn.
     * @param fqcn the fqcn
     * @return the simple name
     */
    private static String simpleName(String fqcn) {
        return fqcn.substring(fqcn.lastIndexOf('.') + 1);
    }


    /**
     * Gets how many dots are contained in the given string.
     * @param str the string
     * @return the number of dots included
     */
    private static int countDot(String str) {
        return (int) str.chars().filter(c -> c == '.').count();
    }

}
