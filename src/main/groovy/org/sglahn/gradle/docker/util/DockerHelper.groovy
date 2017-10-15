/**
 Copyright 2017 Sebastian Glahn

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
package org.sglahn.gradle.docker.util

import org.gradle.api.GradleException

class DockerHelper {

    static File dockerFile(project) {
        if(project.extensions.docker.dockerFile) {
            new File(project.projectDir, project.extensions.docker.dockerFile)
        } else {
            new File(buildContext(project), 'Dockerfile')
        }
    }

    static File buildContext(project) {
        if (project.extensions.docker.buildContext) {
            new File(project.projectDir, project.extensions.docker.buildContext)
        } else {
            project.projectDir
        }
    }

    static Boolean checkIfDockerfileExists(project) {
        def dockerFile = DockerHelper.dockerFile(project)
        if (!dockerFile.exists()) {
            throw new GradleException("Dockerfile not found in ${dockerFile.getAbsolutePath()}.")
        }
        true
    }

    static void executeCmd(project, cmd) {
        project.getLogger().info("Executing: ${cmd}")
        def stdout = new StringBuffer()
        def stderr = new StringBuffer()

        def p = cmd.execute()
        p.consumeProcessOutput(stdout, stderr)
        p.waitFor()

        project.getLogger().error("$stderr")
        project.getLogger().info("$stdout")

        if (p.exitValue() != 0) {
            throw new GradleException("Command: '${cmd}' failed. Reason: '${stderr}'.")
        }
    }

    static void dockerPush(project, tag) {
        def dockerRepository = project.docker.dockerRepository ?: ''
        if (!dockerRepository.empty && !dockerRepository.endsWith('/')) dockerRepository = dockerRepository + '/'
        def imageName = project.docker.imageName ?: project.getName()
        def cmd = "docker push ${dockerRepository}${imageName}:${tag}"

        executeCmd(project, cmd)
    }

    static void removeDockerImage(project, tag) {
        def dockerRepository = project.docker.dockerRepository ?: ''
        if (!dockerRepository.empty && !dockerRepository.endsWith('/')) dockerRepository = dockerRepository + '/'
        def imageName = project.docker.imageName ?: project.getName()
        def cmd = "docker rmi ${dockerRepository}${imageName}:${tag}"

        executeCmd(project, cmd)
    }

    static List<String> dockerBuildParameter(project) {
        List<String> arguments = new ArrayList<>()

        def dockerRepository = project.docker.dockerRepository ?: ''
        if (!dockerRepository.empty && !dockerRepository.endsWith('/')) dockerRepository = dockerRepository + '/'
        def imageName = project.docker.imageName ?: project.getName()
        def imageVersion = project.docker.imageVersion ?: "latest"

        arguments.add('build')
        if (project.docker.tags == null) {
            arguments.add('-t')
            arguments.add("${dockerRepository}${imageName}:${imageVersion}")
        }
        project.docker.tags.each {
            arguments.add('-t')
            arguments.add("${dockerRepository}${imageName}:${it}")
        }

        project.docker.labels.each {
            arguments.add("--label=${it}")
        }

        project.docker.buildArgs.each {
            arguments.add("--build-arg=${it}")
        }

        if (project.docker.isolation == null) {
            arguments.add("--isolation=default")
        }
        else {
            arguments.add("--isolation=${project.docker.isolation}")
        }

        arguments.add("--force-rm=${project.docker.removeIntermediateContainers}")
        arguments.add("--no-cache=${project.docker.noCache}")
        arguments.add("--quiet=${project.docker.quiet}")
        arguments.add("--pull=${project.docker.pull}")

        arguments.add('-f')
        arguments.add(dockerFile(project).getAbsolutePath())
        arguments.add(project.docker.buildContext)

        arguments
    }
}
