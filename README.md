# azure-release-notes-generator

A release notes generator based on Azure DevOps commits using the [Conventional Commits](https://www.conventionalcommits.org) specification.

The release notes markdown file is generated base on the commits pushed to a determinated branch and period.

> This project was based on [Spring's GitHub Release Notes Generator][github-generator]

## Getting started

To execute, run the following command:

```bash
java -jar azure-release-notes-generator.jar --spring.config.location=file:///tmp/dev/application.yml
```

To generate the markdown file is necessary to configure your application properties like bellow:

```yml
releasenotes:
  title: Release Notes *
  append: false
  file: /tmp/CHANGELOG.md *
  issue-link-base-url: https://dev.azure.com/foo/_workitems/edit *
  azure:
    username: user@domain.com *
    password: **** *
    organization: foo *
    project: bar *
    repository: test *
    branch: master
  releases:
    - title: 1.1.0
      from-date: 2019-07-20T00:00:00
      to-date: 2019-08-02T23:59:59
      branch: develop
    - title: 1.0.0
      from-date: 2019-07-06T00:00:00
      to-date: 2019-07-19T23:59:59
      branch: master
```

> The properties marked with * are mandatory.

### Sections

By default the release notes will contain the following sections:

|Title|Emoji|Label Text|
|---|---|---|
|New Features|:star:|"feat", "test" or "perf"|
|Bug Fixes|:beetle:|"bug" or "fix"|
|Documentation|:notebook_with_decorative_cover:|"doc", "docs", "style" or "chore"|
|Refactorings|:wrench:|"refactor"|

Is possible to customized adding sections in the YAML properties file:

```yml
  sections:
    - title: Bugs
      emoji: ":beetle:"
      labels: bug
```

[github-generator]: https://github.com/spring-io/github-release-notes-generator
