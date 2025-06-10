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
 <img src="https://github.com/user-attachments/assets/2cb83b2d-51c9-4c82-a269-be3ee04517e7" width="320px"> | <img src="https://github.com/user-attachments/assets/b6a61b77-e397-422f-9c41-f00a2de8ef83" width="320px"> | <img src="https://github.com/user-attachments/assets/4c464e21-3571-49e4-bcb6-beab767bc8cf" width="320px">
    
</details>

<details open>
    <summary>Material 3</summary>

 Android (> 28) | Android (<= 28) | iOS 18 
 --- | --- | ---
 <img src="https://github.com/user-attachments/assets/9d1604dd-903f-455d-8861-1b60f84aeb38" width="320px"> | <img src="https://github.com/user-attachments/assets/85c56c90-1e88-4576-9433-8d2d529e50d7" width="320px"> | <img src="https://github.com/user-attachments/assets/e6b293c9-c666-4e37-ac3d-acac25de910f" width="320px">
    
</details>

# API Reference

[API Reference🐢](https://turtlekazu.github.io/Furiganable/)

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
    TextWithReadingM3(
        text = "これは[試験用[しけんよう]]の[文字列[もじれつ]]です。",
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
    TextWithReadingM3(
        text = stringResource(R.string.sample_text),
    )
}
```

# Advanced Usage

## Customization 

You can customize the appearance of the furigana text by passing additional parameters to the `TextWithReadingM3` or `TextWithReadingM2` components.

`furiganaFontSize` - Font size for the furigana text. If unspecified, `style.fontSize * 0.5f`.
`furiganaLineHeight` - Line height for the furigana text. If unspecified, uses `furiganaFontSize * 1.2f`.
`furiganaLetterSpacing` - Letter spacing for the furigana text. If unspecified, uses `-letterSpacing * 0.05f`.

// TODO: Image

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
