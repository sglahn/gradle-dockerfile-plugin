# Gradle Dockerfile Plugin
Gradle plugin to build and push Docker images using an external Dockerfile and without the need of inline configuration. This means you can use a normal Dockerfile and put it in your project.
The plugin is available through [Maven Central](https://search.maven.org/#search%7Cga%7C1%7Cgradle-dockerfile-plugin) and the [Gradle Plugin Portal](https://plugins.gradle.org).

[![CircleCI](https://circleci.com/gh/sglahn/gradle-dockerfile-plugin/tree/master.svg?style=svg)](https://circleci.com/gh/sglahn/gradle-dockerfile-plugin/tree/master)

### Installation
To use the plugin add a build script dependency to your Gradle build file:
```gradle
buildscript {
    repositories { mavenCentral() }
    dependencies { classpath('org.sglahn:gradle-dockerfile-plugin:0.4') }
}
apply plugin: 'dockerfile'
```
or via the new plugin mechanism introduced in Gradle 2.1:
```
plugins {
  id "org.sglahn.gradle-dockerfile-plugin" version "0.4"
}
```
The plugin will add the following tasks to your project:
```sh
$ ./gradlew tasks
Docker tasks
------------
dockerBuild - Builds a new image with a Dockerfile.
dockerPush - Pushes a docker image to a repository.
```
### The dockerBuild task
The `dockerBuild` task will build a new Docker image. The default settings are:

 - dockerfile: `${projectDir}/Dockerfile`.
 - imageName: `project.name`
 - tags: `project.version` and `latest`.

For more information see `Configuration` section.
### The dockerPush task
The `dockerPush` task will push the Docker image to a Docker repository.
If authentication is required use [docker login](https://docs.docker.com/engine/reference/commandline/login/) to
add the credential to your `$HOME/.docker/config.json` file. [This](https://hub.docker.com/r/sglahn/gradle-dockerfile-plugin-example-project/) 
is how it looks like when the example project is pushed to DockerHub. When the property "removeImagesAfterPush" is set to `true`, 
the image will be removed from the local repository after the push to a remote repository. This is useful e.g. for builds 
on CI agents. 
### Configuration
The following configuration can be added to your Gradle build file:
```gradle
docker {
    // Image version. Optional, default = project.version
    imageVersion = version
    // Image name. Optional, default = project.name
    imageName = name
    // Docker repository. Optional, default == no repository
    dockerRepository = 'sglahn'
    // Path to the Dockerfile to use. Optional, default = ${projectDir}/Dockerfile
    dockerFile = 'src/main/docker/Dockerfile'
    // Add a list of tags for an image. Optional, default = 'latest'
    tags = [version, 'latest', 'Hello']
    // Set metadata for an image. Optional, default = no label applied
    labels = ['branch=master', 'mylabel=test']
    // name and value of a buildarg. Optional, default = no build arguments
    buildArgs = ['http_proxy="http://some.proxy.url"']
    // Always remove intermediate containers, even after unsuccessful builds. Optional, default = false
    removeIntermediateContainers = true
    // Isolation specifies the type of isolation technology used by containers. Optional, default = default
    isolation = 'default'
    // Do not use cache when building the image. Optional, default = false
    noCache = true
    // Always attempt to pull a newer version of the image. Optional, default false
    pull = true
    // Suppress the build output and print image ID on success. Optional, default = true
    quiet = false
    // Remove image in local repository after push to a remote repository, useful for builds on CI agents. Optional, default = false
    removeImagesAfterPush = true
}
```
