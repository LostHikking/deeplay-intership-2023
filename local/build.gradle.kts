import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    implementation(project(mapOf("path" to ":game")))
    implementation(project(mapOf("path" to ":tui")))
}

tasks {
    val shadowJar by getting(ShadowJar::class) {
        manifest {
            attributes["Main-Class"] = "io.deeplay.grandmastery.LocalGame"
        }
        archiveBaseName.set("local-game")
    }
}