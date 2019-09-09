FROM maven:3.5-jdk-8-alpine AS builder

# Copy the source code to the container
WORKDIR /app
COPY . .

# Build the jar
RUN ["sh", "gradlew", "shadowJar"]

FROM openjdk:8-jre-alpine

ENV APPLICATION_USER ktor
RUN adduser -D -g '' $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

# Copy the jar to the production image from the builder stage.
COPY --from=builder /app/build/libs/deckboxPreview-*.jar /app/deckboxPreview.jar
WORKDIR /app

# Service must listen to $PORT environment variable.
# This default value facilitates local development.
ENV PORT 8080

CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:InitialRAMFraction=2", "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "deckboxPreview.jar"]