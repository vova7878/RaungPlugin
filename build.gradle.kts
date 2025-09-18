plugins {
    alias(libs.plugins.java.gradle.plugin)
    alias(libs.plugins.maven.publish)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    withSourcesJar()
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(libs.android.gradle)

    implementation(libs.raung.asm)
}

gradlePlugin {
    plugins {
        register("zygote") {
            id = when {
                rootProject.group.toString().isEmpty() -> rootProject.name
                else -> "${rootProject.group}.${rootProject.name}"
            }
            implementationClass = "com.v7878.gradle.raung.RaungPlugin"
        }
    }
}
