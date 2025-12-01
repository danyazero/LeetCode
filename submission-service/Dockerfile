FROM openjdk:26-ea-slim-trixie

RUN apt-get update && apt-get install -y --no-install-recommends\
    wget \
 && rm -rf /var/lib/apt/lists/*
 
ARG KEYCLOAK_URL
 
RUN openssl s_client -showcerts -connect ${KEYCLOAK_URL}:443 </dev/null 2>/dev/null \
 | openssl x509 -outform PEM > cert.pem
 
RUN keytool -importcert -trustcacerts -noprompt \
   -alias keycloak \
   -file cert.pem \
   -cacerts -storepass changeit

 
ARG DEPENDENCY=build/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.danyazero.submissionservice.SubmissionServiceApplication"]
