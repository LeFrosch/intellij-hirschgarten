import com.intellij.platform.backend.workspace.WorkspaceModel
import com.intellij.platform.workspace.jps.entities.LibraryEntity
import com.intellij.platform.workspace.storage.entities
import org.jetbrains.plugins.bsp.magicmetamodel.impl.workspacemodel.impl.updaters.WorkspaceModelEntityUpdaterConfig
import org.jetbrains.plugins.bsp.magicmetamodel.impl.workspacemodel.impl.updaters.WorkspaceModelEntityWithoutParentModuleUpdater
import org.jetbrains.plugins.bsp.workspacemodel.entities.BspProjectEntitySource
import org.jetbrains.plugins.bsp.workspacemodel.entities.CompiledSourceCodeInsideJarExclude
import org.jetbrains.plugins.bsp.workspacemodel.entities.CompiledSourceCodeInsideJarExcludeEntity
import org.jetbrains.plugins.bsp.workspacemodel.entities.CompiledSourceCodeInsideJarExcludeId
import org.jetbrains.plugins.bsp.workspacemodel.entities.LibraryCompiledSourceCodeInsideJarExcludeEntity

class CompiledSourceCodeInsideJarExcludeEntityUpdater(private val workspaceModelEntityUpdaterConfig: WorkspaceModelEntityUpdaterConfig) :
  WorkspaceModelEntityWithoutParentModuleUpdater<CompiledSourceCodeInsideJarExclude, CompiledSourceCodeInsideJarExcludeEntity> {
  override suspend fun addEntity(entityToAdd: CompiledSourceCodeInsideJarExclude): CompiledSourceCodeInsideJarExcludeEntity {
    val currentExcludeEntity =
      WorkspaceModel
        .getInstance(workspaceModelEntityUpdaterConfig.project)
        .currentSnapshot
        .entities<CompiledSourceCodeInsideJarExcludeEntity>()
        .firstOrNull()

    val excludeEntityId =
      if (currentExcludeEntity == null) {
        0
      } else if (currentExcludeEntity.relativePathsInsideJarToExclude == entityToAdd.relativePathsInsideJarToExclude) {
        currentExcludeEntity.excludeId.id
      } else {
        // Change the ID, so that all the referring entities' data (LibraryCompiledSourceCodeInsideJarExcludeEntity) will be changed,
        // and therefore CompiledSourceCodeInsideJarExcludeWorkspaceFileIndexContributor will be rerun on them.
        currentExcludeEntity.excludeId.id + 1
      }

    val excludeEntity =
      workspaceModelEntityUpdaterConfig.workspaceEntityStorageBuilder.addEntity(
        CompiledSourceCodeInsideJarExcludeEntity(
          relativePathsInsideJarToExclude = entityToAdd.relativePathsInsideJarToExclude,
          excludeId = CompiledSourceCodeInsideJarExcludeId(excludeEntityId),
          entitySource = BspProjectEntitySource,
        ),
      )

    val libraries = workspaceModelEntityUpdaterConfig.workspaceEntityStorageBuilder.entities<LibraryEntity>().toList()
    val libraryExcludeEntities =
      libraries.map { library ->
        LibraryCompiledSourceCodeInsideJarExcludeEntity(library.symbolicId, excludeEntity.symbolicId, entitySource = BspProjectEntitySource)
      }
    for (libraryExcludeEntity in libraryExcludeEntities) {
      workspaceModelEntityUpdaterConfig.workspaceEntityStorageBuilder.addEntity(libraryExcludeEntity)
    }
    return excludeEntity
  }
}
