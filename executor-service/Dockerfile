FROM openjdk:24-slim

RUN apt-get update && apt-get install -y --no-install-recommends\
    git \
    wget \
    flex \
    bison \
    build-essential \
    protobuf-compiler \
    libprotobuf-dev \
    libnl-route-3-dev \
    pkg-config \
    ca-certificates \
    curl \
 && rm -rf /var/lib/apt/lists/*

RUN git clone https://github.com/google/nsjail.git /tmp/nsjail \
 && make -C /tmp/nsjail \
 && mv /tmp/nsjail/nsjail /usr/local/bin/nsjail \
 && rm -rf /tmp/nsjail

ARG DEPENDENCY=build/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.danyazero.executorservice.ExecutorServiceApplication"]
