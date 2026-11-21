/*
 * SPDX-FileCopyrightText: 2025 NewPipe e.V. <https://newpipe-ev.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "NewPipe"

pluginManagement {
    includeBuild("build-logic/brave-pipe-plugin")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // BravePipe: we have own maven repo for dependencies (jitpack replacement)
        maven(url = "https://raw.githubusercontent.com/bravepipeproject/maven-repo/master/repository")
        maven(url = "https://jitpack.io")
        maven(url = "https://repo.clojars.org")
    }
}
include(":app") // androidApp
include(":desktopApp")
include("shared")

// Use a local copy of NewPipe Extractor by uncommenting the lines below.
// We assume, that NewPipe and NewPipe Extractor have the same parent directory.
// If this is not the case, please change the path in includeBuild().

// Use the checked out BravePipeExtractor repository (the submodule, or a sibling checkout
// for local development) with upstream rebased in, instead of the published extractor.
val bravePipeExtractorDir = listOf(file("BravePipeExtractor"), file("../BravePipeExtractor"))
    .firstOrNull { it.exists() }
if (bravePipeExtractorDir != null) {
    includeBuild(bravePipeExtractorDir) {
        dependencySubstitution {
            substitute(module("com.github.bravepipeproject:extractor"))
                .using(project(":extractor"))
        }
    }
}

//includeBuild("../logcat-toolkit") {
//    dependencySubstitution {
//        substitute(module("com.github.evermind-zz:logcat-toolkit"))
//            .using(project(":"))
//    }
//}

//includeBuild("../challengeFloatsAway") {
//    dependencySubstitution {
//        substitute(module("com.github.evermind-zz:challengeFloatsAway"))
//            .using(project(":library"))
//    }
//}
