
# json-struct

[![Build](https://github.com/naotsugu/json-struct/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/naotsugu/jpa-fluent-query/actions/workflows/gradle-build.yml)


[User Guide](https://naotsugu.github.io/json-struct/)


## What is this

json-struct provides a standard binding layer between Java classes and JSON documents.

* No reflection is used
* Annotation processor resolves bindings at build time
* Small and fast
* No Dependency Libraries
* Constructor binding (for immutable)
* Java 17+



## Usage

Add dependencies.

```kotlin
dependencies {
  implementation("com.mammb:json-struct:0.3.0")
  annotationProcessor("com.mammb:json-struct:0.3.0")
}
```


Annotate the model with `@JsonStruct`.

```java
@JsonStruct
public record Person(FullName fullName, int age) {
}
```

Serialization / Deserialization would be as follows

```java
String string = """
{
    "fullName": {
         "givenName": "Bob",
         "familyName": "Dylan"
    },
    "age": 81,
    "gender": "MALE"
}""";


Person person = Json.objectify(string, Person.class);

person.fullName().givenName();  // Bob
person.fullName().familyName(); // Dylan
person.age();                   // 81
person.gender();                // Gender.MALE

    
String serialized = Json.stringify(person);
// {"fullName":{"givenName":"Bob","familyName":"Dylan"},"age":81,"gender":"MALE"}
```

The following is equivalent.

```java
Json json = Json.of(Person.class);
Person person = json.fromJson(string);
String serialized = json.toJson(person);
```


## Benchmark

Gson vs Jackson vs json-struct.


```bash
$ ./gradlew benchmark:jmh
```


##### Instantiation and Serialize, Deserialize

```
Benchmark            Mode  Cnt      Score      Error  Units
MicroBench.gson      avgt    3  13227.451 ±  463.189  ns/op
MicroBench.jackson   avgt    3  46689.023 ± 1464.963  ns/op
MicroBench.struct    avgt    3   2591.424 ±   11.675  ns/op
```

##### Serialize and Deserialize

```
Benchmark            Mode  Cnt      Score      Error  Units
MicroBench2.gson     avgt    3   4139.815 ±  101.674  ns/op
MicroBench2.jackson  avgt    3   2578.517 ±   55.212  ns/op
MicroBench2.struct   avgt    3   2483.299 ±   30.564  ns/op
```
