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
