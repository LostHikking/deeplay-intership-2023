import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    implementation(project(mapOf("path" to ":game")))
    implementation(project(mapOf("path" to ":tui")))
    implementation(project(mapOf("path" to ":gui")))
    implementation(project(mapOf("path" to ":bots:bots-factory")))
}

tasks {
    val shadowJar by getting(ShadowJar::class) {
        manifest {
            attributes["Main-Class"] = "io.deeplay.grandmastery.Grandmastery"
        }
        archiveBaseName.set("Grandmastery")
        archiveClassifier.set("")
    }
}