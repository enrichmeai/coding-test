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

## Notes

- Both Checkstyle and PMD are configured to generate HTML reports that can be viewed in a web browser.
- Checkstyle is configured to fail the build if there are any errors, but to allow up to 100 warnings.
- PMD is configured to not fail the build on violations, which is a common approach when first introducing code quality tools to an existing codebase.
- The rules for both tools can be adjusted in their respective XML configuration files to better suit the project's needs.
