package com.mammb.code.jsonstruct.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mammb.code.jsonstruct.Json;
import com.mammb.code.jsonstruct.benchmark.data.Glossary;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Warmup;

@Fork(1)
@Warmup(iterations = 2, time = 10)
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

    @Benchmark
    public String struct() {
        return json.stringify(glossary);
    }

    @Benchmark
    public String gson() {
        return gson.toJson(glossary);
    }

    @Benchmark
    public String jackson() throws JsonProcessingException {
        return jackson.writeValueAsString(glossary);
    }

    public static void main(String[] args) throws Exception {
        var mb = new MicroBenchSerialize();

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
