## Como exportar um realm que está executando no ocp

Foi extremamente complicado executar o export de dentro do próprio container em execução do keycloak.

Atentar para a versão da imagem que se está usando, deve ser a mesma entre o docker-compose e o openshift.

Acessei o container com `oc rsh rhbk-keycloak-0`
listei as configurações das variáveis de ambiente com `env | grep -i 'kc_'`. Isso me listou as configurações do keycloak.
Usei o arquivo docker-compose.yml com as mesmas variáveis.
Fiz um `oc port-forward postgresql-rhbk-0 5432:5432` para obter a conexão com o postgres usado pelo keycloak
Depois foi apenas executar `podman compose down && podman compose up`