package com.mammb.code.jsonstruct.parser;

import java.math.BigDecimal;

public interface NumberSource extends CharSource {
    int getInt();
    long getLong();
    BigDecimal getBigDecimal();
}
