package com.mammb.code.jsonstruct;

import java.math.BigDecimal;

public interface JsonNumber extends JsonValue, NumberSource {

    static JsonNumber of(NumberSource ns) {
        return new JsonNumberImpl(ns);
    }

    class JsonNumberImpl implements JsonNumber {
        private final NumberSource source;
        public JsonNumberImpl(NumberSource ns) {
            this.source = ns;
        }

        @Override
        public int getInt() {
            return source.getInt();
        }

        @Override
        public long getLong() {
            return source.getLong();
        }

        @Override
        public BigDecimal getBigDecimal() {
            return source.getBigDecimal();
        }

        @Override
        public String toString() {
            return source.toString();
        }

        @Override
        public char[] chars() {
            return source.chars();
        }
    }

}
