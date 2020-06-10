# API

[![CircleCI](https://circleci.com/gh/backstage-technical-services/quarkus-api.svg?style=shield)](https://circleci.com/gh/backstage-technical-services/quarkus-api)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=backstage-technical-services_quarkus-api&metric=alert_status)](https://sonarcloud.io/dashboard?id=backstage-technical-services_quarkus-api)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=backstage-technical-services_quarkus-api&metric=coverage)](https://sonarcloud.io/dashboard?id=backstage-technical-services_quarkus-api)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=backstage-technical-services_quarkus-api&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=backstage-technical-services_quarkus-api)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=backstage-technical-services_quarkus-api&metric=sqale_index)](https://sonarcloud.io/dashboard?id=backstage-technical-services_quarkus-api)
[![chat](https://img.shields.io/badge/chat-on%20slack-brightgreen)](https://bts-website.slack.com)
[![license](https://img.shields.io/badge/license-Apache%20v2-blue)](./LICENSE.txt)


## Pre-requisites

* GraalVM 19.3.1 (Java 11)

    ```sh
    $ curl -s "https://get.sdkman.io" | bash
    $ source "$HOME/.sdkman/bin/sdkman-init.sh"
    $ sdk install java 19.3.1.r11-grl
    ```
    > For Windows, see the instructions on [SDKMAN's site][sdkman]

* IntelliJ IDEA
    * Make sure the Gradle project is imported, by right-clicking on `build.gradle` and selecting `Import Gradle Project`
    * Add a run configuration for booting the app using the Gradle task `quarkusDev`
    * Add a run configuration for tests using the Gradle task `test`



[sdkman]: https://sdkman.io/install
