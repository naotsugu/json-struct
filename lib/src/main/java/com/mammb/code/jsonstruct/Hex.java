package com.mammb.code.jsonstruct;

import java.util.Arrays;

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
