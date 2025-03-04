package org.jetbrains.bsp.protocol

import ch.epfl.scala.bsp4j.BuildServerCapabilities
import ch.epfl.scala.bsp4j.CompileProvider
import ch.epfl.scala.bsp4j.DebugProvider
import ch.epfl.scala.bsp4j.RunProvider
import ch.epfl.scala.bsp4j.TestProvider

/**
 * Use this instead of [BuildServerCapabilities] to enable Bazel-specific features.
 */
public class BazelBuildServerCapabilities(
  compileProvider: CompileProvider? = null,
  testProvider: TestProvider? = null,
  runProvider: RunProvider? = null,
  debugProvider: DebugProvider? = null,
  inverseSourcesProvider: Boolean = false,
  dependencySourcesProvider: Boolean = false,
  dependencyModulesProvider: Boolean = false,
  resourcesProvider: Boolean = false,
  outputPathsProvider: Boolean = false,
  buildTargetChangedProvider: Boolean = false,
  jvmRunEnvironmentProvider: Boolean = false,
  jvmTestEnvironmentProvider: Boolean = false,
  canReload: Boolean = false,
  jvmCompileClasspathProvider: Boolean = false,
  public val workspaceLibrariesProvider: Boolean = false,
  public val goDebuggerDataProvider: Boolean = false,
  public val workspaceNonModuleTargetsProvider: Boolean = false,
  public val workspaceDirectoriesProvider: Boolean = false,
  public val workspaceInvalidTargetsProvider: Boolean = false,
  public val runWithDebugProvider: Boolean = false,
  public val testWithDebugProvider: Boolean = false,
  public val jvmBinaryJarsProvider: Boolean = false,
  public val bazelRepoMappingProvider: Boolean = false,
) : BuildServerCapabilities() {
  init {
    this.compileProvider = compileProvider
    this.testProvider = testProvider
    this.runProvider = runProvider
    this.debugProvider = debugProvider
    this.inverseSourcesProvider = inverseSourcesProvider
    this.dependencySourcesProvider = dependencySourcesProvider
    this.dependencyModulesProvider = dependencyModulesProvider
    this.resourcesProvider = resourcesProvider
    this.outputPathsProvider = outputPathsProvider
    this.buildTargetChangedProvider = buildTargetChangedProvider
    this.jvmRunEnvironmentProvider = jvmRunEnvironmentProvider
    this.jvmTestEnvironmentProvider = jvmTestEnvironmentProvider
    this.canReload = canReload
    this.jvmCompileClasspathProvider = jvmCompileClasspathProvider
  }
}
