# Scala consumer example

This standalone Gradle build contains two mods: an object entrypoint with a separate client entrypoint, and a class entrypoint with an unrelated companion. Both use the shared Fabric Language Scala runtime without manually declaring Scala libraries.

From the repository root, run `./gradlew verifyConsumer`. This publishes the module under `build/repo`, passes its exact local version to this build, and compiles both mods. The distributable mods appear in each subproject's `build/libs` directory.

After the pinned language-module version is publicly released, this build can resolve it directly from Fabric Maven. To start a new mod project, copy this example together with the root Gradle wrapper files, keep the object or class subproject you need, and change the mod IDs, packages, and metadata. The root `build.gradle` contains the shared Scala, dependency, and split-client-source configuration.

The Java files under `example/smoke` and their mixin JSON files only let the repository's production smoke tests verify startup and exit automatically. They do nothing unless `fabric.scala.smokeTest` is set. Remove those files and the `mixins` entries from the object mod's metadata when using it as a starting point for a real mod.

The version properties are generated from `templates/examples/scala-mod/gradle.template.properties`; run `./gradlew processMDTemplates` at the repository root after changing versions.
