import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    implementation(project(":game"))
    implementation(project(":conversation"))
    implementation(project(":bots"))
    implementation("org.reflections:reflections:0.10.2")
}

tasks {
    val shadowJar by getting(ShadowJar::class) {
        manifest {
            attributes["Main-Class"] = "io.deeplay.grandmastery.botfarm.BotFarm"
        }
        archiveClassifier.set("")
    }
}