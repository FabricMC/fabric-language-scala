/*
 * Copyright 2026 FabricMC
 * Licensed under the Apache License, Version 2.0.
 */
package net.fabricmc.language.scala.test

import net.fabricmc.api.ModInitializer
import net.fabricmc.language.scala.ScalaLanguageAdapter
import net.fabricmc.loader.api.{FabricLoader, LanguageAdapterException}
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ScalaLanguageAdapterTest {
  private val adapter = new ScalaLanguageAdapter
  private val mod = FabricLoader.getInstance().getModContainer("fabric-language-scala").orElseThrow()
  private val prefix = "net.fabricmc.language.scala.test."

  private def load(name: String): ModInitializer =
    adapter.create(mod, prefix + name, classOf[ModInitializer])

  private def failure(name: String): LanguageAdapterException =
    assertThrows(classOf[LanguageAdapterException], () => load(name))

  private def rootCause(error: Throwable): Throwable =
    if (error.getCause == null) error else rootCause(error.getCause)

  @Test def objectIsInitializedAndRetainsIdentity(): Unit = {
    val entrypoint = load("ObjectEntrypoint")
    assertSame(ObjectEntrypoint, entrypoint)
    assertSame(entrypoint, load("ObjectEntrypoint"))
    entrypoint.onInitialize()
    assertEquals(List(2, 4, 6), ObjectEntrypoint.values)
  }

  @Test def constructsTheNamedClass(): Unit = {
    assertInstanceOf(classOf[ClassEntrypoint], load("ClassEntrypoint"))
    assertNotSame(load("ClassEntrypoint"), load("ClassEntrypoint"))
  }

  @Test def unrelatedCompanionIsNotInitialized(): Unit = {
    assertInstanceOf(classOf[UnrelatedCompanion], load("UnrelatedCompanion"))
  }

  @Test def eligibleObjectTakesPrecedenceOverClass(): Unit = {
    assertSame(BothEntrypoints, load("BothEntrypoints"))
  }

  @Test def missingClassPreservesItsNameAndCause(): Unit = {
    val error = failure("MissingEntrypoint")
    assertInstanceOf(classOf[ClassNotFoundException], rootCause(error))
    assertTrue(rootCause(error).getMessage.contains("MissingEntrypoint"))
  }

  @Test def rejectsWrongInterface(): Unit = {
    val error = failure("WrongInterface")
    assertTrue(error.getMessage.contains("cannot be cast"))
    assertTrue(error.getMessage.contains(classOf[ModInitializer].getName))
  }

  @Test def missingConstructorPreservesCause(): Unit = {
    assertInstanceOf(classOf[NoSuchMethodException], rootCause(failure("NeedsArgument")))
  }

  @Test def inaccessibleConstructorPreservesCause(): Unit = {
    assertInstanceOf(classOf[IllegalAccessException], rootCause(failure("PrivateConstructor")))
  }

  @Test def constructorFailureIsNotReplaced(): Unit = {
    val cause = rootCause(failure("ThrowingConstructor"))
    assertInstanceOf(classOf[IllegalStateException], cause)
    assertEquals("constructor failed", cause.getMessage)
  }

  @Test def objectFailureDoesNotFallBackToClass(): Unit = {
    val error = failure("ThrowingObject")
    assertTrue(error.getMessage.contains("ThrowingObject"))
    assertEquals("object failed", rootCause(error).getMessage)
  }

  @Test def rejectsMalformedHandles(): Unit = {
    for (value <- Seq(null, "", " ", prefix + "ObjectEntrypoint::onInitialize", "a::b::c")) {
      val error = assertThrows(classOf[LanguageAdapterException],
        () => adapter.create(mod, value, classOf[ModInitializer]))
      assertTrue(error.getMessage.contains("object or class name"))
    }
  }
}

object ObjectEntrypoint extends ModInitializer {
  enum Multiplier(val amount: Int) { case Twice extends Multiplier(2) }
  given Int = Multiplier.Twice.amount
  extension (value: Int) def scaled(using multiplier: Int): Int = value * multiplier
  lazy val values: List[Int] = List(1, 2, 3).map(_.scaled)
  override def onInitialize(): Unit = { values; () }
}

class ClassEntrypoint extends ModInitializer {
  override def onInitialize(): Unit = ()
}

class UnrelatedCompanion extends ModInitializer {
  override def onInitialize(): Unit = ()
}
object UnrelatedCompanion {
  throw new IllegalStateException("an unrelated companion must not initialize")
}

class BothEntrypoints extends ModInitializer {
  override def onInitialize(): Unit = throw new AssertionError("class must not be selected")
}
object BothEntrypoints extends ModInitializer {
  override def onInitialize(): Unit = ()
}

object WrongInterface
class NeedsArgument(value: Int) extends ModInitializer {
  override def onInitialize(): Unit = ()
}
class PrivateConstructor private () extends ModInitializer {
  override def onInitialize(): Unit = ()
}
class ThrowingConstructor extends ModInitializer {
  throw new IllegalStateException("constructor failed")
  override def onInitialize(): Unit = ()
}
class ThrowingObject extends ModInitializer {
  throw new AssertionError("must not fall back after an object failure")
  override def onInitialize(): Unit = ()
}
object ThrowingObject extends ModInitializer {
  throw new IllegalStateException("object failed")
  override def onInitialize(): Unit = ()
}
