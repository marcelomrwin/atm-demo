# ATM Client App

## Run Podman Local
```shell
podman run --rm --name atm-camel-quarkus -p 8088:8080 quay.io/masales/atm-client-app:latest
```

## Deploy Openshift
```shell
oc create secret generic atm-client-app-marcelo-secret \
    --from-literal=keycloak.url=https://rhbk-nav-portugal.apps.ocp4.masales.cloud/realms/nav-portugal \
    --from-literal=keycloak.client=atm-client-app \
    --from-literal=subscription.api=http://atm-swim-service-nav-portugal.apps.ocp4.masales.cloud
```

```shell
mvn clean package -DskipTests -Pfrontend -Popenshift
```
