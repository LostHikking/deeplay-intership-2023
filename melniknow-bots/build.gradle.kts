import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    implementation(project(":game"))
    implementation(project(":bot-farm"))
    implementation(project(":gui"))
}

tasks {
    val shadowJar by getting(ShadowJar::class) {
        manifest {
            attributes["Main-Class"] = "io.deeplay.grandmastery.AiBotsGame"
        }
        archiveBaseName.set("local-game")
    }
}