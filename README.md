# API

[![pipeline status](https://gitlab.com/backstage-technical-services/website/quarkus-api/badges/v1.0-dev/pipeline.svg)](https://gitlab.com/backstage-technical-services/website/quarkus-api/-/commits/master)
[![coverage report](https://gitlab.com/backstage-technical-services/website/quarkus-api/badges/v1.0-dev/coverage.svg)](https://gitlab.com/backstage-technical-services/website/quarkus-api/-/commits/master)
[![chat](https://img.shields.io/badge/chat-on%20slack-brightgreen)](https://bts-website.slack.com)
[![license](https://img.shields.io/badge/license-Apache%20v2-blue)](./LICENSE.md)


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
