package com.sglahn.gradle.docker

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.tasks.Exec

class DockerPluginExtension {
    def String imageVersion

    def String dockerRepository
    def String imageName
    def String isolation

    def String[] tags
    def String[] labels
    def String[] buildArgs

    def String dockerFile

    def boolean removeIntermediateContainers = false
    def boolean noCache = false
    def boolean pull = false
    def boolean quiet = true
}

class DockerfilePlugin implements Plugin<Project> {

    void apply(Project project) {

        project.extensions.create("docker", DockerPluginExtension)

        project.afterEvaluate {

            project.task('docker', type: Exec) {
                group = 'Docker'
                description = ' Builds a new image with a Dockerfile.'

                doFirst { DockerHelper.checkIfDockerfileExists(project) }

                executable = 'docker'
                workingDir = DockerHelper.dockerFile(project).getParent()
                args = DockerHelper.dockerBuildParameter(project)

                doLast {
                    project.getLogger().debug("Executing: ${executable} ${args.join(" ")}")
                }
            }

            project.task('dockerPush') {
                group = 'Docker'
                description = 'Pushes a docker image to a repository.'
                doLast {
                    project.docker.tags.each {
                        DockerHelper.dockerPush(project, it)
                    }
                }
            }
        }
    }
}

