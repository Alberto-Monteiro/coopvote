## 📋 **Visão Geral do Sistema**

**CoopVote** é um sistema desenvolvido em **Java 23** com **Spring Boot 3.4.1**, projetado para gerenciar sessões de
votação em cooperativas. Ele permite:

1. Criar pautas de votação.
2. Abrir sessões de votação vinculadas a pautas.
3. Registrar votos durante uma sessão ativa.
4. Consultar resultados de votações.

### 🛠️ **Principais Tecnologias Utilizadas**

- **Java 23**: Linguagem de programação.
- **Spring Boot 3.4.1**: Framework para construção de APIs RESTful.
- **Spring Data JPA**: Gerenciamento de persistência de dados.
- **Lombok**: Redução de código boilerplate.
- **MySQL 5.7**: Banco de dados relacional para persistência.
- **Docker e Docker Compose**: Para containerização e execução em ambientes consistentes.
- **Maven**: Gerenciador de dependências e build.

---

## 🧪 **Testes do Sistema**

O sistema **CoopVote** foi projetado para garantir alta qualidade de código e confiabilidade por meio de testes
automatizados. A cobertura de código alcança **100%**, incluindo testes unitários e de integração.

---

### 📌 **Testes Unitários**

Os testes unitários foram criados para validar funcionalidades individuais e componentes isolados do sistema.

- **Objetivo**:
    - Garantir que métodos de classes, como `Service` e `Repository`, funcionem conforme o esperado.
    - Verificar cenários de sucesso e falha para regras de negócio.

- **Tecnologias Utilizadas**:
    - **JUnit 5**: Framework para testes unitários em Java.
    - **Mockito**: Ferramenta para criação de mocks e simulação de comportamentos.

- **Principais Áreas Testadas**:
    - **Services**:
        - Validação de regras de abertura de sessões.
        - Processamento de votos e restrições (ex.: não permitir votos duplicados).
        - Tratamento de exceções, como `SessaoExpiradaException` e `NotFoundException`.
    - **Repositories**:
        - Testes de integração para verificar consultas personalizadas.

---

### 🌐 **Testes de Integração**

Os testes de integração foram desenvolvidos para validar o comportamento completo do sistema, incluindo interações entre
diferentes camadas (Controller, Service e Repository).

- **Objetivo**:
    - Garantir que os endpoints da API funcionem de ponta a ponta.
    - Validar cenários reais, como a criação de pautas, abertura de sessões e registro de votos.

- **Tecnologias Utilizadas**:
    - **Spring Boot Test**: Framework que facilita a configuração e execução de testes em ambientes Spring.
    - **H2 Database**: Banco de dados em memória para simular o ambiente real sem afetar o banco de dados de produção.

- **Principais Endpoints Testados**:
    - **`POST /api/v1/pautas`**: Verificação da criação de pautas e persistência no banco.
    - **`POST /api/v1/pautas/{id}/abrir-sessao`**: Validação de abertura de sessões com tempo configurável.
    - **`POST /api/v1/votos`**: Testes para registrar votos com cenários de sucesso e falha.

---

### 📈 **Cobertura de Código**

- A cobertura de código é de **100%**, abrangendo:
    - Todas as classes do sistema.
    - Testes de cenários de exceção, regras de negócio e comportamento normal.
- Relatórios de cobertura foram gerados utilizando:
    - **Jacoco**: Ferramenta que mede e gera relatórios de cobertura de código.

---

### ⚙️ **Execução dos Testes**

Para executar os testes automatizados, utilize o Maven:

```bash
mvn test
```

- Após a execução, os resultados serão exibidos no terminal.
- Relatórios detalhados podem ser gerados com o Jacoco configurado no projeto.

---

## 🚀 **Como Executar o Sistema**

Você pode rodar o sistema de duas formas: utilizando **Docker** ou **Maven**.

---

### 🐳 **Execução com Docker**

1. **Pré-requisitos**
    - **Docker** instalado em sua máquina.
    - **Docker Compose** (geralmente já incluído no Docker Desktop).

2. **Passos para Executar**
    - Clone o repositório:
      ```bash
      git clone git@github.com:Alberto-Monteiro/coopvote.git
      cd coopvote/src/docker

      ```

    - Execute o comando abaixo para iniciar o ambiente:
      ```bash
      docker-compose up --build
      ```

    - Após a execução:
        - A API estará disponível em: [http://localhost:8080](http://localhost:8080)
        - O banco MySQL estará configurado na porta `3306`.

3. **Parar o Ambiente**
   Para encerrar os serviços:
   ```bash
   docker-compose down
   ```

4. **Monitorar Logs**
   Você pode acompanhar a execução em tempo real com:
   ```bash
   docker-compose logs -f
   ```

---

### ☕ **Execução com Maven**

1. **Pré-requisitos**
    - **Java 23** instalado.
    - **Maven** instalado e configurado ou pode usar o wrapper do Maven que esta na pasta raiz do projeto.
    - Um banco de dados **MySQL** configurado localmente.

2. **Configuração do Banco de Dados**
    - Crie um banco de dados chamado `coopvote`.
    - Configure as credenciais no arquivo `application.properties`:
      ```properties
      spring.datasource.url=jdbc:mysql://localhost:3306/coopvote
      spring.datasource.username=root
      spring.datasource.password=<SUA_SENHA>
      spring.jpa.hibernate.ddl-auto=update
      ```

3. **Passos para Executar**
    - Clone o repositório:
      ```bash
      git clone git@github.com:Alberto-Monteiro/coopvote.git
      cd coopvote
      ```

    - Execute o sistema:
      ```bash
      mvn spring-boot:run
      ```

    - A API estará disponível em: [http://localhost:8080](http://localhost:8080).

4. **Testar o Sistema**
   Utilize ferramentas como **Postman** ou **cURL** para interagir com os endpoints da API. A coleção do Postman está
   disponível aqui:
   [Postman Collection - CoopVote](https://www.postman.com/gold-eclipse-197859/workspace/coopvote/collection/15195822-3dfca26f-1c1c-4fa7-b446-fb1a301cbc12?action=share&creator=15195822)

---

## 📌 **Endpoints Mais Importantes**

Abaixo estão os principais endpoints da API **CoopVote**, com exemplos de uso utilizando **cURL** para facilitar sua
interação.

### [Postman Collection - CoopVote](https://www.postman.com/gold-eclipse-197859/workspace/coopvote/collection/15195822-3dfca26f-1c1c-4fa7-b446-fb1a301cbc12?action=share&creator=15195822)

---

### 📋 **Criar uma Nova Pauta**

**Descrição**: Cria uma nova pauta para votação, com a descrição fornecida.

- **Endpoint**: `POST /api/v1/pautas`
- **Exemplo cURL**:
  ```bash
  curl --location 'localhost:8080/api/v1/pautas' \
  --header 'Content-Type: application/json' \
  --data '{
    "descricao": "Votação para aprovação do orçamento anual"
  }'
  ```
- **Campos no Corpo da Requisição**:
    - `descricao` (String): Descrição da pauta, informando o objetivo ou o assunto a ser votado.

---

### ⏳ **Abrir Sessão para Votação**

**Descrição**: Abre uma sessão de votação para uma pauta, com duração definida em minutos.

- **Endpoint**: `POST /api/v1/pautas/{id}/abrir-sessao`
- **Parâmetros**:
    - `tempoSessaoMinutos` (Integer): Duração da sessão em minutos.
- **Exemplo cURL**:
  ```bash
  curl --location --request POST 'localhost:8080/api/v1/pautas/1/abrir-sessao?tempoSessaoMinutos=3'
  ```
    - Substitua `{id}` pelo ID da pauta para a qual deseja abrir a sessão de votação.

---

### 🗳️ **Registrar um Voto**

**Descrição**: Registra um voto de um associado para uma pauta ativa.

- **Endpoint**: `POST /api/v1/votos`
- **Exemplo cURL**:
  ```bash
  curl --location 'localhost:8080/api/v1/votos' \
  --header 'Content-Type: application/json' \
  --data '{
      "pautaId": 1,
      "associadoId": "2",
      "tipoVoto": "SIM"
  }'
  ```
- **Campos no Corpo da Requisição**:
    - `pautaId` (Integer): ID da pauta onde o voto será registrado.
    - `associadoId` (String): ID do associado que está votando.
    - `tipoVoto` (String): Tipo do voto, podendo ser `"SIM"` ou `"NÃO"`.

---

### 🔍 **Dicas de Uso**

1. **Ordem de Operações**:
    - Primeiro, crie uma pauta.
    - Em seguida, abra uma sessão de votação para a pauta.
    - Por último, registre os votos para a pauta com sessão aberta.

2. **Verificação de Respostas**:
    - Certifique-se de verificar as respostas para identificar possíveis erros, como sessões expiradas ou pautas
      inexistentes.

3. **Ferramentas Alternativas**:
    - Além do cURL, você pode testar os endpoints com **Postman** ou qualquer outra ferramenta de cliente REST.

