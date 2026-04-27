<div align="center">
  
# [Multi-Fragmented-ZipFile-Extractor.](https://github.com/BrenoFariasdaSilva/Multi-Fragmented-ZipFile-Extractor) <img src="https://github.com/BrenoFariasdaSilva/Multi-Fragmented-ZipFile-Extractor/blob/6c4572dcdfe2b83948af4298ecd420e555027bbd/.assets/Icons/ZipFIle.svg"  width="3%" height="3%">

</div>

<div align="center">
  
---

Multi-Fragmented-ZipFile-Extractor is a Java command-line tool for merging multiple ZIP files (including split archives) into a single ZIP archive. It uses Zip4j for extraction and merging, creates a temporary workspace for safe processing, and outputs results as JSON for easy integration with automation scripts or other languages.
  
---

</div>

<div align="center">

![Build Status](https://github.com/BrenoFariasdaSilva/Multi-Fragmented-ZipFile-Extractor/actions/workflows/maven-build.yml/badge.svg)
![Java 25](https://img.shields.io/badge/Java-25-blue?logo=openjdk)
![Maven Build](https://img.shields.io/badge/Maven-Build-orange?logo=apachemaven)
![GitHub License](https://img.shields.io/github/license/BrenoFariasdaSilva/Multi-Fragmented-ZipFile-Extractor)
![GitHub Code Size in Bytes](https://img.shields.io/github/languages/code-size/BrenoFariasdaSilva/Multi-Fragmented-ZipFile-Extractor)
![GitHub Language Count](https://img.shields.io/github/languages/count/BrenoFariasdaSilva/Multi-Fragmented-ZipFile-Extractor)
![GitHub Stars](https://img.shields.io/github/stars/BrenoFariasdaSilva/Multi-Fragmented-ZipFile-Extractor)
![GitHub Forks](https://img.shields.io/github/forks/BrenoFariasdaSilva/Multi-Fragmented-ZipFile-Extractor)
![GitHub Contributors](https://img.shields.io/github/contributors/BrenoFariasdaSilva/Multi-Fragmented-ZipFile-Extractor)
![GitHub Commits](https://img.shields.io/github/commit-activity/t/BrenoFariasdaSilva/Multi-Fragmented-ZipFile-Extractor/main)
![GitHub Last Commit](https://img.shields.io/github/last-commit/BrenoFariasdaSilva/Multi-Fragmented-ZipFile-Extractor)
![GitHub Created At](https://img.shields.io/github/created-at/BrenoFariasdaSilva/Multi-Fragmented-ZipFile-Extractor)
![wakatime](https://wakatime.com/badge/github/BrenoFariasdaSilva/Multi-Fragmented-ZipFile-Extractor.svg)

</div>

<div align="center">
  
![RepoBeats Statistics](https://repobeats.axiom.co/api/embed/a61430b22793e7c2efc88d8228fe6bdc6317d44e.svg "Repobeats analytics image")

</div>

## Table of Contents
- [Multi-Fragmented-ZipFile-Extractor. ](#multi-fragmented-zipfile-extractor-)
  - [Table of Contents](#table-of-contents)
  - [Introduction](#introduction)
  - [Requirements](#requirements)
  - [Setup](#setup)
    - [Clone the repository](#clone-the-repository)
  - [Installation](#installation)
    - [Windows (Chocolatey)](#windows-chocolatey)
    - [Windows (Manual Alternative)](#windows-manual-alternative)
    - [Linux (Debian/Ubuntu)](#linux-debianubuntu)
    - [macOS](#macos)
  - [Run Program](#run-program)
    - [Supported log levels](#supported-log-levels)
    - [Examples:](#examples)
    - [Dependencies](#dependencies)
  - [Usage](#usage)
  - [Results](#results)
  - [Contributing](#contributing)
  - [Collaborators](#collaborators)
  - [License](#license)
    - [Apache License 2.0](#apache-license-20)

## Introduction

Multi-Fragmented-ZipFile-Extractor is a command-line Java application that merges multiple ZIP files (including split archives) into a single ZIP file. It uses Zip4j for all ZIP operations, processes files in a temporary workspace, and outputs results as JSON for seamless integration with Python or other automation tools.

## Requirements

- Java Development Kit (JDK) 17 or newer
- Maven (for build and dependency management)
- Internet connection (for Maven to download dependencies on first build)
- Cross-platform: Works on Windows, Linux, and macOS
- No manual installation of Zip4j required (handled by Maven)

## Setup

### Clone the repository

1. Clone the repository with the following command:

    ```bash
    git clone https://github.com/BrenoFariasdaSilva/Multi-Fragmented-ZipFile-Extractor.git
    cd Multi-Fragmented-ZipFile-Extractor
    ```

## Installation

Installation involves setting up Java and Maven, which are required to
build and run the program. Zip4j is handled automatically via Maven
dependencies.

### Windows (Chocolatey)

1.  Install Chocolatey (if not installed) from:
    https://chocolatey.org/install

2.  Install Java JDK and Maven:

  ```powershell
  choco install openjdk17 -y
  choco install maven -y
  ```

3.  Verify installation:

  ```cmd
  java -version
  javac -version
  mvn -v
  ```

4.  Ensure environment variables are refreshed:

  ```powershell
  refreshenv
  ```

### Windows (Manual Alternative)

1.  Download and install:
    -   Java JDK 17+ (or newer)
    -   Maven
2.  Add to PATH:
    -   JAVA_HOME pointing to JDK folder
    -   MAVEN_HOME pointing to Maven folder
    -   Add %JAVA_HOME%`\bin `{=tex}and %MAVEN_HOME%`\bin `{=tex}to PATH
3.  Verify:

  ```cmd
  java -version
  javac -version
  mvn -v
  ```

### Linux (Debian/Ubuntu)

  ```bash
  sudo apt update
  sudo apt install openjdk-17-jdk maven -y
  java -version
  javac -version
  mvn -v
  ```

### macOS

  ```bash
  brew install openjdk@17 maven
  echo 'export PATH="/usr/local/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
  source ~/.zshrc
  java -version
  javac -version
  mvn -v
  ```

Zip4j dependency is managed automatically by Maven during build (mvn
clean package).

## Run Program

To run the Java program after building the JAR with Maven, use the following command:

```bash
java -jar target/zip-extractor-1.0.jar <output.zip> <input1.zip> <input2.zip> ...
```

Optional: Log level control

You can control verbosity using the --log flag:

### Supported log levels

- `ERROR` → Only critical failures
- `WARN` → Errors + warnings (minimal noise)
- `INFO` → Default mode (recommended, balanced output)
- `DEBUG` → Full verbose output (development only)

### Examples:

```bash
java -jar target/zip-extractor-1.0.jar --log=INFO  output.zip input1.zip input2.zip
java -jar target/zip-extractor-1.0.jar --log=WARN  output.zip input1.zip input2.zip
java -jar target/zip-extractor-1.0.jar --log=ERROR output.zip input1.zip input2.zip
java -jar target/zip-extractor-1.0.jar --log=DEBUG output.zip input1.zip input2.zip
```

Parameters
<output.zip> → Path of final merged ZIP
<input.zip> → One or more input ZIP files

Output format

On success:

```json
{"status":"success","output":"<output.zip>"}
```

On failure:

```json
{"status":"error","message":"<error message>"}
```

This output is suitable for integration with Python or other automation tools via subprocess parsing.

### Dependencies

This project uses Maven for dependency management. The only runtime dependency is [zip4j](https://github.com/srikanth-lingala/zip4j), which is automatically handled by Maven.

To build the project and resolve dependencies, run:

```bash
mvn clean package
```

This will produce an executable fat JAR at `target/zip-extractor-1.0.jar`.

Build output note

Only zip-extractor-1.0.jar should be used for execution. Other artifacts in target/ are intermediate Maven outputs and not intended for runtime use.

## Usage

The program merges multiple ZIP files into a single output ZIP. It extracts each input ZIP into a temporary workspace, then adds all extracted contents into the output ZIP using Zip4j. Temporary files are cleaned up automatically.

**Example:**

```bash
java -jar target/zip-extractor-1.0.jar merged.zip part1.zip part2.zip
```

This will create `merged.zip` containing the merged contents of `part1.zip` and `part2.zip`.

**Integration:**

The program is designed for automation and can be called from Python or other languages. It prints a JSON object to stdout indicating success or error, which can be parsed for workflow integration.

## Results

The output ZIP will contain the merged contents of all input ZIP files. The process is atomic and cleans up all temporary files after execution. Any errors (such as missing input files) are reported as JSON to stdout.

## Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**. If you have suggestions for improving the code, your insights will be highly welcome.
In order to contribute to this project, please follow the guidelines below or read the [CONTRIBUTING.md](CONTRIBUTING.md) file for more details on how to contribute to this project, as it contains information about the commit standards and the entire pull request process.
Please follow these guidelines to make your contributions smooth and effective:

1. **Set Up Your Environment**: Ensure you've followed the setup instructions in the [Setup](#setup) section to prepare your development environment.

2. **Make Your Changes**:
   - **Create a Branch**: `git checkout -b feature/YourFeatureName`
   - **Implement Your Changes**: Make sure to test your changes thoroughly.
   - **Commit Your Changes**: Use clear commit messages, for example:
     - For new features: `git commit -m "FEAT: Add some AmazingFeature"`
     - For bug fixes: `git commit -m "FIX: Resolve Issue #123"`
     - For documentation: `git commit -m "DOCS: Update README with new instructions"`
     - For refactorings: `git commit -m "REFACTOR: Enhance component for better aspect"`
     - For snapshots: `git commit -m "SNAPSHOT: Temporary commit to save the current state for later reference"`
   - See more about crafting commit messages in the [CONTRIBUTING.md](CONTRIBUTING.md) file.

3. **Submit Your Contribution**:
   - **Push Your Changes**: `git push origin feature/YourFeatureName`
   - **Open a Pull Request (PR)**: Navigate to the repository on GitHub and open a PR with a detailed description of your changes.

4. **Stay Engaged**: Respond to any feedback from the project maintainers and make necessary adjustments to your PR.

5. **Celebrate**: Once your PR is merged, celebrate your contribution to the project!

## Collaborators

We thank the following people who contributed to this project:

<table>
  <tr>
    <td align="center">
      <a href="#" title="defina o titulo do link">
        <img src="https://github.com/BrenoFariasdaSilva.png" width="100px;" alt="My Profile Picture"/><br>
        <sub>
          <b>Breno Farias da Silva</b>
        </sub>
      </a>
    </td>
  </tr>
</table>

## License

### Apache License 2.0

This project is licensed under the [Apache License 2.0](LICENSE). This license permits use, modification, distribution, and sublicense of the code for both private and commercial purposes, provided that the original copyright notice and a disclaimer of warranty are included in all copies or substantial portions of the software. It also requires a clear attribution back to the original author(s) of the repository. For more details, see the [LICENSE](LICENSE) file in this repository.
