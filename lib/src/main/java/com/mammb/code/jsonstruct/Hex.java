package com.mammb.code.jsonstruct;

import java.util.Arrays;

public class Hex {

    final static int[] TBL = new int[128];
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

}
