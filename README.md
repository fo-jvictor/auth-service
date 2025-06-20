Este projeto é um serviço de autenticação baseado em JWT desenvolvido em Java e Spring Boot.
Foi criado com fins de estudo e prática, explorando conceitos modernos de autenticação e segurança em APIs REST.

⚠️ Este sistema não contempla todas as etapas de um sistema completo de autenticação em produção e nem é recomendado usar-lo em prod.

- ## 📋 Features implementadas

### 🔐 Autenticação e Autorização
- Autenticação de usuários usando **email e senha**.
- Geração de **Access Token (JWT)** assinado e com expiração curta (3 minutos).
- Geração e controle de **Refresh Token** seguro com:
  - Hash de segurança (salted).
  - Associação com IP de criação.
  - Rotação de tokens (revoga o antigo ao gerar novo).
- Proteção de **rotas privadas** exigindo JWT válido.
- **Token Rotation** automática ao usar refresh tokens.

---

### 🛡️ Segurança
- **Proteção de IP**:
  - Refresh tokens são vinculados ao IP onde foram gerados.
  - Se um refresh token for usado por outro IP, é revogado e o IP é bloqueado.
- **Bloqueio automático de IPs suspeitos**:
  - IP é bloqueado ao usar refresh tokens de outro IP.
- **Rate Limiting**:
  - Limite de requisições por IP em rotas sensíveis evitando ataques de brute force.
- **Auditoria de tentativas de login**:
  - Logs para tentativas bem-sucedidas e mal-sucedidas.
  - Armazena IP, user-agent, resultado da tentativa e erro, se houver.

---

### 📬 Registro de usuários
- Registro de novos usuários com:
  - Envio de **e-mail de confirmação** com token exclusivo.
  - Endpoint para **confirmação do e-mail** via token.
  - Endpoint para **reenvio do token de confirmação** de e-mail.

---

### 🧹 Boas práticas técnicas
- **CSRF desabilitado** (porque o app é stateless — JWT).
- **Configurações de cabeçalho de segurança** como Content-Security-Policy para prevenir XSS básicos.

🛠️ **Tecnologias Utilizadas**
- Spring Boot
- Spring Security
- JavaMailSender
- JWT (com.auth0)
- Bucket4j (rate limiting)
- BCrypt

