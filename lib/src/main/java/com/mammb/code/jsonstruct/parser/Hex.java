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
package com.mammb.code.jsonstruct.parser;

import java.util.Arrays;

/**
 * Hex utility.
 *
 * @author Naotsugu Kobayashi
 */
public class Hex {

    private static final int[] TBL = new int[128];
    static {
        Arrays.fill(TBL, -1);
        for (int i = '0'; i <= '9'; i++) {
            TBL[i] = i - '0';
        }
        for (int i = 'A'; i <= 'F'; i++) {
            TBL[i] = 10 + i - 'A';
        }
        for (int i = 'a'; i <= 'f'; i++) {
            TBL[i] = 10 + i - 'a';
        }
    }

    public static int digit(int ch) {
        return (ch >= 0 && ch < Hex.TBL.length) ? Hex.TBL[ch] : -1;
    }

    public static int deHex(char c) {
        if (c <= '9' && c >= '0') {
            return c - '0';
        }
        if (c <= 'F' && c >= 'A') {
            return c - ('A' - 10);
        }
        if (c <= 'f' && c >= 'a') {
            return c - ('a' - 10);
        }
        return -1;
    }

}
