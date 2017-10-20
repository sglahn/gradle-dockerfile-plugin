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
package org.sglahn.gradle.docker.tasks

import org.gradle.api.tasks.Exec
import org.sglahn.gradle.docker.util.DockerHelper

class DockerBuildTask extends Exec {

    String group = 'Docker'
    String description = 'Builds a new image with a Dockerfile.'

    DockerBuildTask() {
        project.afterEvaluate {
            doFirst {
                DockerHelper.checkIfDockerfileExists(project)
                DockerHelper.checkIfBuildContextExists(project)
            }

            this.setExecutable("docker")
            this.setArgs(DockerHelper.dockerBuildParameter(project))

            doLast {
                project.getLogger().error("Executing: ${executable} ${args.join(" ")}")
            }
        }
    }

}
