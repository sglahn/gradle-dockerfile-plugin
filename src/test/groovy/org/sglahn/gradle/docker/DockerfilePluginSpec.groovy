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
import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import org.gradle.testkit.runner.GradleRunner

class DockerfilePluginSpec extends Specification {
    @Rule TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id 'dockerfile'
            }
        """
    }

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

    def "Plugin task fails with Exception if default Dockerfile not found"() {
        when:
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('dockerBuild')
                .withPluginClasspath()
                .build()

        then:
        UnexpectedBuildFailure exception = thrown()
        exception.message.contains("Dockerfile not found in ")
    }

    def "Plugin task fails with Exception if Dockerfile not found"() {
        given:
        buildFile << """
            docker {
                dockerFile = 'src/foo/Dockerfile'
            }
        """

        when:
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('dockerBuild')
                .withPluginClasspath()
                .build()

        then:
        UnexpectedBuildFailure exception = thrown()
        exception.message.contains("Dockerfile not found in ")
    }

    def "Plugin task fails with Exception if build context not found"() {
        given: 'a project with a not existing build context'
        buildFile << """
            docker {
                dockerFile = 'Dockerfile'
                buildContext = 'src/foo'
            }
        """

        and: 'an existing Dockerfile to pass the preceding Dockerfile check'
        testProjectDir.newFile('Dockerfile')

        when:
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('dockerBuild')
                .withPluginClasspath()
                .build()

        then:
        UnexpectedBuildFailure exception = thrown()
        exception.message.contains("Build context ")
        exception.message.contains(" does not exist.")
    }
}
