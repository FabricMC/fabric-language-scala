package example.smoke;

import java.io.IOException;
import example.SmokeChecks;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
abstract class ClientSmokeMixin {
    @Inject(method = "run", at = @At("HEAD"))
    private void scalaSmokeStarted(CallbackInfo callback) throws IOException {
        if (Boolean.getBoolean("fabric.scala.smokeTest")) {
            SmokeChecks.complete("client");
            System.exit(0);
        }
    }
}
