package org.jetbrains.plugins.bsp.scala.sdk

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.externalSystem.service.project.IdeModifiableModelsProvider

interface ScalaSdkExtension {
  fun addScalaSdk(scalaSdk: ScalaSdk, modelsProvider: IdeModifiableModelsProvider)
}

private val ep = ExtensionPointName.create<ScalaSdkExtension>("org.jetbrains.bsp.scalaSdkExtension")

fun scalaSdkExtension(): ScalaSdkExtension? = ep.extensionList.firstOrNull()

fun scalaSdkExtensionExists(): Boolean = ep.extensionList.isNotEmpty()
