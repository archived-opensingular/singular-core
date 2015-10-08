![Versão](https://img.shields.io/badge/version-0.3.2--SNAPSHOT-lightgrey.svg) ![Build](https://img.shields.io/badge/build-success-brightgreen.svg) ![Sonar](https://img.shields.io/badge/sonar-error-red.svg) ![Test](https://img.shields.io/badge/test-69%-red.svg)

# Projeto SINGULAR

Módulos JAVA do projeto SINGULAR.

## Testes

![Result](https://chart.googleapis.com/chart?chs=400x250&chd=t:2,18,44,1&cht=p&chl=failure%20%282%29|error%20%2818%29|success%20%2844%29|skipped%20%281%29&chco=FF0000|DEBDDE|DEF3BD|FFC6A5&chtt=Unit%20Tests)

## Links

* URL's:
    * Mirante Tecnologia
        * [Atlassian](https://mirante.atlassian.net/secure/RapidBoard.jspa?rapidView=86&projectKey=MIR)
        * [HipChat](https://miranteteam.hipchat.com)
    * Equipe de Desenvolvimento
        * [GitBlit](http://git.mirante.net.br/summary/MIRANTE%2Fsingular.git)
        * [TeamCity](http://ci.mirante.net.br/project.html?projectId=Mirante&tab=projectOverview)
        * [Sonar](http://sonar.mirante.net.br/dashboard/index/36298)
        * [Documentação](gh-pages/_includes/index.md)

# Módulo UI-Admin

Módulo de monitoramento (BAM) de processos. Este é um sub-módulo do **Singular Flow**.

## Notas

Este módulo é usado como um _overlay_ **MAVEN** de outros sistemas, mas também pode ser implantada como um _standalone_.

Para iniciar em modo de desenvolvimento defina a variável de sistema **singular.development**. Por exemplo, passe o seguinte argumento para o **Tomcat**:

```bash
-Dsingular.development=true
```

Por padrão, o modo iniciado é o de produção. Nesse modo apenas usuários autenticados com o papel **ADMIN** podem acessar o sistema.
