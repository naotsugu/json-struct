package com.mammb.code.jsonstruct.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mammb.code.jsonstruct.Json;
import com.mammb.code.jsonstruct.benchmark.data.Glossary;
import com.mammb.code.jsonstruct.parser.JsonStructure;
import com.mammb.code.jsonstruct.parser.Parser;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@Fork(1)
@Warmup(iterations = 2, time = 3)
@Measurement(iterations = 2, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MicroBenchDeserialize {

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

    static Json<Glossary> json = Json.of(Glossary.class);
    static Gson gson = new Gson();
    static ObjectMapper jackson = new ObjectMapper();

    //@Benchmark
    public Glossary struct() {
        JsonStructure json = Parser.of(str).parse(); //  1719.155 ns/op
        return null;
        //return json.from(str);
    }

    //@Benchmark
    public Glossary gson() { // 1699.341 ns/op
        return gson.fromJson(str, Glossary.class);
    }

    //@Benchmark
    public Glossary jackson() throws JsonProcessingException { // 1658.562 ns/op
        return jackson.readValue(str, Glossary.class);
    }


    public static void main(String[] args) throws Exception {

        var mb = new MicroBenchDeserialize();

        System.out.println("-- struct -------------------------------------------");
        var struct = gson.toJson(mb.struct());
        System.out.println(struct);

        System.out.println("-- gson ---------------------------------------------");
        var gson_ = gson.toJson(mb.gson());
        System.out.println(gson_);

        System.out.println("-- jackson ------------------------------------------");
        var jackson = gson.toJson(mb.jackson());
        System.out.println(jackson);

        System.out.println("match: " + gson_.equals(struct));
        System.out.println("match: " + jackson.equals(struct));

    }

}
