FROM openjdk:24-slim

RUN apt-get update && apt-get install -y --no-install-recommends\
    wget \
 && rm -rf /var/lib/apt/lists/*

ARG DEPENDENCY=build/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.danyazero.problemservice.ProblemServiceApplication"]
