FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace/app

COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x gradlew
RUN ./gradlew build -x test
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

FROM eclipse-temurin:21-jre
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency
RUN mkdir -p /app/lib /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib/ /app/lib/
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.example.systeme_suivi_ticket.SystemeSuiviTicketApplication"]