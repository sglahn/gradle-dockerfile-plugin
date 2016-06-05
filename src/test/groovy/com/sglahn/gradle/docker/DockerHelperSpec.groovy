package com.sglahn.gradle.docker

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DockerHelperSpec extends Specification{

    def "Default Dockerfile is returned"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'

        when:
        def dockerfile = DockerHelper.dockerFile(project)

        then:
        dockerfile.equals(new File(project.projectDir, "Dockerfile"))
    }

    def "Specified Dockerfile is returned"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'
        project.docker.dockerFile = "src/main/docker/Dockerfile"

        when:
        def dockerfile = DockerHelper.dockerFile(project)

        then:
        dockerfile.equals(new File(project.projectDir, "src/main/docker/Dockerfile"))
    }

    def "Default tag is applied if no tag specified"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("-t ${project.name}:latest")
    }

    def "Specified tags are applied"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'
        project.docker.tags = ["1.0", "version"]

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("-t ${project.name}:1.0")
        arguments.join(" ").contains("-t ${project.name}:version")
    }

    def "Specified labels are applied"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'
        project.docker.labels = ["mylabel=test", "testlabel=foo"]

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--label=mylabel=test")
        arguments.join(" ").contains("--label=testlabel=foo")
    }

    def "Specified build arguments are applied"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'
        project.docker.labels = ["--build-arg=custom='test'"]

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--build-arg=custom='test'")
    }

    def "Default remove intermediate containers is false"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--force-rm=false")
    }

    def "Remove intermediate containers is flag is true"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'
        project.docker.removeIntermediateContainers = true

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--force-rm=true")
    }

    def "Default Dockerfile is correct"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("-f ${project.projectDir.absolutePath}/Dockerfile")
    }

    def "Specified Dockerfile is applied"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'
        project.docker.dockerFile = "src/main/docker/Dockerfile"

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("-f ${project.projectDir.absolutePath}/src/main/docker/Dockerfile")
    }

    def "Default container isolation is default "() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--isolation=default")
    }

    def "Specified container isolation is applied"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'
        project.docker.isolation = "hyperv"

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--isolation=hyperv")
    }

    def "Cache is disabled as default"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--no-cache=false")
    }

    def "Cache can be activated"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'
        project.docker.noCache = true

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--no-cache=true")
    }

    def "Docker build is quiet as default"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--quiet=true")
    }

    def "Docker output can be activated"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'
        project.docker.quiet = false

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--quiet=false")
    }

    def "Pull newer version of image is deactivated as default"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--pull=false")
    }

    def "Pull newer version of image can be activated"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'
        project.docker.quiet = false

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--quiet=false")
    }
}
