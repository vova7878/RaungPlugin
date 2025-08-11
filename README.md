![Java 21+](https://img.shields.io/badge/Java-21%2B-blue)
[![License](https://img.shields.io/github/license/vova7878/RaungPlugin)](https://github.com/vova7878/RaungPlugin/blob/main/LICENSE)

# About

This project is a gradle plugin for compiling [raung](https://github.com/skylot/raung) - smali-like assembly language for java

### Installation

```java
plugins {
    id 'java' or 'com.android.library' or 'com.android.application'
    id 'com.github.vova7878.RaungPlugin' version 'v0.0.1'
}
```

The plugin is suitable for both standard java projects and android libraries or applications. It automatically integrates into the build and compiles all .raung files in each source set (supports custom source sets), allowing java code in them to reference raung classes
