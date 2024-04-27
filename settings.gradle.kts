pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // to implementation "com.github.Dhaval2404:ColorPicker:2.3"
        maven("https://jitpack.io")
    }
}

rootProject.name = "ReaderApp"
include(":app")
 