# CI/CD Plan for nfr-api

**Repository:** [github.com/mdodds2/nfr-api](https://github.com/mdodds2/nfr-api)
**Stack:** Spring Boot 3, Java 17, Maven (wrapper), H2 for tests, JaCoCo for coverage
**Status:** Draft, October 2026

This document covers the GitHub features available for CI/CD, where nfr-api stands today, the order I plan to adopt them in, and the reasoning behind the main decisions.

---

## 1. GitHub CI/CD capabilities

Each point below was checked against the GitHub documentation page linked next to it.

### GitHub Actions
- **Workflows** are YAML files in `.github/workflows/`. GitHub provides a "Java with Maven" template, and a CI workflow can run the same Maven commands used locally. ([Building and testing Java with Maven](https://docs.github.com/en/actions/tutorials/build-and-test-code/java-with-maven))
- **Triggers:** `push` and `pull_request` can be limited to specific branches with a `branches` filter. By default, `pull_request` runs on the `opened`, `synchronize` and `reopened` activity types, and it tests the merge result of the PR, not just its head branch. Other triggers include `schedule` (cron), `workflow_dispatch` (manual) and `release`. ([Events that trigger workflows](https://docs.github.com/en/actions/reference/workflows-and-actions/events-that-trigger-workflows))
- **Runners:** GitHub-hosted runners come with a tools cache that includes JDKs and Maven. `actions/setup-java` selects the exact distribution and version, for example Temurin 17. ([Java with Maven](https://docs.github.com/en/actions/tutorials/build-and-test-code/java-with-maven))
- **Matrix builds:** `strategy.matrix` creates one job for each combination of variables, such as several JDK versions or operating systems. It also supports `include`, `exclude`, `fail-fast` and `max-parallel`. ([Running variations of jobs](https://docs.github.com/en/actions/how-tos/write-workflows/choose-what-workflows-do/run-job-variations))
- **Caching:** setting `cache: maven` on `setup-java` saves and restores `~/.m2`. The cache key is a hash of `pom.xml`, so any change to the POM invalidates it. ([Java with Maven](https://docs.github.com/en/actions/tutorials/build-and-test-code/java-with-maven))
- **Artifacts:** `actions/upload-artifact` attaches build outputs, such as JARs or reports, to a workflow run so they can be downloaded. ([Java with Maven](https://docs.github.com/en/actions/tutorials/build-and-test-code/java-with-maven))
- **Cost:** standard GitHub-hosted runners are free in public repositories. Private repositories on GitHub Free get 2,000 minutes and 500 MB of artifact storage per month, plus 10 GB of cache per repository. Larger runners are always billed. ([GitHub Actions billing](https://docs.github.com/en/billing/concepts/product-billing/github-actions))

### Branch protection and rulesets
- A branch protection rule can require **status checks** to pass before merging. Checks must finish as successful, skipped or neutral. In "strict" mode the branch must also be up to date with the base branch. Protection rules block force pushes and branch deletion by default. GitHub advises keeping job names unique across workflows so required checks are not ambiguous. ([About protected branches](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches))
- **Rulesets** are named sets of rules. Unlike branch protection rules, several rulesets can apply to the same branch at once, and their rules are combined. A ruleset can be turned on or off without deleting it, and anyone with read access can see it. ([About rulesets](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-rulesets/about-rulesets))

### Dependabot
- **Alerts and security updates:** with the dependency graph and Dependabot alerts turned on, security updates open PRs that raise a vulnerable dependency to the lowest patched version. This also covers vulnerable GitHub Actions. ([Dependabot security updates](https://docs.github.com/en/code-security/dependabot/dependabot-security-updates/about-dependabot-security-updates))
- **Version updates** are configured in `.github/dependabot.yml` (`version: 2`), with one `updates` entry per `package-ecosystem` (`maven` and `github-actions` here). Each entry needs a `directory` and a `schedule.interval`. By default Dependabot keeps up to 5 version-update PRs open at a time, and `groups` can merge related updates into one PR. ([Dependabot options reference](https://docs.github.com/en/code-security/dependabot/working-with-dependabot/dependabot-options-reference))

### Code scanning with CodeQL
- CodeQL can be turned on with **default setup**, where GitHub picks the languages, queries and triggers. **Advanced setup** instead commits a workflow that you can customize. Java is supported under the `java-kotlin` language identifier, and results appear as code scanning alerts. ([About code scanning with CodeQL](https://docs.github.com/en/code-security/code-scanning/introduction-to-code-scanning/about-code-scanning-with-codeql))

### Secret scanning
- Secret scanning checks the full Git history on every branch, plus issues, PRs, discussions and wikis, for leaked credentials. It runs automatically and for free on **public repositories**. ([About secret scanning](https://docs.github.com/en/code-security/secret-scanning/introduction/about-secret-scanning))

### GitHub Packages and GHCR
- The Container registry (`ghcr.io`) stores Docker and OCI images. A workflow can publish images with the built-in `GITHUB_TOKEN`, and publishing from a workflow links the package to its repository automatically. Newly published packages are private by default. Adding the `org.opencontainers.image.source` label also links the image to the repository. ([Working with the Container registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry))

### Environments and secrets
- **Environments** (for example `staging` or `production`) can require approval, limit which branches can deploy, and restrict access to their secrets. A job can read an environment's secrets only after its protection rules pass. ([Deployment environments](https://docs.github.com/en/actions/concepts/workflows-and-actions/deployment-environments))
- **Secrets** are encrypted before they reach GitHub, are only available to workflows that reference them explicitly, and are redacted from logs. Environment secrets can also require reviewers. ([Secrets](https://docs.github.com/en/actions/concepts/security/secrets))

### Status badges
- Each workflow has a badge at `https://github.com/OWNER/REPO/actions/workflows/FILE/badge.svg`. It can be filtered with `?branch=` and `?event=`, and badges from private repositories cannot be embedded on external sites. ([Adding a workflow status badge](https://docs.github.com/en/actions/how-tos/monitor-workflows/add-a-status-badge))

---

## 2. Current state of nfr-api

| Area | Today |
|---|---|
| Automation | None. The repository has no `.github/` directory. |
| Build and test | Run by hand with `./mvnw verify` |
| Branching | Work happens on `develop` and is merged into `main` by hand |
| Tests | 160 tests (JUnit), running against H2 in-memory |
| Coverage | About 99.7% line and 95.7% branch coverage (JaCoCo) |
| Coverage gate | JaCoCo `check` rule: BUNDLE **LINE** covered ratio **≥ 0.80** (`jacoco-maven-plugin` 0.8.13) |
| Java | `java.version` 17 |

The quality bar is already high, but nothing enforces it automatically. A merge to `main` relies on remembering to run the build first.

---

## 3. Roadmap (in order)

**(a) CI workflow.** Run `./mvnw -B verify` on every push and pull request to `main` and `develop`, using Temurin 17 with Maven caching. Because the JaCoCo `check` goal is bound in the POM, the coverage gate runs as part of the build.

```yaml
# .github/workflows/ci.yml
name: CI
on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]

permissions:
  contents: read

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v6
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
          cache: maven
      - name: Build, test, coverage check
        run: ./mvnw -B verify
```

**(b) Required status check on `main`.** Add a branch protection rule or ruleset for `main` that requires the `build` job to pass and requires changes to arrive through a pull request. The manual develop-to-main merge then becomes a PR that is gated by CI.

**(c) Dependabot for Maven and GitHub Actions.** Turn on alerts and security updates, then add version updates:

```yaml
# .github/dependabot.yml
version: 2
updates:
  - package-ecosystem: "maven"
    directory: "/"
    schedule:
      interval: "weekly"
    target-branch: "develop"
  - package-ecosystem: "github-actions"
    directory: "/"
    schedule:
      interval: "weekly"
    target-branch: "develop"
```

Version-update PRs target `develop` so they follow the normal flow. Security updates always target the default branch (`main`).

**(d) CodeQL for Java.** Start with default setup. Move to advanced setup only if the build needs customizing.

**(e) JaCoCo report artifact and a badge.** Upload `target/site/jacoco/` with `actions/upload-artifact` after `verify`, and add the CI status badge to `README.md`:

```markdown
![CI](https://github.com/mdodds2/nfr-api/actions/workflows/ci.yml/badge.svg?branch=main)
```

**(f) Later: Docker image published to GHCR.** Add a `Dockerfile` with the `org.opencontainers.image.source` label, and a workflow on `main` (or on release tags) that builds the image and pushes it to `ghcr.io/mdodds2/nfr-api` using `GITHUB_TOKEN`.

**(g) Deployment: deferred** until a hosting target is chosen. When it is, use a `production` environment with required reviewers and environment-scoped secrets.

---

## 4. Decisions and reasoning

**H2 now, Testcontainers later.** H2 keeps the suite fast and self-contained, and CI needs no Docker or external database. The trade-off is that H2 does not behave exactly like a production database. Once the production database is fixed, I plan to add a Testcontainers-based integration test profile. It would slot into the existing CI workflow from (a).

**JaCoCo minimum of 80% line coverage.** The gate is set well below current coverage (about 99.7% line). It acts as a floor that catches real regressions, such as a large untested feature, without failing builds over small fluctuations. Actual coverage is shown through the report artifact. A branch-coverage limit (current coverage is about 95.7%) could be added later if branch coverage starts to slip.
- Update:  90% branch coverage was added.

**CD deferred.** Continuous deployment has no value without a target, and choosing one involves cost and operational decisions that shouldn't be rushed. A published GHCR image (f) keeps the project ready to deploy to most container hosts once one is chosen.

**develop/main branching.** `develop` is the integration branch and `main` is always releasable. CI runs on both, but only `main` is protected by a required check. This keeps day-to-day work on `develop` low-friction while making every change to `main` a reviewed PR that has already passed CI.
