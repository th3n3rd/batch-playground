# Batch Playground

This repository is used as a playground to experiment building and testing Spring Batch and Spring Cloud Task applications.

## Metrics

In order to allow the application to collect and send metrics to NewRelic, create an `application-newrelic.properties` under `src/main/resources`
with the following properties:

```
newrelic.serviceName=${spring.application.name}
newrelic.apiKey=<your-newrelic-api-key>
newrelic.step=30s
newrelic.uri=https://metric-api.eu.newrelic.com/metric/v1
```
