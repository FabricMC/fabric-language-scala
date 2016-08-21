package net.fabricmc.language.scala

import net.fabricmc.base.loader.Init

object TestMod {

	@Init
	def init(): Unit = {
		System.out.println("**********************")
		System.out.println("Hello from Scala!")
		System.out.println("**********************")
	}

}
