/*
 * This file is part of fabric-loom, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2023 FabricMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.fabricmc.language.scala

import net.fabricmc.loader.api.{LanguageAdapter, ModContainer}

import scala.util.{Failure, Success, Try}

class ScalaLanguageAdapter extends LanguageAdapter {

  override def create[T](modContainer: ModContainer, s: String, aClass: Class[T]): T = {
    Try {
      val objectClass = Class.forName(s + "$")
      val moduleField = objectClass.getField("MODULE$")
      val instance = moduleField.get(null).asInstanceOf[T]
      Option(instance).getOrElse(throw new NullPointerException)
    } match {
      case Success(instance) => instance
      case Failure(_) =>
        println(s"Unable to find ${aClass.getName}$$MODULE$$")
        aClass.getConstructor().newInstance()
    }
  }
}