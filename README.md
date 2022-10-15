# Batch Playground

This repository is used as a playground to experiment building and testing Spring Batch and Spring Cloud Task applications.

# Tests

Most of the tests in `app` are end-to-end (journey tests) and require the `external-system` to be up and running:

```
./mvnw -pl external-system spring-boot:run
```

And then in another terminal:

```
./mvnw -pl app test -P e2e
```

## Metrics

In order to allow the application to collect and send metrics to NewRelic, create an `application-newrelic.properties` under `src/main/resources`
with the following properties:

```
newrelic.serviceName=${spring.application.name}
newrelic.apiKey=<your-newrelic-api-key>
newrelic.step=30s
newrelic.uri=https://metric-api.eu.newrelic.com/metric/v1
```
