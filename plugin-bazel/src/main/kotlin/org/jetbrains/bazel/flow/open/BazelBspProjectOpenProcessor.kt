package org.jetbrains.bazel.flow.open

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.isFile
import org.jetbrains.bazel.assets.BazelPluginIcons
import org.jetbrains.bazel.config.BazelPluginConstants
import org.jetbrains.bazel.config.BazelPluginConstants.bazelBspBuildToolId
import org.jetbrains.bazel.settings.bazelProjectSettings
import org.jetbrains.plugins.bsp.config.BuildToolId
import org.jetbrains.plugins.bsp.impl.flow.open.BaseBspProjectOpenProcessor
import org.jetbrains.plugins.bsp.impl.flow.open.BspProjectOpenProcessor
import org.jetbrains.plugins.bsp.impl.flow.open.BspProjectOpenProcessorExtension
import org.jetbrains.plugins.bsp.impl.flow.open.toBuildToolId
import javax.swing.Icon

internal class BazelBspProjectOpenProcessor : BaseBspProjectOpenProcessor(bazelBspBuildToolId) {
  override fun calculateProjectFolderToOpen(virtualFile: VirtualFile): VirtualFile =
    when {
      virtualFile.isBazelBspConnectionFile() -> BspProjectOpenProcessor().calculateProjectFolderToOpen(virtualFile)
      else -> findProjectFolderFromEligibleFile(virtualFile)
    } ?: error("Cannot find the suitable Bazel project folder to open for the given file $virtualFile.")

  override val icon: Icon = BazelPluginIcons.bazel

  override val name: String = BazelPluginConstants.BAZEL_DISPLAY_NAME

  override val isStrongProjectInfoHolder: Boolean
    get() = ApplicationManager.getApplication().isHeadlessEnvironment

  override fun canOpenProject(file: VirtualFile): Boolean =
    when {
      file.isEligibleFile() -> true
      else -> file.children?.any { it.isEligibleFile() } == true
    }

  private fun VirtualFile.isEligibleFile() = isBazelBspConnectionFile() || isWorkspaceFile() || isBuildFile() || isProjectViewFile()

  private fun VirtualFile.isBazelBspConnectionFile() = toBuildToolId() == bazelBspBuildToolId

  override fun calculateBeforeOpenCallback(originalVFile: VirtualFile): (Project) -> Unit =
    when {
      originalVFile.isBazelBspConnectionFile() -> BspProjectOpenProcessor().calculateBeforeOpenCallback(originalVFile)
      originalVFile.isProjectViewFile() -> projectViewFileBeforeOpenCallback(originalVFile)
      originalVFile.isBuildFile() -> buildFileBeforeOpenCallback(originalVFile)
      else -> {
        val buildFile = calculateBuildFileFromDirectory(originalVFile)
        buildFile?.let { buildFileBeforeOpenCallback(it) } ?: {}
      }
    }

  private fun calculateBuildFileFromDirectory(originalVFile: VirtualFile): VirtualFile? =
    originalVFile.children?.firstOrNull { it.isBuildFile() }

  private fun buildFileBeforeOpenCallback(originalVFile: VirtualFile): (Project) -> Unit =
    fun(project) {
      val bazelPackageDir = originalVFile.parent ?: return
      val outputProjectViewFilePath = ProjectViewFileUtils.calculateProjectViewFilePath(project, true, bazelPackageDir.toNioPath())
      project.bazelProjectSettings =
        project.bazelProjectSettings.withNewProjectViewPath(outputProjectViewFilePath.toAbsolutePath())
    }

  private fun projectViewFileBeforeOpenCallback(originalVFile: VirtualFile): (Project) -> Unit =
    { project ->
      project.bazelProjectSettings =
        project.bazelProjectSettings.withNewProjectViewPath(originalVFile.toNioPath().toAbsolutePath())
    }

  private fun VirtualFile.isProjectViewFile() = extension == BazelPluginConstants.PROJECT_VIEW_FILE_EXTENSION
}

internal class BazelBspProjectOpenProcessorExtension : BspProjectOpenProcessorExtension {
  override val buildToolId: BuildToolId = bazelBspBuildToolId

  override val shouldBspProjectOpenProcessorBeAvailable: Boolean = false
}

tailrec fun findProjectFolderFromEligibleFile(vFile: VirtualFile?): VirtualFile? =
  when {
    vFile == null -> null
    vFile.containsWorkspaceFile() -> vFile
    else -> findProjectFolderFromEligibleFile(vFile.parent)
  }

private fun VirtualFile.containsWorkspaceFile() = isDirectory && children?.any { it.isWorkspaceFile() } == true

private fun VirtualFile.isWorkspaceFile() = isFile && name in BazelPluginConstants.WORKSPACE_FILE_NAMES

private fun VirtualFile.isBuildFile() = isFile && name in BazelPluginConstants.BUILD_FILE_NAMES
