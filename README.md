Este projeto é um serviço de autenticação baseado em JWT desenvolvido em Java e Spring Boot.
Foi criado com fins de estudo e prática, explorando conceitos modernos de autenticação e segurança em APIs REST.

⚠️ Este sistema não contempla todas as etapas de um sistema completo de autenticação em produção e nem é recomendado usar-lo em prod.

✨ Funcionalidades

✅ Registro de usuários com verificação por e-mail (token de confirmação)

✅ Login com geração de JWT e Refresh Token

✅ Renovação de tokens de acesso utilizando refresh token

✅ Proteção contra ataques de força bruta com rate limiting por IP

✅ Validação de tokens via filtro JWT

✅ Hashing seguro de dados sensíveis

🛠️ **Tecnologias Utilizadas**
- Java 17
- Spring Boot
- Spring Security
- JavaMailSender
- JWT (com.auth0)
- Bucket4j (rate limiting)
- BCrypt

