dependencies {
    implementation(project(mapOf("path" to ":game")))
    implementation(project(mapOf("path" to ":gui")))
    implementation("org.nd4j:nd4j-native-platform:1.0.0-M2.1")
    implementation("org.deeplearning4j:deeplearning4j-core:1.0.0-M2.1")
}
