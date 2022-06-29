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

class DockerFilePluginExtension {
    String imageVersion

    String dockerRepository
    String imageName
    String isolation

    List<String> tags = []
    List<String> labels = []
    List<String> buildArgs = []
    List<String> platforms = []

    String dockerFile
    String buildContext

    boolean removeIntermediateContainers = false
    boolean noCache = false
    boolean pull = false
    boolean quiet = true
    boolean removeImagesAfterPush = false
}
