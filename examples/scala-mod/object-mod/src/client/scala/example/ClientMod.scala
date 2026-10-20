package example

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft

object ClientMod extends ClientModInitializer {
  override def onInitializeClient(): Unit = {
    require(Minecraft.getInstance() != null)
    FabricLoader.getInstance().getObjectShare().put("scala-example:client", "ready")
    println("Scala client mod initialized")
  }
}
