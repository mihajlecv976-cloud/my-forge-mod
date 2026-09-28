# Build

Target: Minecraft 1.20.1 + Forge 47.4.20 + Java 17.

The sandbox used to prepare this source does not contain Forge/Minecraft artifacts, so a binary cannot be truthfully marked as tested here.

On a Windows machine with network access:

1. Install Java 17.
2. Install Gradle (8.x is suitable for ForgeGradle 6.x).
3. Open this folder in a terminal.
4. Run `gradle build`.
5. The jar is written to `build/libs/`.

A GitHub Actions workflow is included at `.github/workflows/build.yml`; it can build the project remotely when the source is pushed to a repository.
