
# json-struct

[![Build](https://github.com/naotsugu/json-struct/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/naotsugu/jpa-fluent-query/actions/workflows/gradle-build.yml)


See [User Guide](https://naotsugu.github.io/json-struct/)


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
  implementation("com.mammb:json-struct:0.2.0")
  annotationProcessor("com.mammb:json-struct:0.2.0")
}
```


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

Similarly, the stringify would be as follows

```java
json.stringify(person);
// {"fullName":{"givenName":"Bob","familyName":"Dylan"},"age":81,"gender":"MALE"}
```


## Benchmark

Gson vs Jackson vs json-struct.


```bash
$ ./gradlew benchmark:jmh
```


##### Serialize and Deserialize

```
Benchmark            Mode  Cnt     Score     Error  Units
MicroBench2.gson     avgt    3   4219.603 ±  348.076  ns/op
MicroBench2.jackson  avgt    3   2693.410 ± 1076.516  ns/op
MicroBench2.struct   avgt    3   2556.213 ±   66.621  ns/op
```


##### Instantiation and Serialize, Deserialize 

```
Benchmark            Mode  Cnt      Score      Error  Units
MicroBench.gson      avgt    3  13860.379 ± 3700.307  ns/op
MicroBench.jackson   avgt    3  45937.932 ± 3149.854  ns/op
MicroBench.struct    avgt    3   2600.124 ±   50.123  ns/op
```

