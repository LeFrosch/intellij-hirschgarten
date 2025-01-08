package org.jetbrains.plugins.bsp.magicmetamodel.impl.workspacemodel.impl.updaters

import ch.epfl.scala.bsp4j.BuildTargetIdentifier
import ch.epfl.scala.bsp4j.JvmBuildTarget
import com.intellij.facet.FacetManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.platform.workspace.jps.entities.ModuleEntity
import com.intellij.platform.workspace.jps.entities.ModuleTypeId
import com.intellij.platform.workspace.storage.MutableEntityStorage
import com.intellij.testFramework.runInEdtAndWait
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jetbrains.bsp.protocol.KotlinBuildTarget
import org.jetbrains.kotlin.idea.facet.KotlinFacetType
import org.jetbrains.kotlin.idea.workspaceModel.KotlinSettingsEntity
import org.jetbrains.plugins.bsp.workspacemodel.entities.ContentRoot
import org.jetbrains.plugins.bsp.workspacemodel.entities.GenericModuleInfo
import org.jetbrains.plugins.bsp.workspacemodel.entities.IntermediateModuleDependency
import org.jetbrains.plugins.bsp.workspacemodel.entities.JavaModule
import org.jetbrains.plugins.bsp.workspacemodel.entities.KotlinAddendum
import org.jetbrains.workspace.model.test.framework.WorkspaceModelBaseTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.io.path.name

@DisplayName("kotlinFacetEntityUpdater.addEntity(entityToAdd, parentModuleEntity) tests")
class KotlinFacetEntityUpdaterTest : WorkspaceModelBaseTest() {
  @Test
  fun `should add KotlinFacet when given KotlinAddendum`() {
    runInEdtAndWait {
      // given
      val javaHome = "/fake/path/to/local_jdk"
      val javaVersion = "11"

      val associates = listOf("//target4", "target5")

      val kotlinBuildTarget =
        KotlinBuildTarget(
          languageVersion = "1.8",
          apiVersion = "1.8",
          kotlincOptions = listOf(),
          associates = associates.map { BuildTargetIdentifier(it) },
          jvmBuildTarget =
            JvmBuildTarget().also {
              it.javaHome = javaHome
              it.javaVersion = javaVersion
            },
        )

      val module =
        GenericModuleInfo(
          name = "module1",
          type = ModuleTypeId("JAVA_MODULE"),
          modulesDependencies =
            listOf(
              IntermediateModuleDependency("module2"),
              IntermediateModuleDependency("module3"),
            ),
          librariesDependencies = listOf(),
          associates = associates.map { IntermediateModuleDependency(it) },
        )

      val baseDirContentRoot =
        ContentRoot(
          path = projectBasePath.toAbsolutePath(),
          excludedPaths = listOf(),
        )
      val javaModule =
        JavaModule(
          genericModuleInfo = module,
          baseDirContentRoot = baseDirContentRoot,
          sourceRoots = listOf(),
          resourceRoots = listOf(),
          moduleLevelLibraries = listOf(),
          jvmJdkName = "${projectBasePath.name}-$javaVersion",
          kotlinAddendum =
            KotlinAddendum(
              languageVersion = kotlinBuildTarget.languageVersion,
              apiVersion = kotlinBuildTarget.apiVersion,
              kotlincOptions = kotlinBuildTarget.kotlincOptions,
            ),
        )

      // when
      updateWorkspaceModel {
        val returnedModuleEntity = addEmptyJavaModuleEntity(module.name, it)
        addKotlinFacetEntity(javaModule, returnedModuleEntity, it)
      }

      // then
      val moduleManager = ModuleManager.getInstance(project)
      val retrievedModule = moduleManager.findModuleByName(module.name)
      retrievedModule.shouldNotBeNull()
      val facetManager = FacetManager.getInstance(retrievedModule)
      val facet = facetManager.getFacetByType(KotlinFacetType.TYPE_ID)
      facet.shouldNotBeNull()
      val retrievedFacetSettings = facet.configuration.settings
      retrievedFacetSettings.additionalVisibleModuleNames shouldBe associates
    }
  }

  private fun addKotlinFacetEntity(
    javaModule: JavaModule,
    parentEntity: ModuleEntity,
    builder: MutableEntityStorage,
  ): KotlinSettingsEntity {
    val workspaceModelEntityUpdaterConfig =
      WorkspaceModelEntityUpdaterConfig(
        builder,
        virtualFileUrlManager,
        projectBasePath,
        project,
      )
    val kotlinFacetEntityUpdater = KotlinFacetEntityUpdater(workspaceModelEntityUpdaterConfig, projectBasePath)
    return kotlinFacetEntityUpdater.addEntity(javaModule, parentEntity)
  }
}
