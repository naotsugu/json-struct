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
        var book = json.from("""
            {
                "name": "bookName"
            }
            """);
        assertEquals("bookName", book.getName());
    }

    @Test
    void testFood() {
        var json = Json.of(Food.class);
        var food = json.from("""
            {
                "name": "foodName",
                "materials": ["material0", "material1"]
            }
            """);
        assertEquals("foodName", food.name());
        assertEquals("material0", food.materials().get(0));
        assertEquals("material1", food.materials().get(1));
    }

}
