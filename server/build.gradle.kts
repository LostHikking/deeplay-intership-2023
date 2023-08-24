import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    implementation(project(":game"))
    implementation(project(":conversation"))
    implementation("org.codehaus.janino:janino:3.1.10")
}

tasks {
    val shadowJar by getting(ShadowJar::class) {
        manifest {
            attributes["Main-Class"] = "io.deeplay.grandmastery.Server"
        }
        archiveBaseName.set("server")
    }
}