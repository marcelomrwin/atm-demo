# atm-camel-quarkus-consumer

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/atm-camel-quarkus-consumer-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- Camel AMQP ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/amqp.html)): Messaging with AMQP protocol using Apache QPid Client
- Camel SEDA ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/seda.html)): Asynchronously call another endpoint from any Camel Context in the same JVM
- Camel Direct ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/direct.html)): Call another endpoint from the same Camel Context synchronously
- Camel Data Format ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/dataformat.html)): Use a Camel Data Format as a regular Camel Component
- Camel ActiveMQ ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/activemq.html)): Send messages to (or consume from) Apache ActiveMQ. This component extends the Camel JMS component
- Camel Micrometer ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/micrometer.html)): Collect various metrics directly from Camel routes using the Micrometer library
- Camel XPath ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/xpath.html)): Evaluates an XPath expression against an XML payload
- Camel HTTP ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/http.html)): Send requests to external HTTP servers using Apache HTTP Client 5.x

```shell
podman build -f src/main/docker/Dockerfile.jvm -t quay.io/masales/atm-camel-quarkus:latest .
podman run --rm --name atm-camel-quarkus --env AMQ_QUEUE=masales-queue --env AMQ_HOST=host.docker.internal --env AMQ_PORT=61616 --env AMQ_USER=artemis --env AMQ_PASSWORD=artemis --env CALL_BACK_URL=http://host.docker.internal:8088/api/callback quay.io/masales/atm-camel-quarkus:latest
```

## Deploy Openshift
```shell
oc create secret generic atm-camel-quarkus-consumer-secret \
    --from-literal=amq.host=artemis-swim-hdls-svc.nav-portugal.svc.cluster.local \
    --from-literal=amq.port=61616 \
    --from-literal=amq.user=marcelo \
    --from-literal=amq.password=password \
    --from-literal=callback.url=http://atm-client-app-nav-portugal.apps.ocp4.masales.cloud/api/callback \
    --from-literal=amq.queue=6e0b5bbb-628e-4eac-a2d4-1fc63beab4b8
```

```shell
./mvnw clean package -DskipTests -Popenshift
```