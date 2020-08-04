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
package org.sglahn.gradle.docker.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.sglahn.gradle.docker.util.DockerHelper

class DockerPushTask extends DefaultTask {

//    String group = 'Docker'
//    String description = 'Pushes a docker image to a repository.'

    @TaskAction
    def action() {
        if (project.getExtensions().docker.tags == null) {
            project.getExtensions().docker.tags = [ project.docker.imageVersion ] ?: [ 'latest' ]
        }
        else {
            project.getExtensions().docker.tags.each {
                DockerHelper.dockerPush(project, it)
                if (project.docker.removeImagesAfterPush) {
                    DockerHelper.removeDockerImage(project, it)
                }
            }
        }
    }
}
