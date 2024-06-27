## NÃO USAR JXM, NÃO PERSISTE

## Criando Filas com JMX/REST
```shell
curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://artemis-swim-wconsj-0-svc-rte-nav-portugal.apps.ocp4.masales.cloud/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"amq-broker\"", "operation": "createQueue(java.lang.String,java.lang.String,java.lang.String,java.lang.String,boolean,int,boolean,boolean)", "arguments": ["MASALES.QUEUE","ANYCAST","MASALES.QUEUE","",true,-1,false,true]}'

curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://artemis-swim-wconsj-0-svc-rte-nav-portugal.apps.ocp4.masales.cloud/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"amq-broker\"", "operation": "createQueue(java.lang.String,java.lang.String,java.lang.String,java.lang.String,boolean,int,boolean,boolean)", "arguments": ["MASALES","ANYCAST","MASALES","",true,-1,false,true]}'

curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://artemis-swim-wconsj-0-svc-rte-nav-portugal.apps.ocp4.masales.cloud/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"amq-broker\"", "operation": "createQueue(java.lang.String,java.lang.String,java.lang.String,java.lang.String,boolean,int,boolean,boolean)", "arguments": ["NAV1","ANYCAST","NAV1","",true,-1,false,true]}'

curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://localhost:8161/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"broker\"", "operation": "createQueue(java.lang.String,java.lang.String,java.lang.String,java.lang.String,boolean,int,boolean,boolean)", "arguments": ["MASALES.QUEUE","ANYCAST","MASALES.QUEUE","",true,-1,false,true]}'
```

## Criando Security Settings com JMX/REST
```shell
curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://artemis-swim-wconsj-0-svc-rte-nav-portugal.apps.ocp4.masales.cloud/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"amq-broker\"", "operation": "addSecuritySettings(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String)", "arguments": ["MASALES.QUEUE","admin","consumer,admin","consumer,admin","admin","consumer,admin","admin","admin","admin","admin","admin","admin","admin,consumer"]}'

curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://artemis-swim-wconsj-0-svc-rte-nav-portugal.apps.ocp4.masales.cloud/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"amq-broker\"", "operation": "addSecuritySettings(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String)", "arguments": ["NAV1","champion","consumer,champion","consumer,champion","champion","consumer,champion","champion","champion","champion","champion","champion","champion","champion,consumer"]}'
```

## Listar as permissões para a Fila
```shell
curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://artemis-swim-wconsj-0-svc-rte-nav-portugal.apps.ocp4.masales.cloud/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"amq-broker\"", "operation": "getRolesAsJSON(java.lang.String)", "arguments": ["MASALES.QUEUE"]}'

curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://artemis-swim-wconsj-0-svc-rte-nav-portugal.apps.ocp4.masales.cloud/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"amq-broker\"", "operation": "getRolesAsJSON(java.lang.String)", "arguments": ["NAV1"]}'

curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://artemis-swim-wconsj-0-svc-rte-nav-portugal.apps.ocp4.masales.cloud/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"amq-broker\"", "operation": "getRolesAsJSON(java.lang.String)", "arguments": ["masales-queue"]}'
```

## Remover a diretiva de segurança
```shell
curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://artemis-swim-wconsj-0-svc-rte-nav-portugal.apps.ocp4.masales.cloud/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"amq-broker\"", "operation": "removeSecuritySettings(java.lang.String)", "arguments": ["MASALES.QUEUE"]}'
```

Quando cadastrar o usuário no keycloak criar uma role no cliente e associar o usuário a esta role. Esta role deve ser informada ao cliente na resposta. A criação de security role usa este valor, que deve ser informado no pedido de subscrição. 
Uma alternativa é criar uma custom property e adicionar ao userinfo do usuário, assim, no momento da criação do mesmo esta informação é extraída diretamente do token.