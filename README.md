# API

[![CircleCI](https://circleci.com/gh/backstage-technical-services/quarkus-api.svg?style=shield)](https://circleci.com/gh/backstage-technical-services/quarkus-api)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=backstage-technical-services_quarkus-api&metric=alert_status)](https://sonarcloud.io/dashboard?id=backstage-technical-services_quarkus-api)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=backstage-technical-services_quarkus-api&metric=coverage)](https://sonarcloud.io/dashboard?id=backstage-technical-services_quarkus-api)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=backstage-technical-services_quarkus-api&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=backstage-technical-services_quarkus-api)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=backstage-technical-services_quarkus-api&metric=sqale_index)](https://sonarcloud.io/dashboard?id=backstage-technical-services_quarkus-api)
[![chat](https://img.shields.io/badge/chat-on%20slack-brightgreen)](https://bts-website.slack.com)
[![license](https://img.shields.io/badge/license-Apache%20v2-blue)](./LICENSE.txt)


## Pre-requisites

> Make sure you are familiar with the [Contribution
> Guide][contribution-guide] and the main [development
> readme][development-readme].

* Install GraalVM 19.3.1 (`19.3.1.r11-grl`) using SDKMAN

  > **Do not** set this as your default, as it changes the path for
  > Node.js which will make the SPA very slow to build and run.

* Make sure the [database and SMTP server][aux-services] are running
* Copy the `.env.example` file to `.env` and fill in the missing values

  > You may need to ask in [Slack][slack] for some of the sensitive values

## Configure IntelliJ IDEA

* Make sure the Gradle project is imported, by right-clicking on
  `build.gradle` and selecting `Import Gradle Project`
* Add a run configuration for booting the app using the Gradle task
  `quarkusDev`
* Add the environment file to the `EnvFile` tab and turn `Enable
  experimental integrations` on

  > If you don't see this, you'll need to install the `EnvFile` plugin.

* Add a run configuration for tests using the Gradle task `test`

  > It's often a good idea to ensure that your tests are clean, in which
  > case you can use the `clean test` tasks rather than just `test`.

## Running the API

* To run the API select the correct run configuration and click the
  "Start" button
* To stop the API click the "Stop" button
* If you make changes to the code, restart the API by clicking the "Run"
  button to re-run it (you can also Stop and Start)

## Debugging

* To enable debugging you will need to add another run configuration,
  using the `Remote` configuration, ensuring you set the Debugger mode
  to `Attach to remote JVM` using the port `5005`.
* Once the API is booted, you can start the debugger by selecting this
  run configuration and clicking the "Debug" button
* You can then insert breakpoints at any point in the code and when that
  block runs the execution will pause, allowing you to inspect variables
  and the stack to see what's going on

[contribution-guide]: https://github.com/backstage-technical-services/hub/blob/master/Contributing.md
[development-readme]: https://github.com/backstage-technical-services/website-development/blob/master/readme.md
[aux-services]: https://github.com/backstage-technical-services/website-development/blob/master/readme.md#running-the-auxiliary-services
[slack]: https://bts-website.slack.com
