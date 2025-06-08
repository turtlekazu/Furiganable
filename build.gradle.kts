plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.mavenPublish) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.dokka)
}

dokka {
    moduleName.set("furiganable")
    dokkaPublications.html {
        outputDirectory.set(rootDir.resolve("docs"))
    }

    pluginsConfiguration.html {
        moduleVersion.set(rootProject.properties["VERSION_NAME"].toString())
    }
}

dependencies {
    dokka(project(":furiganable:compose-core"))
    dokka(project(":furiganable:compose-m2"))
    dokka(project(":furiganable:compose-m3"))
}
