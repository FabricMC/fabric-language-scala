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

import net.fabricmc.loader.api.{LanguageAdapter, LanguageAdapterException, ModContainer}

class ScalaLanguageAdapter extends LanguageAdapter {

  override def create[T](mod: ModContainer, value: String, typ: Class[T]): T = {
    if (value == null || value.isBlank || value.contains("::")) {
      throw new LanguageAdapterException(s"Expected a Scala object or class name, got '$value'")
    }

    try {
      // Inspect companions without running their initializers unless they are the entrypoint.
      val moduleClass = try {
        Class.forName(value + "$", false, getClass.getClassLoader)
      } catch {
        case _: ClassNotFoundException => null
      }

      if (moduleClass != null && typ.isAssignableFrom(moduleClass)) {
        val instance = moduleClass.getField("MODULE$").get(null)
        if (instance == null) {
          throw new LanguageAdapterException(s"Scala object '$value' has a null MODULE$$ instance")
        }
        typ.cast(instance)
      } else {
        LanguageAdapter.getDefault().create(mod, value, typ)
      }
    } catch {
      case e: ReflectiveOperationException =>
        throw new LanguageAdapterException(s"Cannot load Scala entrypoint '$value' as ${typ.getName}", e)
      case e: LinkageError =>
        throw new LanguageAdapterException(s"Cannot initialize Scala entrypoint '$value'", e)
      case e: SecurityException =>
        throw new LanguageAdapterException(s"Cannot access Scala entrypoint '$value'", e)
    }
  }
}
