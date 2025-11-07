plugins {
    alias(libs.plugins.java.gradle.plugin)
    alias(libs.plugins.maven.publish)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
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
            id = "io.github.vova7878.RaungPlugin"
            implementationClass = "com.v7878.gradle.raung.RaungPlugin"
        }
    }
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = false)
    signAllPublications()

    coordinates(
        groupId = "io.github.vova7878",
        artifactId = "RaungPlugin",
        version = project.version.toString()
    )

    pom {
        name.set("RaungPlugin")
        description.set("Gradle plugin for compiling raung java bytecode")
        inceptionYear.set("2025")
        url.set("https://github.com/vova7878/RaungPlugin")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/license/mit")
                distribution.set("repository")
            }
        }

        developers {
            developer {
                id.set("vova7878")
                name.set("Vladimir Kozelkov")
                url.set("https://github.com/vova7878")
            }
        }

        scm {
            url.set("https://github.com/vova7878/RaungPlugin")
            connection.set("scm:git:git://github.com/vova7878/RaungPlugin.git")
            developerConnection.set("scm:git:ssh://git@github.com/vova7878/RaungPlugin.git")
        }
    }
}
