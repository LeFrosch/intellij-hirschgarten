load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "sync",
    srcs = glob([
        "*.java",
        "*.kt",
    ]),
    visibility = ["//visibility:public"],
    deps = [
        "//protocol",
        "//server/bazelrunner",
        "//server/bazelrunner/src/main/kotlin/org/jetbrains/bsp/bazel/bazelrunner/utils",
        "//server/commons",
        "//server/executioncontext/workspacecontext",
        "//server/logger",
        "//server/server/src/main/kotlin/org/jetbrains/bsp/bazel/server/benchmark",
        "//server/server/src/main/kotlin/org/jetbrains/bsp/bazel/server/bep",
        "//server/server/src/main/kotlin/org/jetbrains/bsp/bazel/server/bsp/info",
        "//server/server/src/main/kotlin/org/jetbrains/bsp/bazel/server/bsp/managers",
        "//server/server/src/main/kotlin/org/jetbrains/bsp/bazel/server/bsp/utils",
        "//server/server/src/main/kotlin/org/jetbrains/bsp/bazel/server/dependencygraph",
        "//server/server/src/main/kotlin/org/jetbrains/bsp/bazel/server/label",
        "//server/server/src/main/kotlin/org/jetbrains/bsp/bazel/server/model",
        "//server/server/src/main/kotlin/org/jetbrains/bsp/bazel/server/paths",
        "//server/server/src/main/kotlin/org/jetbrains/bsp/bazel/server/sync/firstPhase",
        "//server/server/src/main/kotlin/org/jetbrains/bsp/bazel/server/sync/languages",
        "//server/server/src/main/kotlin/org/jetbrains/bsp/bazel/server/sync/proto:bsp_target_info_java_proto",
        "//server/server/src/main/kotlin/org/jetbrains/bsp/bazel/server/sync/sharding",
        "//third_party/bazel/src/main/java/com/google/devtools/build/lib/buildeventstream/proto:build_event_stream_java_proto",
        "//third_party/bazel/src/main/protobuf:deps_java_proto",
        "@maven//:ch_epfl_scala_bsp4j",
        "@maven//:io_grpc_grpc_stub",
        "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j_jsonrpc",
        "@maven//:org_jetbrains_kotlinx_kotlinx_coroutines_core",
        "@rules_intellij//intellij_platform_sdk:plugin_api",
    ],
)
