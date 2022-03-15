package org.jetbrains.bsp.bazel.bazelrunner;

import io.vavr.control.Option;
import java.io.InputStream;
import java.util.List;
import org.jetbrains.bsp.bazel.bazelrunner.data.BazelProcessResult;
import org.jetbrains.bsp.bazel.bazelrunner.utils.BazelStreamReader;
import org.jetbrains.bsp.bazel.server.loggers.BuildClientLogger;

public class BazelProcess {

  private static final int OK_EXIT_CODE = 0;

  private final Process process;
  private final Option<BuildClientLogger> buildClientLogger;

  BazelProcess(Process process, Option<BuildClientLogger> buildClientLogger) {
    this.process = process;
    this.buildClientLogger = buildClientLogger;
  }

  public BazelProcessResult waitAndGetResult() {
    try {
      int exitCode = process.waitFor();
      BazelProcessResult bazelProcessResult =
          new BazelProcessResult(process.getInputStream(), process.getErrorStream(), exitCode);

      logBazelMessage(bazelProcessResult, exitCode);

      return bazelProcessResult;
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private void logBazelMessage(BazelProcessResult bazelProcessResult, int exitCode) {
    if (exitCode == OK_EXIT_CODE) {
      logBazelMessage(bazelProcessResult);
    } else {
      logBazelError(bazelProcessResult);
    }
  }

  private void logBazelMessage(BazelProcessResult bazelProcessResult) {
    String message = bazelProcessResult.getStderr();
    buildClientLogger.forEach(logger -> logger.logMessage(message));
  }

  private void logBazelError(BazelProcessResult bazelProcessResult) {
    String error = bazelProcessResult.getStderr();
    buildClientLogger.forEach(logger -> logger.logError(error));
  }

  public InputStream getInputStream() {
    return process.getInputStream();
  }

  public List<String> getStderr() {
    return BazelStreamReader.drainStream(process.getErrorStream());
  }
}
