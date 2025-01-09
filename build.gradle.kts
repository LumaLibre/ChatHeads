import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.7.5"
    id("com.gradleup.shadow") version "8.3.5"
}

group = "dev.jsinco.chatheads"
version = "1.5"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.opencollab.dev/main/")
    maven("https://repo.essentialsx.net/releases/")
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    paperweight.paperDevBundle("1.21.3-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0")
    implementation("com.github.Jsinco:AbstractJavaFileLib:1.3")
    compileOnly("org.geysermc.floodgate:api:2.2.2-SNAPSHOT")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {

    java {
        toolchain.languageVersion = JavaLanguageVersion.of(21)
    }

    assemble {
        dependsOn(reobfJar)
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