---
title: User Guide
description: User Guide
layout: ../../layouts/MainLayout.astro
---

**Json Strust** のユーザガイドです。

リポジトリは以下を参照してください。

* [Json Strust](https://github.com/naotsugu/json-struct)



## Introduction


### json-struct とは

JSON と Java オブジェクトの相互変換ライブラリです。

数ある同様なライブラリは、リフレクションの多用や機能過多により、実行時のフットプリントが高い傾向があります。
単に、JSON変換という簡単な処理だけを行いたい場合、このフットプリントの高さは適切でないケースが多くあります。

Json Struct は、単純なJSON変換において、最適なフットプリントと速度を実現します。
そしてほとんどのケースでは単純なJSON変換しか必要ではありません。

アノテーションプロセッサによりビルド時に相互変換コードを生成するため、実行時には単純なオブジェクト生成/JSON文字列の出力となるため、優れたフットプリントと実行速度を両立します。



## Quickstart


### 依存の追加

依存の追加とアノテーションプロセッサの指定を行います。


* Gradle Kotlin DSL の場合

```kotlin
dependencies {
  implementation("com.mammb:json-struct:0.3.0")
  annotationProcessor("com.mammb:json-struct:0.3.0")
}
```

* Gradle Groovy DSL の場合

```groovy
dependencies {
    testImplementation 'com.mammb:json-struct:0.3.0'
    annotationProcessor 'com.mammb:json-struct:0.3.0'
}
```

* Maven の場合

```xml
<dependency>
    <groupId>com.mammb</groupId>
    <artifactId>json-struct</artifactId>
    <version>0.3.0</version>
</dependency>
```



### モデルの指定

マッピング対象のモデルに `@JsonStruct` アノテーションを付与します。

```java
@JsonStruct
public record Person(FullName fullName, int age) { }
```

`@JsonStruct` アノテーションによりモデルに応じたシリアライズ/デシリアライズ専用のクラスが生成されます。


オブジェクトの生成にはコンストラクタが利用され、JSON文字列の生成にはアクセッサーメソッドが利用されます。



### シリアライズ/デシリアライズ

以下のようなJSON文字列があった場合、

```java
var str = """
{
    "fullName": {
        "givenName": "Bob",
        "familyName": "Dylan"
    },
    "age": 81,
    "gender": "MALE"
}
""";
```

デシリアライズは以下のように行います。

```java
var json = Json.of(Person.class);
var person = json.fromJson(str);
```

シリアライズは以下のように行います。

```java
var serialized = json.toJson(person);
```

`Json.of()` によるJsonオブジェクトの生成は、生成コストが非常に小さいため、都度インスタンス化しても問題になることはありません。

以下のように書くこともできます。


```java
Person person = Json.objectify(string, Person.class);
String serialized = Json.stringify(person);
```



## @JsonStruct

`@JsonStruct` はJSON変換対象のモデルクラスを指定します。

`@JsonStruct` が付与されたクラスはアノテーションプロセッサによりJSON との相互変換コードが生成されます。

`@JsonStruct` は、クラス/レコードクラス/コンストラクタ/スタティックファクトリメソッド の何れかに付与することができます。


### レコードクラス

レコードクラスに`@JsonStruct` を付与するケースは、多くの場合で推奨されます。

```java
@JsonStruct
public record Person(FullName fullName, int age) { }
```

レコードクラスのコンポーネント名がJSONプロパティ名とマッピングされます。

対象のクラスはインナークラスでも機能します。



### クラス

クラスに `@JsonStruct` を付与した場合、そのクラスの中で引数の数が最も多いコンストラクタが選択され、インスタンス化に使用されます。

```java
@JsonStruct
public class Book {
    private final String name;
    public Book(String name) { this.name = name; }
    public String getName() { return name; }
}
```

コンストラクタの引数パラメータ名がJSONプロパティ名とマッピングされます。

シリアライズにはアクセッサ(getter)が利用されます。
ミューテータ(setter)は必要ありません。


### コンストラクタ

コンストラクタに `@JsonStruct` を付与した場合、そのコンストラクタがインスタンス化に使用されます。

これは、同数のパラメータを取るコンストラクタが複数存在する場合に、前述のクラスに付与するケースの代替となります。

```java
public class Book {
    private final String name;

    @JsonStruct
    public Book(String name) {
        this.name = name;
    }

    public String getName() { return name; }
}
```


### スタティックファクトリメソッドへの付与

スタティックファクトリメソッドに `@JsonStruct` を付与した場合、そのスタティックファクトリメソッドがインスタンス化に使用されます。


```java
public class Pet {
    private final String name;
    private Pet(String name) { this.name = name; }

    @JsonStruct
    public static Pet of(String name) {
        return new Pet(name);
    }

    public String getName() { return name; }
}
```

public, static であり、自身のクラスオブジェクトが戻り値となっている必要があります。



## @JsonStructIgnore

`@JsonStructIgnore` は、オブジェクトのシリアライズ/デシリアライズで、無視したい項目に付与します。

レコードクラスのレコードコンポーネントに付与することで、シリアライズ/デシリアライズの対象から除外されます。

デシリアライズ時には、デフォルト値が適用されます。
デフォルト値には、プリミティブ型の場合は言語のデフォルト値が適用されます。
オブジェクトの場合は null となりますが、Integer などのラッパクラスの場合はプリミティブ型のデフォルト値が適用されます。

```java
@JsonStruct
public record Person(FullName fullName, @JsonStructIgnore int age) { }
```


クラスの場合には、アクセッサに付与することで、シリアライズから除外されます。
コンストラクタ引数に付与すれば、デシリアライズ時にデフォルト値が適用されます。

```java
@JsonStruct
public class Book {
    private final String name;
    // ...
    public Book(String name, // ...) {
        this.name = name;
        // ...
    }

    @JsonStructIgnore
    public String getName() { return name; }
}
```



## @JsonStructConvert

`@JsonStructConvert` はデフォルトの変換処理をカスタマイズしたい場合、追加の変換処理を定義したい場合に以下のように利用します。


```java
public static final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

@JsonStructConvert
public static final Function<LocalDateTime, CharSequence> localDateTimeStringify = value ->
    value.format(dtFormatter);

@JsonStructConvert
public static final Function<String, LocalDateTime> localDateTimeObjectify = str ->
    LocalDateTime.parse(str, dtFormatter);
```

この例では、`LocalDateTime` の組み込みの変換をカスタマイズして上書いています。

変換処理の修飾子と戻り値のシグネチャは上記例の形式に従う必要があります。



## 組み込み変換の対象

以下のクラスには、組み込みの変換処理が提供されています。

* `byte`, `java.lang.Byte`
* `boolean`, `java.lang.Boolean`
* `double`, `java.lang.Double`
* `float`, `java.lang.Float`
* `int`, `java.lang.Integer`
* `long`, `java.lang.Long`
* `short`, `java.lang.Short`
* `java.math.BigDecimal`
* `java.lang.Number`
* `java.math.BigInteger`
* `java.util.OptionalDouble`
* `java.util.OptionalInt`
* `java.util.OptionalLong`

* `java.lang.String`
* `char`, `java.lang.Character`
* `java.util.Date`
* `java.util.Calendar`
* `java.util.TimeZone`
* `java.time.Instant`
* `java.time.LocalDateTime`
* `java.time.LocalDate`
* `java.time.LocalTime`
* `java.time.OffsetDateTime`
* `java.time.OffsetTime`
* `java.time.ZonedDateTime`
* `java.time.ZoneId`
* `java.time.ZoneOffset`
* `java.time.Duration`
* `java.nio.file.Path`
* `java.time.Period`
* `java.net.URI`
* `java.net.URL`
* `java.util.UUID`


※`java.sql.Date`, `java.sql.Timestamp` には組み込みの変換処理を提供しません


## JsonPrettyWriter

整形されたJsonを取得する場合は、`JsonPrettyWriter` を使い以下のようにします。

```java
var bob = new Person(new FullName("Bob", "Dylan"), 81, Gender.FEMALE);
var writer = new StringWriter();
Json.stringify(bob, JsonPrettyWriter.of(writer));
```

以下の出力が得られます。

```java
assertEquals("""
    {
      "fullName": {
        "givenName": "Bob",
        "familyName": "Dylan"
      },
      "age": 81,
      "gender": "FEMALE"
    }""", writer.toString());
```

直接Json文字列を整形することもできます。

```java
var ret = JsonPrettyWriter.toPrettyString("""
    {"fullName":{"givenName":"Bob","familyName":"Dylan"},"age": 81,"gender": "FEMALE"}""");
```

