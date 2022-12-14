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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static java.util.function.Predicate.*;

/**
 * Imports.
 * @author Naotsugu Kobayashi
 */
public class Imports {

    /** The fqcn of import. */
    private final Set<String> set;

    /** The implicit imports. */
    private final Set<String> implicits;


    /**
     * Constructor.
     */
    private Imports(Set<String> set) {
        this.set = set;
        this.implicits = new HashSet<>();
        this.implicits.add("java.lang.");
    }


    /**
     * Create the empty imports.
     * @return the empty imports
     */
    public static Imports of() {
        return new Imports(new HashSet<>());
    }


    /**
     * Create the empty imports.
     * @param literal the literal of imports
     * @return the imports
     */
    public static Imports of(String literal) {
        return new Imports(literal.lines()
            .filter(not(String::isBlank))
            .map(Imports::strip)
            .collect(Collectors.toSet()));
    }


    /**
     * Marge other imports.
     * @param that other imports
     */
    public void marge(Imports that) {
        set.addAll(that.set);
    }


    /**
     * Add implicit import.
     * maybe package name
     * @param implicit the implicit
     */
    public void addImplicit(String implicit) {
        implicits.add(implicit.endsWith(".") ? implicit : implicit + ".");
    }


    /**
     * Apply import.
     * @param fqcn the type fqcn
     * @return the type name applied import
     */
    public String apply(String fqcn) {
        return applyImport(strip(fqcn));
    }


    /**
     * Add import.
     * @param imports the import subjects
     */
    public void add(String imports) {
        imports.lines().filter(not(String::isBlank)).map(Imports::strip).forEach(this::applyImport);
    }


    /**
     * Apply import with generics.
     * <p>
     * {@code java.util.Map<java.util.List<java.lang.String>,java.util.List<java.lang.Integer>>}
     *
     * Import types are
     * {@code java.util.Map}
     * {@code java.util.List}
     * {@code java.util.String}
     * {@code java.lang.Integer}
     *
     * Return string are
     * {@code Map<List<String>,List<Integer>>}
     *
     * @param fqcn the type fqcn
     * @return the type name applied import
     */
    private String applyImport(String fqcn) {
        final String withDelimiterRe = "((?<=%1$s)|(?=%1$s))".formatted("[<>,]");
        return Arrays.stream(fqcn.split(withDelimiterRe))
            .map(String::trim)
            .filter(not(String::isBlank))
            .map(s -> s.matches("[<>,]") ? s : applySingle(s))
            .collect(Collectors.joining());
    }


    /**
     * Apply import.
     * @param fqcn the type fqcn
     * @return the type name applied import
     */
    private String applySingle(String fqcn) {

        if (fqcn.isBlank() || fqcn.contains(" ") ||
            fqcn.contains(";") || fqcn.contains("\n") ||
            fqcn.contains("<") || fqcn.contains(">") || fqcn.contains(",")) {
            throw new IllegalArgumentException(fqcn);
        }
        if (countDot(fqcn) == 0) {
            return fqcn;
        }

        set.add(fqcn);
        return simpleName(fqcn);
    }


    @Override
    public String toString() {
        return normalize().stream()
            .map(s -> "import " + s + ";")
            .collect(Collectors.joining("\n"));
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
     * Strip import statement.
     * remove {@code "import "} and {@code ";"}
     * @param string the import statement
     * @return the simple name
     */
    private static String strip(String string) {

        var ret = string.trim();

        if (ret.startsWith("import ")) {
            ret = ret.substring("import ".length());
        }
        if (ret.endsWith(";")) {
            ret = ret.substring(0, ret.length() - 1);
        }
        return ret;
    }


    /**
     * Gets how many dots are contained in the given string.
     * @param str the string
     * @return the number of dots included
     */
    private static int countDot(String str) {
        return (int) str.chars().filter(c -> c == '.').count();
    }


    /**
     * Remove the default import.
     * @return the import strings
     */
    private List<String> normalize() {

        Predicate<String> notContainImplicits = str -> implicits.stream().noneMatch(str::startsWith);

        return set.stream()
            .filter(notContainImplicits)
            .filter(not(String::isBlank))
            .sorted().toList();
    }

}
