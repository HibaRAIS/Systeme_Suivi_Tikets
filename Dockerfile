FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace/app

COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x gradlew
RUN ./gradlew build -x test --stacktrace

FROM eclipse-temurin:21-jre
VOLUME /tmp
WORKDIR /app

# Directly copy the JAR file
COPY --from=build /workspace/app/build/libs/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]