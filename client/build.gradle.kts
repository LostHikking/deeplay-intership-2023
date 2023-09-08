import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    implementation(project(":game"))
    implementation(project(":conversation"))
    implementation(project(":tui"))
    implementation(project(":gui"))
}

tasks {
    val shadowJar by getting(ShadowJar::class) {
        manifest {
            attributes["Main-Class"] = "io.deeplay.grandmastery.Client"
        }
        archiveClassifier.set("")
    }
}