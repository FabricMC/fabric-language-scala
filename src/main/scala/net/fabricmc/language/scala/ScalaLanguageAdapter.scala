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

import net.fabricmc.language.scala.ScalaLanguageAdapter.logger
import net.fabricmc.loader.api.{LanguageAdapter, ModContainer}
import org.slf4j.LoggerFactory
import scala.util.{Failure, Success, Try}
import net.fabricmc.loader.api.LanguageAdapterException

class ScalaLanguageAdapter extends LanguageAdapter {

    override def create[T](
        modContainer: ModContainer,
        value: String,
        typ: Class[T]
    ): T = {
        // Load Object
        val tried = Try {
            val objectClass = Class.forName(value + "$")
            val moduleField = objectClass.getField("MODULE$")
            val instance = moduleField.get(null).asInstanceOf[T]
            Option(instance).getOrElse(throw new NullPointerException)
        }

        tried match
            case Success(instance) => return instance
            case _                 =>

        logger.warn(
          s"Unable to find ${value}.MODULE$$. Maybe it isn't a Scala object?"
        )

        // Try constructing Scala class
        Try(typ.getConstructor().newInstance()) match
            case Success(value)     => value
            case Failure(exception) => throw LanguageAdapterException(exception)
    }
}

object ScalaLanguageAdapter {
    private lazy val logger = LoggerFactory.getLogger("ScalaLanguageAdapter")
}
