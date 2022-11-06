package com.mammb.code.jsonstruct.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mammb.code.jsonstruct.Json;
import com.mammb.code.jsonstruct.benchmark.data.Glossary;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import java.util.concurrent.TimeUnit;

@Fork(1)
@Warmup(iterations = 2, time = 3)
@Measurement(iterations = 2, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MicroBenchSerialize {

    private static final String str = """
            {
              "title": "example glossary",
              "div": {
                "title": "S",
                "list":[
                  {
                    "id": "SGML",
                    "sortAs": "SGML",
                    "glossTerm": "Standard Generalized Markup Language",
                    "acronym": "SGML",
                    "abbrev": "ISO 8879:1986",
                    "def": {
                      "para": "A meta-markup language, used to create markup languages such as DocBook.",
                      "seeAlso": ["GML", "XML"]
                    }
                  }
                ]
              }
            }""";

    private static final Json<Glossary> json = Json.of(Glossary.class);
    private static final Gson gson = new Gson();
    private static final ObjectMapper jackson = new ObjectMapper();
    private static final Glossary glossary = json.from(str);

    // @Benchmark
    public String struct() {
        return json.stringify(glossary);
    }

    // @Benchmark
    public String gson() {
        return gson.toJson(glossary);
    }

    // @Benchmark
    public String jackson() throws JsonProcessingException {
        return jackson.writeValueAsString(glossary);
    }

}
