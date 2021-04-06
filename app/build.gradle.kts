import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.4.32"
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

repositories {
    jcenter()
}

val invoker by configurations.creating

dependencies {
    // Kotlin.
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Functions framework.
    implementation("com.google.cloud.functions:functions-framework-api:1.0.1")
    invoker("com.google.cloud.functions.invoker:java-function-invoker:1.0.0-alpha-2-rc5")

    // Testing libraries.
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()
}

val appName = "kotlin-functions-framework-example"
val functionTarget = "io.github.dfings.example.App"
val splitter = "\\s+".toRegex()

task<Exec>("pack") {
    dependsOn("test", "clean", "shadowJar")
    commandLine = """pack build $appName
        --path build/libs
        --env GOOGLE_FUNCTION_TARGET=$functionTarget
        --builder gcr.io/buildpacks/builder:v1""".split(splitter)
}

task<JavaExec>("run") {
    dependsOn("shadowJar")
    inputs.files("app.jar")

    main = "com.google.cloud.functions.invoker.runner.Invoker"
    args("--target", functionTarget)

    classpath(invoker)
    doFirst {
        args("--classpath", files(configurations.runtimeClasspath, sourceSets["main"].output).asPath)
    }
}

task<Exec>("runDocker") {
    // Note the image must be built first.
    commandLine = "docker run --rm -p 8080:8080 $appName".split(splitter)
}
