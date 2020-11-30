plugins {
    java
    application
    groovy
    id("org.sglahn.gradle-dockerfile-plugin") version "0.8"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.guava:guava:29.0-jre")

    testImplementation("org.codehaus.groovy:groovy-all:2.5.11")

    testImplementation("org.spockframework:spock-core:1.3-groovy-2.5")
    testImplementation("junit:junit:4.13")
}

application {
    mainClassName = "example.project.kotlin.App"
}

docker {
    // Image version. Optional, default = project.version
    imageVersion = version.toString()
    // Image name. Optional, default = project.name
    imageName = "test-project"
    // Docker repository. Optional, default == no repository
    dockerRepository = "sglahn"
    // Path or URL referring to the build context. Optional, default = ${project.projectDir.getAbsolutePath()}
    buildContext = "src/main/docker"
    // Path to the Dockerfile to use (relative to ${project.projectDir}). Optional, default = ${buildContext}/Dockerfile
    dockerFile = "src/main/docker/Dockerfile"
    // Add a list of tags for an image. Optional, default = "latest"
    tags = listOf(version.toString(), "latest", "Hello")
    // Set metadata for an image. Optional, default = no label applied
    labels = listOf("branch=master", "mylabel=test")
    // name and value of a buildarg. Optional, default = no build arguments
    buildArgs = listOf("http_proxy=\"http://some.proxy.url\"")
    // Always remove intermediate containers, even after unsuccessful builds. Optional, default = false
    removeIntermediateContainers = true
    // Isolation specifies the type of isolation technology used by containers. Optional, default = default
    isolation = "default"
    // Do not use cache when building the image. Optional, default = false
    noCache = true
    // Always attempt to pull a newer version of the image. Optional, default false
    pull = true
    // Suppress the build output and print image ID on success. Optional, default = true
    quiet = false
    // Remove image on local registry after push to remote registry, useful for builds on CI agents. Optional, default = false
    removeImagesAfterPush = true
}

tasks.named("dockerBuild") {
    dependsOn(":build")
}
tasks.named("dockerPush") {
    dependsOn(":dockerBuild")
}
