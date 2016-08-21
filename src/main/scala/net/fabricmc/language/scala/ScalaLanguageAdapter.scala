/*
 * Copyright 2016 FabricMC
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

import net.fabricmc.base.loader.Init
import net.fabricmc.base.loader.language.ILanguageAdapter
import org.apache.logging.log4j.LogManager

class ScalaLanguageAdapter extends ILanguageAdapter {

	private val logger = LogManager.getFormatterLogger("ScalaLanguageAdapter")

	override def createModInstance(clazz: Class[_]): AnyRef = {
		try {
			val objectClass = Class.forName(clazz.getName + "$")
			val moduleField = objectClass.getField("MODULE$")
			val instance = moduleField.get(null)
			if (instance == null) throw new NullPointerException
			logger.debug(s"Found ${clazz.getName}$$MODULE$$")
			instance
		} catch {
			case e: Exception => {
				logger.error(s"Unable to find ${clazz.getName}$$MODULE$$")
				clazz.newInstance().asInstanceOf[AnyRef]
			}
		}
	}

	override def callInitializationMethods(instance: scala.Any): Unit = {
		instance.getClass.getDeclaredMethods.foreach(it => {
			if (it.isAnnotationPresent(classOf[Init]) && it.getParameterCount == 0) {
				it.setAccessible(true)
				it.invoke(instance)
			}
		})
	}

}
