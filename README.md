# Batch Playground

This repository is used as a playground to experiment building and testing Spring Batch and Spring Cloud Task applications.

# Tests

Most of the tests in `app` are end-to-end (journey tests) and require the `external-system` to be up and running:

```
./mvnw -pl external-system spring-boot:run
```

Note: the external-system application will run with `chaos-monkey` enabled by default to simulate transient failures at runtime.
This is done in order to harness the robustness of the batch application.

Some tests in the application will also require additional processes, in order to run the remote partitioning implementation
we also need to start the following on different terminals:

```
./backing-services/postgresql.sh                                                    # will run on port 5432
./backing-services/rabbitmq.sh                                                      # will run on port 5672,15672
./mvnw -pl app spring-boot:run -Dspring-boot.run.profiles=remote,worker,postgres    # will run on port 8080
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
