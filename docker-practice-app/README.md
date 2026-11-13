# Docker Practice App

App minimale per esercitarsi con i Dockerfile: **React (Vite)** → **Spring Boot (Java 17)** → **PostgreSQL**.
Nessun Dockerfile e nessun docker-compose sono inclusi: li scrivi tu.

```
docker-practice-app/
├── backend/    # Spring Boot + JPA + Postgres (Maven)
└── frontend/   # React + Vite, login page
```

Utente di prova (creato al primo avvio): `admin` / `admin123`

## Cosa installare sul PC

| Strumento | Versione | Serve per |
|---|---|---|
| Docker Desktop (o Docker Engine su Linux) | recente | **Obbligatorio**: build/run dei tuoi Dockerfile e del Postgres |
| JDK | 17 | Solo per provare il backend *senza* Docker |
| Maven | 3.9+ | Solo per provare il backend *senza* Docker |
| Node.js | 20 LTS | Solo per provare il frontend *senza* Docker |

Se vuoi lavorare solo con Docker, bastano **Docker Desktop + VS Code**: JDK, Maven e Node girano dentro i container.

### Estensioni VS Code consigliate
- **Docker** (Microsoft): sintassi, linting e gestione container/immagini
- **Extension Pack for Java** + **Spring Boot Extension Pack**: solo se vuoi editare/eseguire il backend in locale
- **ES7+ React/Redux snippets** (opzionale)

## Prova locale (senza Docker per app, solo Postgres in container)

```bash
# 1) Postgres
docker run -d --name pg -p 5432:5432 \
  -e POSTGRES_DB=appdb -e POSTGRES_USER=appuser -e POSTGRES_PASSWORD=apppass \
  postgres:16

# 2) Backend (http://localhost:8080)
cd backend
mvn spring-boot:run

# 3) Frontend (http://localhost:5173)
cd frontend
npm install
npm run dev
```

Verifica rapida del backend:

```bash
curl http://localhost:8080/actuator/health
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

## Spunti per i tuoi Dockerfile

**Backend** (`backend/Dockerfile`)
- Multi-stage: build con `maven:3.9-eclipse-temurin-17`, runtime con `eclipse-temurin:17-jre`
- Il jar si genera con `mvn package -DskipTests` in `target/auth-service-0.0.1.jar`
- Sfrutta la cache dei layer: copia prima `pom.xml` e scarica le dipendenze (`mvn dependency:go-offline`), poi il codice
- Utente non-root, `EXPOSE 8080`, `HEALTHCHECK` su `/actuator/health`
- Variabili d'ambiente lette da `application.properties`:
  `DB_HOST` (default `localhost`), `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `SERVER_PORT`

**Frontend** (`frontend/Dockerfile`)
- Multi-stage: build con `node:20-alpine` (`npm ci && npm run build`), serve la cartella `dist/` con `nginx:alpine`
- `VITE_API_URL` viene incorporata **a build time**: passala con `ARG VITE_API_URL` + `ENV VITE_API_URL=$VITE_API_URL` prima di `npm run build`
  (attenzione: il browser chiama il backend, quindi l'URL deve essere raggiungibile *dal browser*, es. `http://localhost:8080`, non il nome del servizio Docker)
- Aggiungi un `.dockerignore` (`node_modules`, `dist`)

**Orchestrazione** (`docker-compose.yml`, facoltativo)
- 3 servizi: `db` (postgres:16 con volume), `backend` (`DB_HOST=db`, `depends_on` con healthcheck del db), `frontend`
- Rete custom e volume nominato per i dati di Postgres
