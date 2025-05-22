FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace/app

COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x gradlew
RUN ./gradlew build -x test
RUN ls -la build/libs/

FROM eclipse-temurin:21-jre
VOLUME /tmp
WORKDIR /app

# Directly copy the JAR file instead of extracting it
COPY --from=build /workspace/app/build/libs/*.jar app.jar

# Use the simpler approach of running the JAR directly
ENTRYPOINT ["java", "-jar", "/app/app.jar"]