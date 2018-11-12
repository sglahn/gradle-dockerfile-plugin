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
import org.gradle.api.Plugin
import org.sglahn.gradle.docker.tasks.DockerBuildTask
import org.sglahn.gradle.docker.tasks.DockerPushTask

class DockerfilePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.getExtensions().create('docker', DockerFilePluginExtension)

        project.getTasks().create('dockerBuild', DockerBuildTask)
        project.getTasks().create('dockerPush', DockerPushTask)
    }
}

