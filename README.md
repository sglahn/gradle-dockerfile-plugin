# Gradle Dockerfile Plugin
Gradle plugin to build and push Docker images using an external Dockerfile and without the need of inline configuration. This means you can use a normal Dockerfile and put it in your project.
The plugin is available through the [Gradle Plugin Portal](https://plugins.gradle.org).

![Build State](https://github.com/sglahn/gradle-dockerfile-plugin/workflows/Build%20and%20Test/badge.svg)

### Installation
Use the plugin via the Gradle [plugins DSL](https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block):

Groovy:
```Groovy
plugins {
  id "org.sglahn.gradle-dockerfile-plugin" version "0.7"
}
```
Kotlin:
```Kotlin
plugins {
  id("org.sglahn.gradle-dockerfile-plugin") version "0.7"
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

 - dockerFile: `${projectDir}/Dockerfile`.
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
    // Image version. Optional, default = latest
    imageVersion = version

    // Image name. Optional, default = project.name
    imageName = name

    // Docker repository. Optional, default == no repository
    dockerRepository = 'sglahn'

    // Path or URL referring to the build context. Optional, default = ${project.projectDir.getAbsolutePath()}
    buildContext = 'build-context'

    // Path to the Dockerfile to use (relative to ${project.projectDir}). Optional, default = ${buildContext}/Dockerfile
    dockerFile = 'src/main/docker/Dockerfile'

    // Add a list of tags for an image. Optional, default = $imageVersion
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
