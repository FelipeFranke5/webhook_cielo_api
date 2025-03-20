# Webhook Cielo API

## Descrição
Este projeto simula o funcionamento de um webhook que captura notificações enviadas pela adquirente Cielo e as envia para um tópico no Kafka, além de salvar as informações no banco de dados. Foi estruturado para uma loja que possui cadastro na solução [API E-commerce 3.0](https://docs.cielo.com.br/ecommerce-cielo/docs/sobre-api-ecommerce), inicialmente em ambiente Sandbox.

## Objetivo do Projeto
Aprofundar conhecimentos no funcionamento de microserviços, especificamente na produção de mensagens, com o webhook atuando como producer (recebe uma informação, processa e envia uma mensagem ao Kafka). Além disso, o projeto visa aprofundar a técnica de TDD (Test-Driven-Development) para construir os controllers e services da aplicação.

## Tecnologias Utilizadas
- Java 17
- Spring Boot
- Apache Kafka
- H2 Database

## Dependências
- `org.springframework.kafka:spring-kafka`
- `com.h2database:h2`
- `org.springframework.boot:spring-boot-starter-data-jpa`
- `org.springframework.boot:spring-boot-devtools`

## Executando o Projeto

### Pré-requisitos
- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Docker](https://github.com/docker)
- [Kafka](https://hub.docker.com/r/apache/kafka)

### Passos para Executar

1. Clone o repositório:
    ```bash
    git clone https://github.com/FelipeFranke5/webhook_cielo_api.git
    cd webhook_cielo_api
    ```

2. Execute o Kafka usando Docker:
    ```bash
    docker run -d --name=kafka -p 9092:9092 apache/kafka
    ```

3. Compile e execute a aplicação:
    ```bash
    ./mvnw spring-boot:run
    ```

## Rotas Disponíveis

### POST - /api/webhook
- **Recurso**: `processData`
- **Descrição**: Recebe uma notificação da adquirente Cielo, envia para um tópico no Kafka e salva no banco de dados.
- **Payload de Exemplo**:
    ```json
    {
        "PaymentId": "1cbdc15a-849a-4cee-861e-23b5e2ef7a80",
        "RecurrentPaymentId": "d7366b71-f75d-4ca3-81eb-7623528269b9",
        "ChangeType": 2
    }
    ```
- **Resposta**: HTTP 200 (OK)
- **Documentação Cielo explicando sobre os campos acima**: [Webhook Cielo](https://docs.cielo.com.br/ecommerce-cielo/docs/webhook)

### GET - /api/webhook/{paymentId}
- **Recurso**: `retrieveData`
- **Descrição**: Recebe um `PaymentId` e exibe suas informações.
- **Resposta**: HTTP 200 (OK)
- **Corpo da Resposta**:
    ```json
    {
        "PaymentId": "1cbdc15a-849a-4cee-861e-23b5e2ef7a80",
        "RecurrentPaymentId": "d7366b71-f75d-4ca3-81eb-7623528269b9",
        "ChangeType": 2
    }
    ```

## Licença
Este projeto está licenciado sob a licença MIT. Consulte o arquivo LICENSE para obter mais informações.