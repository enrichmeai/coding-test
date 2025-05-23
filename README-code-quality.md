# Code Quality Tools

This document describes the code quality tools that have been added to the project.

## Checkstyle

Checkstyle is a development tool to help programmers write Java code that adheres to a coding standard. It automates the process of checking Java code to spare humans of this boring (but important) task.

### Configuration

Checkstyle has been configured in the `build.gradle` file:

```gradle
checkstyle {
    toolVersion = '10.12.5'
    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
    ignoreFailures = false
    maxErrors = 0
    maxWarnings = 100
}

tasks.withType(Checkstyle) {
    reports {
        xml.required = true
        html.required = true
    }
}
```

The Checkstyle rules are defined in `config/checkstyle/checkstyle.xml`.

### Running Checkstyle

To run Checkstyle on the main source code:

```bash
./gradlew checkstyleMain
```

To run Checkstyle on the test source code:

```bash
./gradlew checkstyleTest
```

## PMD

PMD is a source code analyzer. It finds common programming flaws like unused variables, empty catch blocks, unnecessary object creation, and so forth.

### Configuration

PMD has been configured in the `build.gradle` file:

```gradle
pmd {
    toolVersion = '6.55.0'
    ruleSetFiles = files("${rootDir}/config/pmd/ruleset.xml")
    ignoreFailures = true
    ruleSets = []
}

tasks.withType(Pmd) {
    reports {
        xml.required = true
        html.required = true
    }
}
```

The PMD rules are defined in `config/pmd/ruleset.xml`.

### Running PMD

To run PMD on the main source code:

```bash
./gradlew pmdMain
```

To run PMD on the test source code:

```bash
./gradlew pmdTest
```

## Cyclomatic Complexity Validation (CVC)

Cyclomatic Complexity Validation (CVC) checks have been added to the project to ensure that code complexity is kept within reasonable limits. These checks help maintain code quality by identifying overly complex methods and classes that may be difficult to understand, test, and maintain.

### Configuration

The following complexity metrics are checked:

1. **Cyclomatic Complexity**: Measures the number of linearly independent paths through a method or class.
   - Class report level: 80
   - Method report level: 10

2. **NPath Complexity**: Measures the number of possible execution paths through a method.
   - Report level: 200

3. **Cognitive Complexity**: Measures how difficult it is for a human to understand the control flow of a method.
   - Report level: 15

These checks are configured in the PMD ruleset file (`config/pmd/ruleset.xml`).

### Running CVC Checks

CVC checks are run as part of the PMD analysis. To run these checks:

```bash
./gradlew pmdMain
```

## Security Vulnerability Scanning

The project includes two security vulnerability scanning tools to help identify potential security issues:

### OWASP Dependency-Check

OWASP Dependency-Check is a Software Composition Analysis (SCA) tool that detects publicly disclosed vulnerabilities in project dependencies.

#### Configuration

Dependency-Check has been configured in the `build.gradle` file:

```gradle
dependencyCheck {
    failBuildOnCVSS = 7  // Fail the build for CVSS scores >= 7 (high severity)
    formats = ['HTML', 'XML', 'JSON']
    outputDirectory = "${buildDir}/reports/dependency-check"
    suppressionFile = file("${rootDir}/config/dependency-check/suppressions.xml")
    analyzers {
        assemblyEnabled = false
        nodeEnabled = false  // Disable Node.js analysis if not needed
    }
}
```

The suppressions file is located at `config/dependency-check/suppressions.xml`.

#### Running Dependency-Check

To run Dependency-Check:

```bash
./gradlew dependencyCheckAnalyze
```

The report will be generated in `build/reports/dependency-check/`.

### SpotBugs with FindSecBugs

SpotBugs is a static analysis tool that looks for bugs in Java code. FindSecBugs is a SpotBugs plugin specifically designed to find security vulnerabilities.

#### Configuration

SpotBugs has been configured in the `build.gradle` file:

```gradle
spotbugs {
    toolVersion = '4.7.3'
    ignoreFailures = false
    effort = 'max'
    reportLevel = 'medium'
    excludeFilter = file("${rootDir}/config/spotbugs/exclude.xml")
}

spotbugsMain {
    reports {
        create('html') {
            required = true
            outputLocation = file("${buildDir}/reports/spotbugs/main.html")
            stylesheet = 'fancy-hist.xsl'
        }
        create('xml') {
            required = true
            outputLocation = file("${buildDir}/reports/spotbugs/main.xml")
        }
    }
}
```

The exclusion file is located at `config/spotbugs/exclude.xml`.

#### Running SpotBugs

To run SpotBugs:

```bash
./gradlew spotbugsMain
```

The report will be generated in `build/reports/spotbugs/`.

### Running All Security Checks

To run all security checks at once:

```bash
./gradlew securityCheck
```

This will run both Dependency-Check and SpotBugs.

## Notes

- Both Checkstyle and PMD are configured to generate HTML reports that can be viewed in a web browser.
- Checkstyle is configured to fail the build if there are any errors, but to allow up to 100 warnings.
- PMD is configured to not fail the build on violations, which is a common approach when first introducing code quality tools to an existing codebase.
- The rules for both tools can be adjusted in their respective XML configuration files to better suit the project's needs.
- OWASP Dependency-Check is configured to fail the build for vulnerabilities with a CVSS score of 7 or higher (high severity).
- SpotBugs with FindSecBugs is configured to fail the build for medium or higher severity issues.
- Both security tools generate HTML reports that can be viewed in a web browser.
