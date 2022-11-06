package com.mammb.code.jsonstruct.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mammb.code.jsonstruct.Json;
import com.mammb.code.jsonstruct.benchmark.data.Glossary;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import java.util.concurrent.TimeUnit;

@Fork(1)
@Warmup(iterations = 5, time = 5)
@Measurement(iterations = 3, time = 5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MicroBench2 {

    private static final String str = """
            {
              "title": "example glossary",
              "div": {
                "title": "S",
                "list":[
                  {
                    "id": "SGML",
                    "sortAs": "SGML",
                    "glossTerm": "Standard Markup Language",
                    "acronym": "SGML",
                    "abbrev": "ISO 8879:1986",
                    "def": {
                      "para": "A meta-markup language.",
                      "seeAlso": ["GML", "XML"]
                    }
                  }
                ]
              }
            }""";
    private static final Json<Glossary> json = Json.of(Glossary.class);
    private static final Gson gson = new Gson();
    private static final ObjectMapper jackson = new ObjectMapper();

    @Benchmark
    public String struct() {
        Glossary glossary = json.from(str);
        return json.stringify(glossary);
    }

    @Benchmark
    public String gson() {
        Glossary glossary = gson.fromJson(str, Glossary.class);
        return gson.toJson(glossary);
    }

    @Benchmark
    public String jackson() throws JsonProcessingException {
        Glossary glossary = jackson.readValue(str, Glossary.class);
        return jackson.writeValueAsString(glossary);
    }


    public static void main(String[] args) throws Exception {

        var mb = new MicroBench2();

        System.out.println("-- struct -------------------------------------------");
        var struct = mb.struct();
        System.out.println(struct);

        System.out.println("-- gson ---------------------------------------------");
        var gson = mb.gson();
        System.out.println(gson);

        System.out.println("-- jackson ------------------------------------------");
        var jackson = mb.jackson();
        System.out.println(jackson);

        System.out.println("match: " + gson.equals(struct));
        System.out.println("match: " + jackson.equals(struct));

    }

}
