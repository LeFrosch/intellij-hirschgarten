package org.jetbrains.bsp.bazel.install

import java.nio.file.Path

class BazelBspEnvironmentCreator(projectRootDir: Path) : EnvironmentCreator(projectRootDir) {
  override fun create() {
    createDotBazelBsp()
  }
}
