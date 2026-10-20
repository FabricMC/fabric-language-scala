# Fabric Language Scala

Fabric language module for [Scala](https://www.scala-lang.org/). It bundles the Scala runtime and lets Fabric mods use Scala objects and ordinary classes as entrypoints.

The revival release is **3.0.0-beta.1+scala.3.10.0**. It targets **Minecraft 26.3**, **Fabric Loader 0.19.5 or newer**, and **Scala 3.10.0**. Building and running that Minecraft version requires **JDK 25**. The language module itself targets Java 17; older Minecraft versions have not been qualified.

This is a beta until the compatibility checks pass and FabricMC publishes it. Before publication, use the temporary local publication through `./gradlew verifyConsumer`.

## Use it in a mod

Use Gradle 9.8.2, Loom 1.19.2, the standard `scala` plugin, and JDK 25. Pin the Scala compiler to the bundled runtime version:

```groovy
plugins {
    id 'scala'
    id 'net.fabricmc.fabric-loom' version '1.19.2'
}

scala {
    scalaVersion = '3.10.0'
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

dependencies {
    minecraft 'com.mojang:minecraft:26.3'
    implementation 'net.fabricmc:fabric-loader:0.19.5'
    implementation 'net.fabricmc:fabric-language-scala:3.0.0-beta.1+scala.3.10.0'
}
```

Loom supplies Fabric Maven. The language-module dependency exposes the Scala runtime to the compiler; do not add a different Scala standard library or bundle a second copy in each mod. Current unobfuscated Minecraft uses `implementation`; older Loom projects may require `modImplementation` and a different mapping setup.

Create `src/main/scala/example/MyMod.scala`:

```scala
package example

import net.fabricmc.api.ModInitializer

object MyMod extends ModInitializer {
  override def onInitialize(): Unit = println("Hello from Scala!")
}
```

Declare it in `src/main/resources/fabric.mod.json`:

```json
{
  "schemaVersion": 1,
  "id": "my-scala-mod",
  "version": "1.0.0",
  "name": "My Scala Mod",
  "environment": "*",
  "entrypoints": {
    "main": [{"adapter": "scala", "value": "example.MyMod"}]
  },
  "depends": {
    "fabric-language-scala": ">=3.0.0-beta.1+scala.3.10.0",
    "fabricloader": ">=0.19.5",
    "minecraft": "26.3",
    "java": ">=25"
  }
}
```

Install Fabric Language Scala alongside your built mod on both client and server. A Maven dependency supplies libraries during development; players still need the language-module JAR.

## Entrypoints and client code

An ordinary `class MyMod extends ModInitializer` also works when it has an accessible no-argument constructor. Class construction follows Fabric's default adapter. For objects, the adapter returns the existing singleton. If both a class and its companion object implement the requested entrypoint interface, the object takes precedence. Unrelated companion objects are not initialized.

Entrypoint values are fully qualified object/class names. Custom `Class::member` references are not supported. Missing classes, invalid types, and construction or object-initialization failures are reported with their causes.

For a client entrypoint, implement `ClientModInitializer` in `src/client/scala`, declare it under `entrypoints.client`, and enable Loom's split environment source sets. The [two-mod consumer example](examples/scala-mod) shows the complete configuration and keeps client-only Minecraft classes off the dedicated-server initialization path.

The module bundles only these runtime libraries, both at **3.10.0**:

- `org.scala-lang:scala3-library_3`
- `org.scala-lang:scala-library`

Scala 3.8 and newer use a standard library compiled by Scala 3; the old Scala 2.13 runtime pairing is not appropriate here. Compiler compatibility and compatibility of previously compiled mods are separate: older Scala compilers, Scala 2 authoring, and historical Minecraft releases are not covered by this release. Mod authors choose and package additional ecosystem libraries themselves.

## Build and verify

Install JDK 25, then run:

```sh
./gradlew build
./gradlew prodServer
./gradlew prodClient
./gradlew publishMods
```

The checked-in daemon JVM criteria select an installed JDK 25 even when your shell uses another Java version. A Java compilation toolchain alone does not select the JVM that loads Loom. Check `./gradlew --version` for `Daemon JVM` when diagnosing a Java-version error.

`build` runs adapter tests, distributable and Maven-metadata checks, and a standalone build of both consumer mods against `build/repo`. `prodServer` and `prodClient` launch the packaged JARs using Loom's production launchers, verify both common entrypoints and the appropriate client entrypoint, and exit after startup. Their temporary files and logs are under `build/smoke`. The server test writes EULA acceptance in that test directory and binds only to localhost with an ephemeral port. The client test needs a graphical session or Linux Xvfb with software graphics support.

To check the language module on another supported Java runtime, keep Gradle on JDK 25 and select an installed test JDK:

```sh
./gradlew test -PtestJava=17
./gradlew test -PtestJava=21
./gradlew test -PtestJava=25
```

Gradle must be able to discover the selected JDK; otherwise pass its home using `-Dorg.gradle.java.installations.paths=/path/to/jdk`.

Running `testJava17` and `testJava21` together requires JDKs 17 and 21 in addition to JDK 25. On macOS, install JDK bundles in `~/Library/Java/JavaVirtualMachines` or `/Library/Java/JavaVirtualMachines` so Gradle can discover them.

`publishMods` defaults to a dry run even when tokens are present. It writes previews under `build/publishMods` and performs no uploads. Local artifacts carry a `.local` suffix.

## Releases and maintenance

Official releases run manually through the Release workflow in `FabricMC/fabric-language-scala`. Configure `MAVEN_URL`, `MAVEN_USERNAME`, `MAVEN_PASSWORD`, and `CURSEFORGE_API_KEY` as repository secrets. The workflow supplies `GITHUB_TOKEN` and generates a changelog. It runs all checks and both production launches before Maven, CurseForge project `309275`, or GitHub uploads. Direct upload tasks have the same checks. `-PpublishRelease` explicitly enables real mod uploads and removes the local version suffix.

Modrinth is disabled until FabricMC provides an official `MODRINTH_PROJECT_ID` repository variable and `MODRINTH_TOKEN` secret. Only Minecraft 26.3 is labeled compatible. The current `mod_version` produces a beta; promote it to `3.0.0` only after beta feedback and passing checks.

A weekly workflow proposes stable Scala 3 updates. It updates the shared Scala version and advances the module patch version together, regenerates documentation and consumer properties, and checks the build before opening a PR. Updates never publish automatically. Build-tool updates use Dependabot; Fabric Loader updates must be checked against Fabric's stability metadata.

Edit this README in `templates/README.template.md`, then run:

```sh
./gradlew processMDTemplates
```

After `./gradlew updateVersions`, run template generation in a **separate Gradle invocation** so it reads the new versions. Every runtime release must also advance `mod_version`; the Scala suffix alone is not a version-ordering guarantee.

The revival builds on the earlier work and failure reports in [PR #23](https://github.com/FabricMC/fabric-language-scala/pull/23) and [PR #24](https://github.com/FabricMC/fabric-language-scala/pull/24), with [Fabric Language Kotlin](https://github.com/FabricMC/fabric-language-kotlin) as the reference for runtime packaging, tests, and publishing.

[Published Maven versions](https://maven.fabricmc.net/net/fabricmc/fabric-language-scala/) · [Issues](https://github.com/FabricMC/fabric-language-scala/issues) · [Apache-2.0 license](LICENSE)
