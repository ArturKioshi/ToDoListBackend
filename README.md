# 📋 ToDoList API

Projeto de backend para gerenciamento de tarefas (ToDoList) com funcionalidades de autenticação de usuários, envio de e-mails de verificação, recuperação de senha e alteração de senha.

Desenvolvido com **Spring Boot**, utilizando **Spring Security**, **Spring Data JPA** e **Spring Mail**.

## Funcionalidades

- Cadastro de usuários
- Login com autenticação via JWT
- Verificação de conta por e-mail (código de verificação)
- Recuperação de senha via e-mail
- Alteração de senha para usuários logados
- CRUD de tarefas (ToDos)


## Configuração do `application.properties`

Antes de rodar a aplicação, crie o arquivo de configuração `application.properties` no diretório: src/main/resources/


### Exemplo de configuração (valores genéricos):

```properties
# Nome da aplicação
spring.application.name=todolist

# Configuração do Banco de Dados (PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/seu_banco
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

# Configuração JPA (Hibernate)
spring.jpa.hibernate.ddl-auto=update

# JWT (Chave secreta para geração e validação de tokens JWT)
JWT_SECRET=SUA_CHAVE_SECRETA_AQUI

# Configuração de Email (Exemplo com MailTrap ou outro servidor SMTP)
spring.mail.host=smtp.seuprovedor.com
spring.mail.port=587
spring.mail.username=seu_usuario_smtp
spring.mail.password=sua_senha_smtp
spring.mail.protocol=smtp
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Email remetente usado pelo sistema
app.mail.from=noreply@seudominio.com
```


## Configuração do E-mail

No arquivo `application.properties` ou `application.yml`, configure as credenciais do seu servidor SMTP:

### Exemplo para Mailtrap:

```properties
spring.mail.host=smtp.mailtrap.io
spring.mail.port=587
spring.mail.username=SEU_USERNAME
spring.mail.password=SUA_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
``` 

## Executando o Projeto

1. Clone o projeto:

```bash
git clone https://github.com/seu-usuario/todolist-api.git
```

2. Acesse a pasta:

```bash
cd todolist-api
```

3. Configure o `application.properties` com os dados do banco e do SMTP.

4. Rode o projeto:

```bash
./mvnw spring-boot:run
```

## 🖥️ Frontend

Este projeto é apenas backend.  
Futuramente, será integrado com um **frontend em Flutter ou React Native**.

