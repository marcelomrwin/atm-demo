```shell
export AMQ_USER=my-client-user
export AMQ_PASSWORD=my-client-pwd
./amq-broker/bin/artemis queue stat --user $AMQ_USER --password $AMQ_PASSWORD
```
user: t8tnElvC
pass: SGNOy1l2

cat /amq/extra/secrets/security-roles-bp/securityRoles.properties
cat /amq/extra/secrets/artemis-jaas-config/roles.properties


```shell
export AMQ_USER=admin
export AMQ_PASSWORD=password
./amq-broker/bin/artemis queue stat --user $AMQ_USER --password $AMQ_PASSWORD
```
cat /amq/extra/secrets/security-swim-roles-bp/securityRoles.properties
cat /amq/extra/secrets/sso-jaas-config/login.config
cat /amq/extra/secrets/sso-jaas-config/_keycloak-login-module.json