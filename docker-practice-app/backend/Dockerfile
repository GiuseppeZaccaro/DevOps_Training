# fase di build dell'eseguibile Java (.jar)
FROM maven:3.9-eclipse-temurin-21 AS builder

# cartella di lavoro isolata all'interno del container
WORKDIR /src 
# inseriamo prima le dipendenze in quanto meno soggette a modifiche rispetto al codice sorgente
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
# il flag -DskipTests permette di saltare i test che Spring avvierebbe di default, tentando una connessione al db
RUN mvn clean package -DskipTests

# build immagine finale
FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app
# Creiamo ed impostiamo un utente non-root per sicurezza
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --chown=spring:spring --from=builder /src/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]