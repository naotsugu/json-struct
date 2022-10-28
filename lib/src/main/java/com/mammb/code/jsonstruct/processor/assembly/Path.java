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
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

/**
 * Path.
 * @author Naotsugu Kobayashi
 */
public class Path {

    /** The list of path. */
    private final List<String> paths;


    /**
     * Constructor.
     * @param paths The list of path
     */
    private Path(List<String> paths) {
        this.paths = paths;
    }


    /**
     * Create a new empty path.
     * @return a new empty path
     */
    public static Path of() {
        return new Path(new ArrayList<>());
    }


    /**
     * Create a new path with given paths.
     * @param paths The list of path
     * @return a new path
     */
    public static Path of(String... paths) {
        return new Path(new ArrayList<>(List.of(paths)));
    }


    public Path with(String name) {
        Path ret = Path.of();
        ret.paths.addAll(this.paths);
        ret.paths.add(name);
        return ret;
    }


    /**
     * Gets whether this list contains no elements.
     * @return {@code true} if this list contains no elements.
     */
    public boolean isEmpty() {
        return paths.isEmpty();
    }


    /**
     * Add a path.
     * @param path path
     */
    public void add(String path) {
        this.paths.add(path);
    }


    /**
     * Clear path elements.
     */
    public void clear() {
        paths.clear();
    }


    /**
     * Join the paths as elvis operation.
     * @return the joined string
     */
    public String elvisJoin() {
        StringBuilder sb = new StringBuilder("Optional.ofNullable(");
        for (Iterate.Entry<String> entry : Iterate.of(paths)) {
            String path = entry.value();
            if (Objects.isNull(path) || path.isBlank()) {
                continue;
            }
            if (!entry.isFirst() && !path.endsWith(")")) {
                path = path + "()";
            }
            sb.append(path);
            if (entry.hasNext()) {
                sb.append(").map(e -> e.");
            }
        }
        sb.append(")");
        return sb.toString();
    }


    /**
     * Join the paths as pointer.
     * @return the joined string
     */
    public String pointerJoin() {
        return "/" + paths.stream()
            .filter(Objects::nonNull).filter(not(String::isBlank))
            .collect(Collectors.joining("/"));
    }


    /**
     * Join the paths as camel case.
     * @return the joined string
     */
    public String camelJoin() {

        String ret = paths.stream()
            .filter(Objects::nonNull).filter(not(String::isBlank))
            .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
            .collect(Collectors.joining());

        return Character.toLowerCase(ret.charAt(0)) + ret.substring(1);
    }

}
