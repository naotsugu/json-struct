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
package com.mammb.code.jsonstruct.processor.assemble;

import com.mammb.code.jsonstruct.lang.Iterate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

/**
 * AccessPath.
 * @author Naotsugu Kobayashi
 */
public class AccessPath {

    private final List<String> paths;


    public AccessPath(List<String> paths) {
        this.paths = paths;
    }


    public static AccessPath of() {
        return new AccessPath(new ArrayList<>());
    }


    public static AccessPath of(String... paths) {
        return new AccessPath(new ArrayList<>(List.of(paths)));
    }


    public AccessPath with(String name) {
        AccessPath ret = AccessPath.of();
        ret.paths.addAll(this.paths);
        ret.paths.add(name);
        return ret;
    }


    public void add(String name) {
        this.paths.add(name);
    }


    public void clear() {
        paths.clear();
    }


    public String elvis() {
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


    public String join(String delimiter) {
        return paths.stream()
            .filter(Objects::nonNull).filter(not(String::isBlank))
            .collect(Collectors.joining(delimiter));
    }


    public String camelJoin(String delimiter) {

        String ret = paths.stream()
            .filter(Objects::nonNull).filter(not(String::isBlank))
            .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
            .collect(Collectors.joining(delimiter));

        return Character.toLowerCase(ret.charAt(0)) + ret.substring(1);
    }

}
