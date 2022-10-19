# json-struct

[![Build](https://github.com/naotsugu/json-struct/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/naotsugu/jpa-fluent-query/actions/workflows/gradle-build.yml)

Under development.

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
        "fullName": { "givenName": "Bob", "familyName": "Dylan"}
        "age": 81
    }
    """);
assertEquals("Bob", person.fullName().givenName());
```


