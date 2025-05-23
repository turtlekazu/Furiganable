# Furiganable

Furiganable is a simple library that allows you to add furigana (ruby) to Japanese text. It is designed to be easy to use and flexible.

The latest demo app APK can be found in the [releases](https://github.com/turtlekazu/Furiganable/releases) section under the "Assets" section of the latest release.

This library is heavily inspired by the [android-compose-furigana](https://github.com/mainrs/android-compose-furigana/) library.

# Features

- Supports Compose Multiplatform (Android, iOS)
- Supports Localization

# Usage

## Add dependency on this library

### Version Catalog

If you're using Version Catalog, add the following to your `libs.versions.toml` file:

```toml
[versions]
#...
furiganable = "0.0.0"

[libraries]
#...
furiganable = { module = "com.turtlekazu.lib:furiganable", version.ref = "furiganable" }
```

or

```toml
[libraries]
#...
furiganable = { module = "com.turtlekazu.lib:furiganable", version = "0.0.0" }
```

then

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
    implementation("com.turtlekazu.lib:furiganable:0.0.0")
}
```

#### Groovy DSL

```groovy
dependencies {
    implementation 'com.turtlekazu.lib:furiganable:0.0.0'
}
```

## Add component to your composable function

### Before
```kotlin
@Composable
fun SampleComponent() {
    ...

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
    ...

    // Replace Text component with TextWithReading component
    TextWithReading(
        text = "これは[試験用[しけんよう]]の[文字列[もじれつ]]です。",
    )
}
```

You can use localized string for text with furigana like below

```strings.xml(ja)
<string name="sample_text">これは[試験用[しけんよう]]の[文字列[もじれつ]]です。</string>
```

```kotlin
@Composable
fun SampleComponent() {
    ...

    // You can use localized string with furigana
    TextWithReading(
        text = stringResource(R.string.sample_text),
    )
}
```

# Examples

See [demo app code](demoApp/composeApp/src/commonMain/kotlin/com/turtlekazu/furiganable/demo) for more examples.

# License

Copyright 2025 Kazuhiro ISHIBASHI

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.