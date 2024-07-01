
## Example creating Queues using JMX/REST
```shell
curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://artemis-swim-wconsj-0-svc-rte-nav-portugal.apps.ocp4.masales.cloud/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"amq-broker\"", "operation": "createQueue(java.lang.String,java.lang.String,java.lang.String,java.lang.String,boolean,int,boolean,boolean)", "arguments": ["MASALES.QUEUE","ANYCAST","MASALES.QUEUE","",true,-1,false,true]}'

curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://artemis-swim-wconsj-0-svc-rte-nav-portugal.apps.ocp4.masales.cloud/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"amq-broker\"", "operation": "createQueue(java.lang.String,java.lang.String,java.lang.String,java.lang.String,boolean,int,boolean,boolean)", "arguments": ["MASALES","ANYCAST","MASALES","",true,-1,false,true]}'

curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://artemis-swim-wconsj-0-svc-rte-nav-portugal.apps.ocp4.masales.cloud/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"amq-broker\"", "operation": "createQueue(java.lang.String,java.lang.String,java.lang.String,java.lang.String,boolean,int,boolean,boolean)", "arguments": ["NAV1","ANYCAST","NAV1","",true,-1,false,true]}'

curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://localhost:8161/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"broker\"", "operation": "createQueue(java.lang.String,java.lang.String,java.lang.String,java.lang.String,boolean,int,boolean,boolean)", "arguments": ["MASALES.QUEUE","ANYCAST","MASALES.QUEUE","",true,-1,false,true]}'
```

## Example creating Security Settings using JMX/REST
```shell
curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://artemis-swim-wconsj-0-svc-rte-nav-portugal.apps.ocp4.masales.cloud/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"amq-broker\"", "operation": "addSecuritySettings(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String)", "arguments": ["MASALES.QUEUE","admin","consumer,admin","consumer,admin","admin","consumer,admin","admin","admin","admin","admin","admin","admin","admin,consumer"]}'

curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://artemis-swim-wconsj-0-svc-rte-nav-portugal.apps.ocp4.masales.cloud/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"amq-broker\"", "operation": "addSecuritySettings(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String)", "arguments": ["NAV1","champion","consumer,champion","consumer,champion","champion","consumer,champion","champion","champion","champion","champion","champion","champion","champion,consumer"]}'
```

## List all permissions on a queue
```shell
curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://artemis-swim-wconsj-0-svc-rte-nav-portugal.apps.ocp4.masales.cloud/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"amq-broker\"", "operation": "getRolesAsJSON(java.lang.String)", "arguments": ["MASALES.QUEUE"]}'

curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://artemis-swim-wconsj-0-svc-rte-nav-portugal.apps.ocp4.masales.cloud/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"amq-broker\"", "operation": "getRolesAsJSON(java.lang.String)", "arguments": ["NAV1"]}'

curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://artemis-swim-wconsj-0-svc-rte-nav-portugal.apps.ocp4.masales.cloud/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"amq-broker\"", "operation": "getRolesAsJSON(java.lang.String)", "arguments": ["masales-queue"]}'
```

## Remove the security directive
```shell
curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://artemis-swim-wconsj-0-svc-rte-nav-portugal.apps.ocp4.masales.cloud/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"amq-broker\"", "operation": "removeSecuritySettings(java.lang.String)", "arguments": ["MASALES.QUEUE"]}'
```

### Creating keycloak user flow
<p>
When registering the user in keycloak, create a role on the client and associate the user with this role. This role must be informed to the customer in the response. Security role creation uses this value, which must be informed in the subscription request.

A custom property was created and added to the user's userinfo, so at the time of creation this information is extracted directly from the token.
</p>

```shell
oc -n nav-portugal create secret generic swim-db-secret --dry-run=client -o yaml --from-literal=database-user=swimuser --from-literal=database-password=swimpassword --from-literal=database-name=swim
```

### Generate Resources
```shell
mvn clean package oc:resource -DskipTests -Popenshift
```

### Deploy on Openshift
```shell
mvn clean package oc:build oc:resource oc:apply -Popenshift -DskipTests
```

### Undeploy on Openshift
```shell
mvn oc:undeploy -Popenshift
```

### Ping the service
```shell
curl -X GET http://atm-swim-service-nav-portugal.apps.ocp4.masales.cloud/api/subscription/v1/ping

curl -X POST --header "Content-Type: application/json" -k http://atm-swim-service-nav-portugal.apps.ocp4.masales.cloud/api/subscription/v1/user/subscribe -d '{"topicTypes":["ARRIVAL_SEQUENCE_DATA_A"]}'
```