pluginManagement {
        resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.sglahn.gradle-dockerfile-plugin") {
                useModule("org.sglahn:gradle-dockerfile-plugin:${requested.version}")
            }
        }
    }
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

rootProject.name = "gradle-dockerfile-plugin-example-project-kotlin-dsl"
