.PHONY: build

bootJar:
	./gradlew clean bootJar
	mkdir -p build/dependency
	cd build/dependency && jar xf `ls -t ../libs/*.jar`

build:
	@echo "[${SERVICE_NAME}] Building service..."
	@make bootJar > /dev/null 2>&1
	@echo "[${SERVICE_NAME}] Finished Successfully"

test:
	@echo "[${SERVICE_NAME}] Testing service..."
	@./gradlew test > /dev/null 2>&1
	@echo "[${SERVICE_NAME}] Finished Successfully"