import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
    id("java") // Java support
    alias(libs.plugins.kotlin) // Kotlin support
    alias(libs.plugins.gradleIntelliJPlugin) // Gradle IntelliJ Plugin
    alias(libs.plugins.changelog) // Gradle Changelog Plugin
    alias(libs.plugins.qodana) // Gradle Qodana Plugin
    alias(libs.plugins.kover) // Gradle Kover Plugin
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

// Configure project's dependencies
repositories {
    mavenCentral()
}

// Dependencies are managed with Gradle version catalog - read more: https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog
dependencies {
//    implementation(libs.annotations)
    implementation("com.squareup.okhttp3:okhttp:4.9.1") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
    }
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.32")
    implementation("io.ktor:ktor-server-core:2.3.7"){
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("io.ktor:ktor-server-netty:2.3.7") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("io.ktor:ktor-server-cors:2.3.7"){
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
}


// Set the JVM language level used to build the project. Use Java 11 for 2020.3+, and Java 17 for 2022.2+.
kotlin {
    jvmToolchain(17)
}

// Configure Gradle IntelliJ Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    pluginName = properties("pluginName")
    version = properties("platformVersion")
    type = properties("platformType")

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins = properties("platformPlugins").map { it.split(',').map(String::trim).filter(String::isNotEmpty) }
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    groups.empty()
    repositoryUrl = properties("pluginRepositoryUrl")
}

// Configure Gradle Qodana Plugin - read more: https://github.com/JetBrains/gradle-qodana-plugin
qodana {
    cachePath = provider { file(".qodana").canonicalPath }
    reportPath = provider { file("build/reports/inspections").canonicalPath }
    saveReport = true
    showReport = environment("QODANA_SHOW_REPORT").map { it.toBoolean() }.getOrElse(false)
}

// Configure Gradle Kover Plugin - read more: https://github.com/Kotlin/kotlinx-kover#configuration
koverReport {
    defaults {
        xml {
            onCheck = true
        }
    }
}

tasks {
    prepareSandbox {
        from("../../binary/bin") {
            into("${intellij.pluginName.get()}/core/")
        }
    }

    wrapper {
        gradleVersion = properties("gradleVersion").get()
    }

    patchPluginXml {
        version = properties("pluginVersion")
        sinceBuild = properties("pluginSinceBuild")
        untilBuild = properties("pluginUntilBuild")

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with (it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }
//
//        val changelog = project.changelog // local variable for configuration cache compatibility
//        // Get the latest available change notes from the changelog file
//        changeNotes = properties("pluginVersion").map { pluginVersion ->
//            with(changelog) {
//                renderItem(
//                    (getOrNull(pluginVersion) ?: getUnreleased())
//                        .withHeader(false)
//                        .withEmptySections(false),
//                    Changelog.OutputType.HTML,
//                )
//            }
//        }
    }

    // Configure UI tests plugin
    // Read more: https://github.com/JetBrains/intellij-ui-test-robot
    runIdeForUiTests {
        systemProperty("robot-server.port", "8082")
        systemProperty("ide.mac.message.dialogs.as.sheets", "false")
        systemProperty("jb.privacy.policy.text", "<!--999.999-->")
        systemProperty("jb.consents.confirmation.enabled", "false")
    }

    signPlugin {
        certificateChain = """-----BEGIN CERTIFICATE-----
MIIFazCCA1OgAwIBAgIUAiVyrwm67QhnCX74Q5qKl5lWbfUwDQYJKoZIhvcNAQEL
BQAwRTELMAkGA1UEBhMCQVUxEzARBgNVBAgMClNvbWUtU3RhdGUxITAfBgNVBAoM
GEludGVybmV0IFdpZGdpdHMgUHR5IEx0ZDAeFw0yNDA3MjkwMTM1MTZaFw0yNTA3
MjkwMTM1MTZaMEUxCzAJBgNVBAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEw
HwYDVQQKDBhJbnRlcm5ldCBXaWRnaXRzIFB0eSBMdGQwggIiMA0GCSqGSIb3DQEB
AQUAA4ICDwAwggIKAoICAQCHSnH0PywFEUQeKNkEv8FF44d6twhJpGVfcbV7ikiU
ot5inOUnZn/XE0T/ANxFAMdHulXK5+us60v7yLZ6IuVeSdwnNtiyCacpL6bGhwzN
YsnSRV2s2ht1z7R+zHuCpHn9BD4ovbCh4d4xmB9IOcIWsC6tUzLqCPNkvcwnNgbe
0mbyS5MzD087C68utWI/Rf9D46v1xaZz7WYvi5HV6+rOtc0RJw+MrSVpHPU+DcV2
/AiKsMXCiUCg6Vdt1l1fnoF1OwPiZSqKdTA/gbp8ISp1+jHVkVHFEdQGrbKnvTzS
+IQ+/P3C+ogzhrTiyIYMb+A5SWhE608TH0RYHGIhBy89XQsRaQUBsAJQz1IBtOkN
ODlLPPCRlGQ0E7/VeNj5Kw3qL/ONJNkdmqBH1smRtkQZ/zBMkt1aVVuQE5prlGiX
uskkppZi7Og7qNP9s4N8kR8MaEwSXhqUORV4srx0qnAxfj1aJxbrxWszRBlC6j+i
O39kc/KxKI3FTOjpXTL+HbhaHQUW4sm5bqTTAhet0z4w0hktT4x10DnmBDIe8cNw
s5tkNj/r9am4gAcK8ngFP6tYfqTdR+1SLMu4FcEo8FS7j3s6oMYRCJCb32HqL0ej
3rgUgRDuI4CuAmoC4UzDwv/ky2vfP8iqnkelYX/PjKgGYZ7E2iFILlIZfvMqdlWk
VQIDAQABo1MwUTAdBgNVHQ4EFgQUfWmZ9fLojyv+abiSDd+Jhaz3UNIwHwYDVR0j
BBgwFoAUfWmZ9fLojyv+abiSDd+Jhaz3UNIwDwYDVR0TAQH/BAUwAwEB/zANBgkq
hkiG9w0BAQsFAAOCAgEAciBdtiiWD6qn33Ee2em/CY07Kw5mszIkwqBcN/quM3OR
fyQc6Dwr6nDLyOGT8Xf9yJwtmaej5mP/a0PGYzDBNfBijVG74vtOV7F5jWDmYeNJ
o92y8yiW7juwhOH7NxFo/8DaAzQlyoKuVLLd52chYcQHoYcEEhxtTs4FI6j3bhPj
vDS+v2RiNJwEMoYFm+jLo8nlfjJHMKFS/80RP7BAZLTc9qfs4BjIWyAEq0H+z5PP
YOE8hc2n9kSF4pvC0xkywoIcA5XY/rOq5KomCwqldLobjLHeNznVKdn4z75NFRAt
KqBqJiB6Z5FSLswbr7mC6SN9ptNKpMKGzp4jAfjPWQbddSAoS7nWfh1HgspXSqi8
n5hNTN+PghIEQI371ixvmvg+RanWF4V7DQD+qBGDZFejV7KIo45r9qQiIVwk9R7K
TxRtyc1iz1zgxF1eGLrkc2+APSsXdt8Dx0Sc65V1JZDJluV2kZkxumJQ9POEpMt3
yoIjjQLjRakJvI3POpPLQahd7F7eQPHLTd3mxSZlFy1WtonkT994u9mvWqVbN48V
thH5FoeRB82C9lrWSQTSDRWnssk753Kkhw5Bg9B8a+X1eVosIP4FHGGR4BYXlO5X
ViH1SLPf6SP76rWaHROZKQ12QPV81Igg9DCQ6cyWRK5idTjqrFP/M5KGmWYjP2E=
-----END CERTIFICATE-----
"""
        privateKey = """-----BEGIN PRIVATE KEY-----
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
1yFlXSfKo5aFN1vzh816ImMUGfegbI6r
-----END PRIVATE KEY-----
"""
        password = "hanan"
    }

    publishPlugin {
//        dependsOn("patchChangelog")
        token = "perm:eGlhb3l1Y3VueA==.OTItMTA0OTE=.zidXB45shxHvHdLgD5LMYCppgboiU7"
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels = properties("pluginVersion").map { listOf(it.split('-').getOrElse(1) { "default" }.split('.').first()) }
    }
}
