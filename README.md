# Furiganable

Furiganable is a simple and flexible library for adding furigana (phonetic readings) to text components. It’s designed to be easy to use.

This library is heavily inspired by the [android-compose-furigana](https://github.com/mainrs/android-compose-furigana/) library.

# Features

- Compatible with Compose Multiplatform (Android, iOS)
- Compatible with both Material 2 and Material 3 libraries
- Easily add furigana by using a simple predefined format, such as `[漢字[かんじ]]`.
- Supports Localization

# Variations

This library consists of three modules: `compose-core`, `compose-m2`, and `compose-m3`. Including just one of them is sufficient. Both `compose-m2` and `compose-m3` already include `compose-core`.

Module | Recommended | Description | Use Case
---|---|---|---
`compose-core` | - | A minimal core module with no dependency on Material libraries. Requires passing additional parameters to components. | Ideal for projects that avoid using Material libraries.
`compose-m2` | ✅ | Designed for projects using the Material 2 library. | Use if your project relies on Material 2.
`compose-m3` | ✅ | Designed for projects using the Material 3 library. | Use if your project relies on Material 3.


# Images

<details>
    <summary>Material 2</summary>

 Android (> 28) | Android (<= 28) | iOS 18 
 --- | --- | ---
 <img src="https://github.com/user-attachments/assets/99fd2c08-76a4-4af0-b9be-337471b705ca" width="320px"> | <img src="https://github.com/user-attachments/assets/ad6ffb01-117d-48bf-9475-579e741e0876" width="320px"> | <img src="https://github.com/user-attachments/assets/33511821-f125-4ea1-afda-fb2b3b55815f" width="320px">
    
</details>


<details open>
    <summary>Material 3</summary>

 Android (> 28) | Android (<= 28) | iOS 18 
 --- | --- | ---
 <img src="https://github.com/user-attachments/assets/e4a50bf5-1ac2-4f46-a768-68ed1ec21dae" width="320px"> | <img src="https://github.com/user-attachments/assets/db34f77c-d6e7-4544-aeff-6755fe7cb22d" width="320px"> | <img src="https://github.com/user-attachments/assets/99c41d29-c300-4a48-8375-bdcd6f183856" width="320px">

</details>

# API Reference

[API Reference🐢](https://turtlekazu.github.io/Furiganable/)

## Limitations

To force `isFallbackLineSpacing = false` for furigana text, the library swaps Jetpack Compose’s Text composable for an AndroidTextView in certain paths. As a consequence, some Text-related parameters are currently ignored.

Unsupported at the moment
- TextStyle: `textMotion`
- Text composable args: `onTextLayout`

If you depend on these APIs, the library may not yet meet your requirements. PRs welcome.

# Usage

## Add dependency on this library

<img alt="version badge" src="https://img.shields.io/github/v/release/turtlekazu/Furiganable?filter=*.*.*">

### Version Catalog

If you're using Version Catalog, add the following to your `libs.versions.toml` file:

```toml
[versions]
#...
furiganable = "$version"

[libraries]
#...
furiganable = { module = "com.turtlekazu.furiganable:compose-m3", version.ref = "furiganable" }
```

or

```toml
[libraries]
#...
furiganable = { module = "com.turtlekazu.furiganable:compose-m3", version = "$version" }
```

then

```kotlin
repositories {
    mavenCentral()
}
```

```kotlin
dependencies {
    // ...
    implementation(libs.furiganable)
}
```

### Gradle

If you're using Gradle instead, add the following to your `build.gradle` file:

#### Kotlin DSL

```kotlin
dependencies {
    implementation("com.turtlekazu.furiganable:compose-m3:$version")
}
```

#### Groovy DSL

```groovy
dependencies {
    implementation 'com.turtlekazu.furiganable:compose-m3:$version'
}
```

## Add component to your composable function

The format to add furigana is like, `[漢字[かんじ]]`.
To recognize the start and end positions of kanji, outer brackets are required.

### Before
```kotlin
@Composable
fun SampleComponent() {
    // ...

    // I want to add furigana to this Text component
    Text(
        text = "これは試験用の文字列です。",
    )
}
```

### After

```kotlin
@Composable
fun SampleComponent() {
    // ...

    // Replace Text component with TextWithReading component
    TextWithReading(
        formattedText = "これは[試験用[しけんよう]]の[文字列[もじれつ]]です。",
    )
}
```

You can use localized string for text with furigana like below.

In strings.xml(ja),
```xml
<string name="sample_text">これは[試験用[しけんよう]]の[文字列[もじれつ]]です。</string>
```

In your composable function,
```kotlin
@Composable
fun SampleComponent() {
    // ...

    // You can use localized string with furigana
    TextWithReading(
        formattedText = stringResource(R.string.sample_text),
    )
}
```

# Customization

You can customize the appearance of the furigana text by passing additional parameters to the
`TextWithReadingCore`, `TextWithReading`(m2), or `TextWithReading`(m3) components:

- **`furiganaEnabled`**:  
  Whether to enable furigana.  
  If `false`, a normal text component is used.

- **`furiganaGap`**:  
  Space between the main text and the furigana.  
  Defaults to `style.fontSize * 0.03f` if unspecified.

- **`furiganaFontSize`**:  
  Font size for the furigana text.  
  Defaults to `style.fontSize * 0.45f` if unspecified.

- **`furiganaLineHeight`**:  
  Line height for the furigana text.  
  Defaults to `furiganaFontSize` if unspecified.

- **`furiganaLetterSpacing`**:  
  Letter spacing for the furigana text.  
  Defaults to `-style.fontSize * 0.03f` if unspecified.
  
<img width="1024" alt="Furigana Params" src="https://github.com/user-attachments/assets/b8f45ce8-a84d-47d0-a017-12a70aa56848" />



# Examples

See [demo app code](demoApp/composeApp/src/commonMain/kotlin/com/turtlekazu/furiganable/demo/App.kt) for more examples.

# License

Copyright 2025 turtlekazu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
