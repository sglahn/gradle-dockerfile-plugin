/**
 Copyright 2017-2018 Sebastian Glahn

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.sglahn.gradle.docker

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.sglahn.gradle.docker.util.DockerHelper
import spock.lang.Specification

class DockerHelperSpec extends Specification {

    def pluginName = 'dockerfile'

    def "Default Dockerfile is returned"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName

        when:
        def dockerfile = DockerHelper.dockerFile(project)

        then:
        dockerfile == (new File(project.projectDir, "Dockerfile"))
    }

    def "Specified Dockerfile is returned"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName
        project.getExtensions().docker.dockerFile = "src/main/docker/Dockerfile"

        when:
        def dockerfile = DockerHelper.dockerFile(project)

        then:
        dockerfile == (new File(project.projectDir, "src/main/docker/Dockerfile"))
    }

    def "Default build context is returned"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName

        expect:
        DockerHelper.buildContext(project) == project.projectDir
    }

    def "Specified build context is returned"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName
        project.getExtensions().docker.buildContext = "build-context"

        expect:
        DockerHelper.buildContext(project) == new File(project.projectDir, "build-context")
    }

    def "Default tag is applied if no tag specified"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("-t ${project.name}:latest")
    }

    def "Specified tags are applied"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName
        project.getExtensions().docker.tags = ["1.0", "version"]

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("-t ${project.name}:1.0")
        arguments.join(" ").contains("-t ${project.name}:version")
    }

    def "Specified labels are applied"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName
        project.getExtensions().docker.labels = ["mylabel=test", "testlabel=foo"]

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--label=mylabel=test")
        arguments.join(" ").contains("--label=testlabel=foo")
    }

    def "Specified build arguments are applied"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName
        project.getExtensions().docker.labels = ["--build-arg=custom='test'"]

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--build-arg=custom='test'")
    }

    def "Default remove intermediate containers is false"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--force-rm=false")
    }

    def "Remove intermediate containers is flag is true"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName
        project.getExtensions().docker.removeIntermediateContainers = true

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--force-rm=true")
    }

    def "Default build context is the project directory"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").endsWith(project.projectDir.getAbsolutePath())
    }

    def "Specified build context is applied"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName
        project.getExtensions().docker.buildContext = "build-context"

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").endsWith(" ${project.projectDir.absolutePath}/build-context")
    }

    def "Default Dockerfile is correct (default build context)"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("-f ${project.projectDir.absolutePath}/Dockerfile")
    }

    def "Default Dockerfile is correct (specified build context)"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName
        project.getExtensions().docker.buildContext = "build-context/"

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("-f ${project.projectDir.absolutePath}/build-context/Dockerfile")
    }

    def "Specified Dockerfile is applied"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName
        project.getExtensions().docker.buildContext = "does/not/matter"
        project.getExtensions().docker.dockerFile = "src/main/docker/Dockerfile"

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("-f ${project.projectDir.absolutePath}/src/main/docker/Dockerfile")
    }

    def "Default container isolation is default"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--isolation=default")
    }

    def "Specified container isolation is applied"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName
        project.getExtensions().docker.isolation = "hyperv"

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--isolation=hyperv")
    }

    def "Cache is disabled as default"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--no-cache=false")
    }

    def "Cache can be activated"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName
        project.getExtensions().docker.noCache = true

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--no-cache=true")
    }

    def "Docker build is quiet as default"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--quiet=true")
    }

    def "Docker output can be activated"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName
        project.getExtensions().docker.quiet = false

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--quiet=false")
    }

    def "Pull newer version of image is deactivated as default"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--pull=false")
    }

    def "Pull newer version of image can be activated"() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: pluginName
        project.getExtensions().docker.quiet = false

        when:
        def arguments = DockerHelper.dockerBuildParameter(project)

        then:
        arguments.join(" ").contains("--quiet=false")
    }
}
