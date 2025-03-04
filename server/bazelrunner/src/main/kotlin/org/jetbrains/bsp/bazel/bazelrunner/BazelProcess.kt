package org.jetbrains.bsp.bazel.bazelrunner

import org.apache.logging.log4j.LogManager
import org.eclipse.lsp4j.jsonrpc.CancelChecker
import org.jetbrains.bsp.bazel.bazelrunner.outputs.AsyncOutputProcessor
import org.jetbrains.bsp.bazel.bazelrunner.outputs.OutputProcessor
import org.jetbrains.bsp.bazel.bazelrunner.outputs.SyncOutputProcessor
import org.jetbrains.bsp.bazel.commons.BazelStatus
import org.jetbrains.bsp.bazel.commons.Format
import org.jetbrains.bsp.bazel.commons.Stopwatch
import org.jetbrains.bsp.bazel.logger.BspClientLogger
import java.time.Duration
import java.util.concurrent.CompletableFuture

class BazelProcess internal constructor(
  val process: Process,
  private val logger: BspClientLogger? = null,
  private val serverPidFuture: CompletableFuture<Long>?,
  private val finishCallback: () -> Unit = {},
) {
  fun waitAndGetResult(cancelChecker: CancelChecker, ensureAllOutputRead: Boolean = false): BazelProcessResult {
    return try {
      val stopwatch = Stopwatch.start()
      val outputProcessor: OutputProcessor =
        if (logger != null) {
          if (ensureAllOutputRead) {
            SyncOutputProcessor(process, logger::message, LOGGER::info)
          } else {
            AsyncOutputProcessor(process, logger::message, LOGGER::info)
          }
        } else {
          if (ensureAllOutputRead) {
            SyncOutputProcessor(process, LOGGER::info)
          } else {
            AsyncOutputProcessor(process, LOGGER::info)
          }
        }

      val exitCode = outputProcessor.waitForExit(cancelChecker, serverPidFuture, logger)
      val duration = stopwatch.stop()
      logCompletion(exitCode, duration)
      return BazelProcessResult(outputProcessor.stdoutCollector, outputProcessor.stderrCollector, BazelStatus.fromExitCode(exitCode))
    } finally {
      finishCallback()
    }
  }

  private fun logCompletion(exitCode: Int, duration: Duration) {
    logger?.message("Command completed in %s (exit code %d)", Format.duration(duration), exitCode)
  }

  companion object {
    private val LOGGER = LogManager.getLogger(BazelProcess::class.java)
  }
}
