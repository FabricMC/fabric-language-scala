package example

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader

class ClassMod extends ModInitializer {
  override def onInitialize(): Unit = {
    require(Vector("Scala", "class").mkString(" ") == "Scala class")
    FabricLoader.getInstance().getObjectShare().put("scala-example:class", "ready")
    println("Scala class mod initialized")
  }
}

object ClassMod {
  throw new AssertionError("An unrelated companion must never be initialized")
}
