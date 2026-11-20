/*
 * SPDX-FileCopyrightText: 2025 NewPipe e.V. <https://newpipe-ev.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    // Kotlin artifacts required by the kotlin-dsl plugin are not on the plugin portal
    mavenCentral()
}
