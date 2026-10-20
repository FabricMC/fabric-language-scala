package example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public final class SmokeChecks {
    public static void complete(String environment) throws IOException {
        var loader = FabricLoader.getInstance();
        if (loader.isDevelopmentEnvironment()) {
            throw new AssertionError("Smoke tests must run the packaged production mods");
        }
        for (String entrypoint : environment.equals("client")
                ? new String[]{"object", "class", "client"} : new String[]{"object", "class"}) {
            if (!"ready".equals(loader.getObjectShare().get("scala-example:" + entrypoint))) {
                throw new AssertionError("Scala entrypoint did not run: " + entrypoint);
            }
        }
        String version = loader.getModContainer("fabric-language-scala").orElseThrow()
                .getMetadata().getVersion().getFriendlyString();
        if (!version.equals(System.getProperty("fabric.scala.expectedVersion"))) {
            throw new AssertionError("Loaded the wrong language module: " + version);
        }
        Files.writeString(Path.of("smoke-" + environment + ".passed"), version);
        System.out.println("SCALA_SMOKE_" + environment.toUpperCase() + "_READY " + version);
    }
}
