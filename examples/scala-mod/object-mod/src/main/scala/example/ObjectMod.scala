package example

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.world.level.block.Blocks

object ObjectMod extends ModInitializer {
  enum Scale(val amount: Int) { case Double extends Scale(2) }
  given Int = Scale.Double.amount
  extension (value: Int) def scaled(using scale: Int): Int = value * scale
  lazy val values = List(1, 2, 3).map(_.scaled)

  override def onInitialize(): Unit = {
    require(values == List(2, 4, 6))
    require(Blocks.DIRT != null)
    FabricLoader.getInstance().getObjectShare().put("scala-example:object", "ready")
    println("Scala object mod initialized")
  }
}
