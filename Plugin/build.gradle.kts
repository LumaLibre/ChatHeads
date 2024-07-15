import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.5.12"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.jsinco"
version = "1.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.opencollab.dev/main/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0")
    implementation("com.github.Jsinco:AbstractJavaFileLib:1.3")
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
    compileOnly("org.geysermc.floodgate:api:2.2.2-SNAPSHOT")
}


tasks {

    java {
        toolchain.languageVersion = JavaLanguageVersion.of(17)
    }

    assemble {
        dependsOn("reobfJar")
    }

    processResources {
        outputs.upToDateWhen { false }
        filter<ReplaceTokens>(mapOf(
            "tokens" to mapOf("version" to project.version.toString()),
            "beginToken" to "\${",
            "endToken" to "}"
        ))
    }

    shadowJar {
        dependencies {
            include(dependency("com.github.Jsinco:AbstractJavaFileLib"))
        }
        archiveClassifier.set("")
    }

    jar {
        enabled = false
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}