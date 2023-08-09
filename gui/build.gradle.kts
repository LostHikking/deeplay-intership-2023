plugins {
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
}

javafx {
    version = "11.0.2"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation(project(":game"))
}