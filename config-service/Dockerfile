FROM eclipse-temurin:21-jre
RUN apt install wget

ARG DEPENDENCY=build/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
COPY src/main/resources/config /app/config

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.danyazero.configservice.ConfigServiceApplication"]
