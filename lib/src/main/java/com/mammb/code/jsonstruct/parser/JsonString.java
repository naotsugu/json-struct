package com.mammb.code.jsonstruct.parser;

public interface JsonString extends JsonValue, CharSource {

    static JsonString of(CharSource cs) {
        return new JsonStringImpl(cs);
    }

    class JsonStringImpl implements JsonString {
        private final CharSource source;
        public JsonStringImpl(CharSource cs) {
            source = cs;
        }
        @Override
        public char[] chars() {
            return source.chars();
        }
        @Override
        public String toString() {
            return source.toString();
        }
    }

}
