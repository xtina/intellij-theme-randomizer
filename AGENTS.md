# AGENTS.md

## Cursor Cloud specific instructions

This is a **Kotlin/JVM IntelliJ Platform Plugin** built with Gradle. JDK 21 is required and pre-installed on the VM. No Docker, databases, or external services are needed.

### Key commands

All commands use the Gradle wrapper (`./gradlew`). See `build.gradle.kts` and `gradle.properties` for full configuration.

| Task | Command |
|---|---|
| Build plugin | `./gradlew buildPlugin` |
| Run tests | `./gradlew test` |
| Full check (test + coverage) | `./gradlew check` |
| Verify plugin config | `./gradlew verifyPluginProjectConfiguration` |
| Run IDE sandbox | `./gradlew runIde` (requires display; headless-only in Cloud) |

### Known caveats

- **VFS root access in tests:** If new tests access files from the test data directory, they may need `VfsRootAccess.allowRootAccess(testRootDisposable, testDataPath)` in `setUp()` because the `/workspace` path is not in IntelliJ's default VFS allowed roots. See `MyPluginTest` for the pattern.
- **`JAVA_HOME`:** Must be set to the JDK 21 path (e.g. `/usr/lib/jvm/java-21-openjdk-amd64`). The Gradle wrapper auto-detects it if `java` is on PATH.
- **`--no-daemon`:** Using `--no-daemon` is recommended in Cloud to avoid orphan daemon processes. First builds are slower (~2-3 min) because Gradle downloads the IntelliJ Platform SDK (~800 MB).
- **`runIde`:** Launches a full IntelliJ IDEA sandbox. Requires a display server; not usable in headless Cloud sessions. To verify the plugin works, use `buildPlugin` + `test` instead.
