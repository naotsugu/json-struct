# json-struct

[![Build](https://github.com/naotsugu/json-struct/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/naotsugu/jpa-fluent-query/actions/workflows/gradle-build.yml)


## What is this

json-struct provides a standard binding layer between Java classes and JSON documents.

Annotation processor resolves bindings at build time.

No reflection is used, resulting in fast operation.


## Usage

Annotate the model with `@JsonStruct`.

```java
@JsonStruct
public record Person(FullName fullName, int age) {
}
```

```java
var json = Json.of(Person.class);
var person = json.from("""
    {
        "fullName": {
             "givenName": "Bob",
             "familyName": "Dylan"
        },
        "age": 81,
        "gender": "MALE"
    }
    """;

person.fullName().givenName();  // Bob
person.fullName().familyName(); // Dylan
person.age();                   // 81
person.gender();                // Gender.MALE
```


The code generated by the annotation processor looks like this

```java
@Override
public Person from(Reader reader) {
    var json = Parser.of(reader).parse();
    return new Person(
        new FullName(
            ((JsonStructure) json).as("/fullName/givenName", convert.to(String.class)),
            ((JsonStructure) json).as("/fullName/familyName", convert.to(String.class))),
        ((JsonStructure) json).as("/age", convert.to(int.class)),
        Gender.valueOf(((JsonStructure) json).as("/gender", convert.to(String.class)))
    );
}
```

Similarly, the stringify would be as follows

```java
var person = json.from(jsonStr);

json.stringify(person);
// {"fullName":{"givenName":"Bob","familyName":"Dylan"},"age":81,"gender":"MALE"}
```


## Benchmarking

```bash
$ ./gradlew benchmark:jmh
```


```
Benchmark            Mode  Cnt      Score      Error  Units
MicroBench.gson     thrpt    5  72342.839 ± 2642.378  ops/s
MicroBench.jackson  thrpt    5  20952.252 ± 1746.561  ops/s
MicroBench.struct   thrpt    5  93881.308 ±  314.969  ops/s
```
