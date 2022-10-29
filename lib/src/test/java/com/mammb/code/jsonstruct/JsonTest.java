package com.mammb.code.jsonstruct;

import com.mammb.code.jsonstruct.processor.testdata.Book;
import com.mammb.code.jsonstruct.processor.testdata.Food;
import com.mammb.code.jsonstruct.processor.testdata.Gender;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonTest {

    @Test
    void testBook() {

        var json = Json.of(Book.class);
        var jsonStr = """
            {"name":"bookName"}
            """;

        var book = json.from(jsonStr);
        assertEquals("bookName", book.getName());

        assertEquals(jsonStr.trim(), json.stringify(book));
    }


    @Test
    void testFood() {

        var json = Json.of(Food.class);
        var jsonStr = """
            {"name":"foodName","materials":["material0","material1"]}
            """;

        var food = json.from(jsonStr);
        assertEquals("foodName", food.name());
        assertEquals("material0", food.materials().get(0));
        assertEquals("material1", food.materials().get(1));

        assertEquals(jsonStr.trim(), json.stringify(food));
    }

}
