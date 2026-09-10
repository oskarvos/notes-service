# Notes Service

REST-сервис управления заметками.  
Spring Boot 3.5.6 · Java 17 · H2 · Maven · Docker

---

## Требования

- JDK 17+
- Docker 20.10+

Maven устанавливать не нужно — используется Maven Wrapper (`./mvnw`).

---

## Запуск через Docker

```bash
git clone https://github.com/oskarvos/notes-service.git
cd notes-service
docker build -t notes-service:latest .
docker run -d --name notes-service-run -p 8080:8080 notes-service:latest