.PHONY: build

build:
	./gradlew clean bootJar
	mkdir -p build/dependency
	cd build/dependency && jar xf `ls -t ../libs/*.jar`
