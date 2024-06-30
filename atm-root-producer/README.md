#

## Local

### Compile and Package
```shell
mvn clean package -DskipTests
```

### Build Local Image
```shell
podman build -t quay.io/masales/atm-root-producer:latest .
```

### Execute Podman local
```shell
podman run --rm --name atm-root-producer -e JMS_HOST=host.docker.internal -e APP_JOB_INTERVAL=10000 -e JMS_USER=artemis -e JMS_PASSWORD=artemis  quay.io/masales/atm-root-producer
```

## Openshift

### Login on Openshift
```shell
oc login ...
```

### Creating Configmap
```shell
oc create configmap atm-root-producer-config \
    --from-literal=jms.host=artemis-ss-ss-0.artemis-ss-hdls-svc.nav-portugal.svc.cluster.local \
    --from-literal=job.interval=60000
```

### Creating Secret
```shell
oc create secret generic atm-root-producer-secret \
    --from-literal=jms.user=artemis \
    --from-literal=jms.password=artemis
```

### Generate Resources
```shell
mvn clean package oc:resource -DskipTests -Popenshift
```

### Undeploy on Openshift
```shell
mvn oc:undeploy -Popenshift
```

### Deploy on Openshift
```shell
mvn clean package oc:build oc:resource oc:apply -Popenshift -DskipTests
```

### Test Artemis
```shell



./artemis producer --destination helloworld --message-count 10 --url tcp://artemis-ss-0.artemis-hdls-svc.nav-portugal.svc.cluster.local:61616 --user artemis --password artemis

./artemis producer --destination helloworld --message-count 10 --url amqp://artemis-ss-0.artemis-hdls-svc.nav-portugal.svc.cluster.local:5672 --user artemis --password artemis

./artemis producer --destination helloworld --message-count 10 --url tcp://artemis-ss-hdls-svc.nav-portugal.svc.cluster.local:61616 --user artemis --password artemis

./artemis producer --destination helloworld --message-count 10 --url tcp://artemis-ss-ss-0.artemis-ss-hdls-svc.nav-portugal.svc.cluster.local:61616 --user artemis --password artemis


```