![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)
[![License](https://img.shields.io/github/license/vova7878/RaungPlugin)](https://github.com/vova7878/RaungPlugin/blob/main/LICENSE)

# About

This project is a gradle plugin for compiling [raung](https://github.com/skylot/raung) - smali-like assembly language for java

### Installation

```kotlin
plugins {
    id("java" or "com.android.application" or "com.android.library")
    id("io.github.vova7878.RaungPlugin") version "<version>"
}
```

The plugin is suitable for both standard java and android projects. It automatically integrates into the build and compiles all .raung files in each source set (supports custom source sets), allowing java code in them to reference raung classes
