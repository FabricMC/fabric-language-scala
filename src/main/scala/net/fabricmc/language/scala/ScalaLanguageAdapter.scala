/*
 * Copyright 2016, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.language.scala


import net.fabricmc.loader.api.{LanguageAdapter, ModContainer}

class ScalaLanguageAdapter extends LanguageAdapter {

  override def create[T](modContainer: ModContainer, s: String, aClass: Class[T]): T = {
    try {
      val objectClass = Class.forName(s + "$")
      val moduleField = objectClass.getField("MODULE$")
      val instance = moduleField.get(null).asInstanceOf[T]
      if (instance == null) throw new NullPointerException
      instance
    } catch {
      case _: Exception =>
        println(s"Unable to find ${aClass.getName}$$MODULE$$")
        aClass.getConstructor().newInstance()
    }
  }
}
