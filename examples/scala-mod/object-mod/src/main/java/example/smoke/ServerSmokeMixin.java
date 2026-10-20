package example.smoke;

import java.io.IOException;
import example.SmokeChecks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
abstract class ServerSmokeMixin {
    @Inject(method = "initServer", at = @At("RETURN"))
    private void scalaSmokeStarted(CallbackInfoReturnable<Boolean> result) throws IOException {
        if (Boolean.getBoolean("fabric.scala.smokeTest") && result.getReturnValue()) {
            SmokeChecks.complete("server");
            ((MinecraftServer) (Object) this).halt(false);
        }
    }
}
