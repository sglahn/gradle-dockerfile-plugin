package org.sglahn.gradle.docker

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DockerfilePluginSpec extends Specification {

    def "Plugin can be applied"() {
        given:
        Project project = ProjectBuilder.builder().build()

        when:
        project.apply plugin: 'dockerfile'

        then:
        notThrown Exception
    }

    def "Plugin task is available and has a description and a group"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'

        expect:
        project.tasks['dockerBuild'].group == "Docker"
        project.tasks['dockerBuild'].description != ""
    }

    def "Plugin task fails with Exception if Dockerfile not found"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'
        project.getExtensions().docker.dockerFile = "src/foo/Dockerfile"
        project.evaluate()

        when:
        project.tasks['dockerBuild'].execute()

        then:
        GradleException exception = thrown()
        exception.getCause().getLocalizedMessage().contains("Dockerfile not found in ")
    }

    def "Plugin task fails with Exception if default Dockerfile not found"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'
        project.evaluate()

        when:
        project.tasks['dockerBuild'].execute()

        then:
        GradleException exception = thrown()
        exception.getCause().getLocalizedMessage().contains("Dockerfile not found in ")
    }

    def "Plugin task fails with Exception if build context not found"() {
        given: 'a project with a not existing build context'
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dockerfile'
        project.getExtensions().docker.buildContext = "src/foo"
        project.getExtensions().docker.dockerFile = 'Dockerfile'
        project.evaluate()

        and: 'an existing Dockerfile to pass the preceding Dockerfile check'
        File existingDockerfile = project.file('Dockerfile')
        existingDockerfile.createNewFile()

        when:
        project.tasks['dockerBuild'].execute()

        then:
        GradleException exception = thrown()
        exception.getCause().getLocalizedMessage().contains("Build context ")
        exception.getCause().getLocalizedMessage().contains(" does not exist.")
    }
}
