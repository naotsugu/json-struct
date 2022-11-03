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
public class MicroBench {

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


    //@Benchmark
    public String struct() {
        Json<Glossary> json = Json.of(Glossary.class);
        Glossary glossary = json.from(str);
        return json.stringify(glossary);
    }

    //@Benchmark
    public String gson() {
        Gson gson = new Gson();
        Glossary glossary = gson.fromJson(str, Glossary.class);
        return gson.toJson(glossary);
    }

    //@Benchmark
    public String jackson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Glossary glossary = mapper.readValue(str, Glossary.class);
        return mapper.writeValueAsString(glossary);
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
