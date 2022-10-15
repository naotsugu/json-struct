package com.mammb.code.jsonstruct;

import com.mammb.code.jsonstruct.processor.testdata.Book;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonTest {

    @Test
    void test() {
        var json = Json.of(Book.class);
        var book = json.from("""
            {
                "name": "bookName"
            }
            """);
        assertEquals("bookName", book.getName());
    }

}
